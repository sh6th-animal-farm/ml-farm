package com.animalfarm.mlf.domain.retry;

// 외부 시스템(증권사 등)과 통신하는 서비스의 인터페이스
public interface ExternalApiService {
	/**
	* API를 실제로 호출합니다.
	* @param apiType API 종류
	* @param payload 전송 데이터
	* @param idempotencyKey 중복 방지 키 (증권사 서버에서 이 키를 보고 중복 처리를 막음)
	*/
	void execute(String apiType, String payload, String idempotencyKey) throws Exception;
}
