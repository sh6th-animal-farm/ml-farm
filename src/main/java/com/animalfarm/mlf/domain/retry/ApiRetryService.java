package com.animalfarm.mlf.domain.retry;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiRetryService {

	// 일반 로그는 @Slf4j 사용하면 됨
	// 하지만 특정 파일 전용 로거(ApiFailedFileLogger)는 이름을 지정해야 하므로 따로 남겨두거나
	// logback.xml 설정에 따라 log.error를 활용하는 방향으로 가야 함
	private static final org.slf4j.Logger fileLogger = org.slf4j.LoggerFactory.getLogger("ApiFailedFileLogger");

	private final ApiRetryQueueRepository apiRetryQueueMapper;
	private final ApiRetryProcessor apiRetryProcessor;
	private final ObjectMapper objectMapper;
	
	// 강황증권 API 서버 주소
	@Value("${api.kh-stock.url}")
	private String KH_BASE_URL;

	/**
	 * 최초 API 호출 실패 시 재시도 큐에 등록
	 * REQUIRES_NEW: 메인 트랜잭션이 롤백되어도 재시도 기록은 DB에 남아야 함
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void registerRetry(ApiType apiType, Object payloadObj, Object[] params, String idempotencyKey) {
		String payload = "";
		try {
			payload = objectMapper.writeValueAsString(payloadObj);
		} catch (Exception e) {
			log.error("페이로드 직렬화에 실패했습니다.", e);
			payload = String.valueOf(payloadObj);
		}

		// Object[]를 JSON으로 저장했다가 다시 읽으면, Long이 Integer로 바뀌는 등 형변환 에러 발생 가능
		// STring[]으로 변환해서 저장
		List<String> stringParams = Arrays.stream(params)
			.map(String::valueOf)
			.collect(Collectors.toList());
		String queryParams = "";
		try {
			queryParams = objectMapper.writeValueAsString(stringParams);
		} catch (Exception e) {
			log.error("쿼리 파라미터 직렬화 실패", e);
		}

		ApiRetryQueueDTO retryQueue = ApiRetryQueueDTO.builder()
			.idempotencyKey(idempotencyKey)
			.apiType(apiType.name())
			.payload(payload)
			.query(queryParams)
			.idempotencyKey(idempotencyKey)
			.retryCount(1)
			.nextRetryAt(LocalDateTime.now().plusMinutes(2))
			.build();

		try {
			apiRetryQueueMapper.insert(retryQueue);
			log.info("재시도를 요청하는 API: {}, IdempotencyKey: {}", apiType,
				retryQueue.getIdempotencyKey());
		} catch (Exception e) {
			// DB 저장 실패 시 로컬 파일에 로그를 남겨 유실 방지
			log.error("ApiRetryQueue 테이블에 입력을 실패했습니다. 로컬 로그에 기록을 남깁니다.", e);
			fallbackToLocalLog(retryQueue);
		}
	}

	/**
	 * DB 저장 실패 시 로컬 파일 시스템에 로그를 기록
	 * DB 서버 장애 시에도 API 요청 재시도 데이터가 유실되는 것을 방지하기 위함
	 */
	private void fallbackToLocalLog(ApiRetryQueueDTO retryQueue) {
		// 직접 파일에 쓰거나 로거를 사용할 수 있음
		// 로거 설정이 불확실할 경우를 대비해 직접 파일 쓰기(FileWriter) 방식을 병행함
		try {
			String json = objectMapper.writeValueAsString(retryQueue);
			fileLogger.error(json);
			// 로거 설정과 관계없이 "error_api_failed.log" 파일에 직접 기록
			// 'true' 파라미터는 파일 끝에 내용을 덧붙이는(append) 모드임
			try (BufferedWriter writer = new BufferedWriter(new FileWriter("error_api_failed.log", true))) {
				writer.write(json);
				writer.newLine();
			}
			log.info("DB 저장 실패로 인해 로컬 로그 파일에 비상 기록되었습니다. 키: {}", retryQueue.getIdempotencyKey());
		} catch (IOException ioException) {
			log.error("CRITICAL: 로컬 로그 파일 기록마저 실패했습니다. 데이터 유실 위험.", ioException);
		}
	}

	/**
	 * 배치 스케줄러: 1분마다 재시도 대상이 있는지 확인하고 실행
	 */
	@Scheduled(fixedDelay = 60000)
	public void processRetries() {
		List<ApiRetryQueueDTO> pendingRetries = apiRetryQueueMapper.selectPendingRetries();

		if (pendingRetries != null && !pendingRetries.isEmpty()) {
			for (ApiRetryQueueDTO retry : pendingRetries) {
				apiRetryProcessor.executeRetry(retry, this);
			}
		}
	}

	/**
	 * 실패 관리 및 지수 백오프(Exponential Backoff) 계산
	 */
	public void handleFailure(ApiRetryQueueDTO retry) {
		int currentCount = retry.getRetryCount();
		int maxRetries = 5; // 최대 재시도 횟수

		if (currentCount >= maxRetries) {
			retry.setStatus("FAILED");
			log.error("최종 재시도에 실패 했습니다. Key: {}", retry.getIdempotencyKey());
		} else {
			retry.setStatus("PENDING");
			int nextCount = currentCount + 1;
			retry.setRetryCount(nextCount);

			// 지수 백오프 계산
			long delayMinutes = (long)Math.pow(2, currentCount); // 1->2, 2->4, 3->8...
			retry.setNextRetryAt(LocalDateTime.now().plusMinutes(delayMinutes));
		}

		apiRetryQueueMapper.updateStatusAndNextRetry(retry);
	}
}
