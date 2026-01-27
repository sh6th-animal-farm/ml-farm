package com.animalfarm.mlf.common.http;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
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
				return response.getBody().getPayload();
			}

			throw new RuntimeException("API 응답이 비어있습니다.");

		} catch (HttpClientErrorException e) {
			// 핵심: 증권사가 보낸 에러 JSON 바디를 ApiResponse 객체로 변환
			String errorBody = e.getResponseBodyAsString();
			log.error("[API Error Body] {}", errorBody);

			try {
				// ApiResponse 구조에 맞게 읽어옴
				ApiResponse<?> apiResponse = objectMapper.readValue(errorBody, ApiResponse.class);
				// 증권사가 보낸 "잔액 부족", "이미 소각됨" 등의 실질적 메시지를 던짐
				throw new RuntimeException(apiResponse.getMessage());
			} catch (Exception parseException) {
				// 파싱 실패 시 기본 에러 메시지
				throw new RuntimeException("증권사 서비스 오류가 발생했습니다. (Status: " + e.getRawStatusCode() + ")");
			}

		} catch (Exception e) {
			log.error("[API System Error] URL: {}, Message: {}", url, e.getMessage());
			throw new RuntimeException("시스템 오류로 API를 호출할 수 없습니다.");
		}
	}
}