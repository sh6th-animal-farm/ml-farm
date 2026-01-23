package com.animalfarm.mlf.domain.retry;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ApiRetryService {

	// 일반 로그는 @Slf4j 사용하면 됨
	// 하지만 특정 파일 전용 로거(ApiFailedFileLogger)는 이름을 지정해야 하므로 따로 남겨두거나
	// logback.xml 설정에 따라 log.error를 활용하는 방향으로 가야 함
	private static final org.slf4j.Logger fileLogger = org.slf4j.LoggerFactory.getLogger("ApiFailedFileLogger");

	@Autowired
	private ApiRetryQueueRepository apiRetryQueueMapper;

	@Autowired
	private ExternalApiService externalApiService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * 최초 API 호출 실패 시 재시도 큐에 등록
	 * REQUIRES_NEW: 메인 트랜잭션이 롤백되어도 재시도 기록은 DB에 남아야 함
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void registerRetry(String apiType, Object payloadObj) {
		String payload = "";
		try {
			payload = objectMapper.writeValueAsString(payloadObj);
		} catch (Exception e) {
			log.error("페이로드 직렬화에 실패했습니다.", e);
			payload = String.valueOf(payloadObj);
		}

		ApiRetryQueueDTO retryQueue = new ApiRetryQueueDTO();
		retryQueue.setIdempotencyKey(UUID.randomUUID().toString());
		retryQueue.setApiType(apiType);
		retryQueue.setPayload(payload);
		retryQueue.setRetryCount(1);
		retryQueue.setNextRetryAt(LocalDateTime.now().plusMinutes(2)); // 첫 재시도는 2분 뒤

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
		System.out.println("배치 작업!!");

		if (pendingRetries != null && !pendingRetries.isEmpty()) {
			for (ApiRetryQueueDTO retry : pendingRetries) {
				executeRetry(retry);
			}
		}
	}

	/**
	 * 실제 API 재호출 실행
	 */
	private void executeRetry(ApiRetryQueueDTO retry) {
		// 중복 실행 방지를 위해 상태를 PROCESSING으로 변경

		retry.setStatus("PROCESSING");
		apiRetryQueueMapper.updateStatus(retry);

		try {
			// 외부 API 호출 (멱등성 키 포함)
			externalApiService.execute(retry.getApiType(), retry.getPayload(), retry.getIdempotencyKey());

			// 성공 시 완료 처리
			retry.setStatus("COMPLETED");
			apiRetryQueueMapper.updateStatus(retry);
			log.info("재시도 성공했습니다. Key: {}", retry.getIdempotencyKey());

		} catch (Exception e) {
			// 실패
			log.warn("재시도 실패했습니다. Key: {}, 사유: {}", retry.getIdempotencyKey(), e.getMessage());
			handleFailure(retry);
		}
	}

	/**
	 * 실패 관리 및 지수 백오프(Exponential Backoff) 계산
	 */
	private void handleFailure(ApiRetryQueueDTO retry) {
		int currentCount = retry.getRetryCount();
		int maxRetries = 5; // 최대 재시도 횟수

		if (currentCount >= maxRetries) {
			retry.setStatus("FAILED");
			// TODO: 관리자 알림
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
