package com.animalfarm.mlf.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [토큰 응답 전송 객체]
 * 로그인에 성공하거나 토큰 재발급 요청이 정상적으로 처리되었을 때,
 * 클라이언트(프론트엔드/포스트맨)에게 전달할 토큰 정보들을 담는 바구니입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDTO {

	/**
	 * [액세스 토큰]
	 * - 유효시간: 60분 (요구사항 반영)
	 * - 역할: 실제 API 요청 시 HTTP 헤더(Authorization)에 담아 보내는 짧은 수명의 인증 티켓
	 */
	private String accessToken;

	/**
	 * [리프레시 토큰]
	 * - 유효시간: 30일 (요구사항 반영)
	 * - 역할: 액세스 토큰이 만료되었을 때 새로운 액세스 토큰을 발급받기 위한 "교환권"
	 * - 보안을 위해 Redis에 저장되어 대조됩니다.
	 */
	private String refreshToken;

}
