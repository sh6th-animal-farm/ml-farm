package com.animalfarm.mlf.domain.retry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

// 외부 시스템(증권사 등)과 통신하는 서비스의 인터페이스
@Service
@Slf4j
public class ExternalApiService {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${external.api.base-url}")
	private String baseUrl;

	/**
	* API를 실제로 호출합니다
	* @param apiType API 종류
	* @param payload 전송 데이터
	* @param idempotencyKey 중복 방지 키 (증권사 서버에서 이 키를 보고 중복 처리를 막음)
	*/
	void execute(ApiType type, String payload, String idempotencyKey, Object... pathVars) throws Exception {
		String uri = type.getFullUri(pathVars);

		String fullUrl = baseUrl + uri;

		log.info("외부 API 호출 시작: [{}], URL: {}", type.getDescription(), fullUrl);

		// 2. 헤더 설정 (JSON 타입 및 멱등성 키 추가)
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-Idempotency-Key", idempotencyKey); // 증권사 전송용 헤더

		HttpEntity<String> entity = new HttpEntity<>(payload, headers);

		try {
			log.info(">>> API Call: [{}], URL: {}", type.name(), fullUrl);

			// 3. 실제 전송 (POST 기준)
			ResponseEntity<String> response = restTemplate.postForEntity(fullUrl, entity, String.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				log.info("<<< API Success: {}", response.getBody());
			} else {
				// 400, 500 에러 발생 시 예외를 던져 재시도 큐가 작동하게 함
				throw new Exception("API Response Error: " + response.getStatusCode());
			}
		} catch (Exception e) {
			log.error("<<< API Connection Failed: {}", e.getMessage());
			// 예외를 밖으로 던져야 ApiRetryService에서 캐치해서 재시도 처리를 합니다.
			throw e;
		}

	};
}
