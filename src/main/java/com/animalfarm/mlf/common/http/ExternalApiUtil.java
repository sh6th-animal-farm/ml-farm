package com.animalfarm.mlf.common.http;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalApiUtil {

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;


	/**
	 * 기본 호출 메서드 (멱등성 키가 없는 경우)
	 */
	public <T> T callApi(String url, HttpMethod method, Object body,
		ParameterizedTypeReference<ApiResponse<T>> responseType) {
		return callApi(url, method, body, responseType, null); // 마지막 인자로 null 전달
	}

	/**
	 * 외부 API 호출 공통 메서드
	 * @param url 호출 주소
	 * @param method HttpMethod (GET, POST 등)
	 * @param body 요청 바디 (없으면 null)
	 * @param responseType ParameterizedTypeReference (제네릭 타입 보존용)
	 * @param idempotencyKey 증권사 전송용 멱등성 키 (null이면 헤더에 추가 안 함)
	 */
	public <T> T callApi(String url, HttpMethod method, Object body,
		ParameterizedTypeReference<ApiResponse<T>> responseType, String idempotencyKey) {

		// 1. 헤더 설정 (JSON 통신 표준화)
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
	    
		// 멱등성 키가 존재할 때만 헤더에 추가
		if (idempotencyKey != null && !idempotencyKey.isEmpty()) {
			headers.set("X-Idempotency-Key", idempotencyKey);
		}

		HttpEntity<Object> entity = new HttpEntity<>(body, headers);

		try {
			// 2. API 호출
			ResponseEntity<ApiResponse<T>> response = restTemplate.exchange(url, method, entity, responseType);

			// 3. 성공 응답 처리 (2xx)
			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				log.info("[API Success] URL: {}, Msg: {}", url, response.getBody().getMessage());
				return response.getBody().getPayload(); // 실제 데이터, 빈 리스트, Null 중 하나 반환
			}

			throw new RuntimeException("API 응답이 비어있습니다.");

		} catch (HttpStatusCodeException e) {
			// 4XX, 5XX 에러 캐치
			// 핵심: 증권사가 보낸 에러 JSON 바디를 ApiResponse 객체로 변환
			String errorBody = e.getResponseBodyAsString();
			log.error("[API Fail] Status: {}, Body: {}", e.getStatusCode(), errorBody);

			String finalMessage = "서비스 오류가 발생했습니다."; // 기본값

			try {
				// 파싱 시도
				ApiResponse<?> apiResponse = objectMapper.readValue(errorBody, ApiResponse.class);
				finalMessage = apiResponse.getMessage();
			} catch (Exception parseException) {
				// 파싱 실패 시 에러 메시지 직접 추출
				log.warn("JSON 파싱 실패, 직접 문자열 추출 시도");
				if (errorBody.contains("\"message\":\"")) {
					finalMessage = errorBody.split("\"message\":\"")[1].split("\",\"")[0];
				}
			}

			// 추출된 메시지 정제 (MyBatis 로그 제거)
			if (finalMessage != null && finalMessage.contains("ERROR:")) {
				String[] parts = finalMessage.split("ERROR:");
				finalMessage = parts[parts.length - 1].split("\n")[0].replace("\"", "").trim();
			}

			throw new RuntimeException(finalMessage);

		} catch (Exception e) {
			log.error("[API System Error] URL: {}, Message: {}", url, e.getMessage());
			throw new RuntimeException("시스템 오류로 API를 호출할 수 없습니다.");
		}
	}
	
	public <T> T callExternalApi(String url, HttpMethod method, Object body,
			ParameterizedTypeReference<T> responseType, Map<String, String> customHeaders) {

		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);
		    
		    if (customHeaders != null) {
		        customHeaders.forEach(headers::set);
		    }

		    HttpEntity<Object> entity = new HttpEntity<>(body, headers);

		    try {
		        // ApiResponse<T>가 아닌 일반 T 타입으로 받음
		        ResponseEntity<T> response = restTemplate.exchange(url, method, entity, responseType);
		        return response.getBody();
		    } catch (HttpStatusCodeException e) {
		        log.error("[External API Fail] Status: {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
		        throw new RuntimeException("외부 서비스 호출 중 오류가 발생했습니다.");
		    } catch (Exception e) {
		        log.error("[External API System Error] Message: {}", e.getMessage());
		        throw new RuntimeException("시스템 오류가 발생했습니다.");
		    }
		}
}
