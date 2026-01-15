package com.animalfarm.mlf.common.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * [401 Unauthorized 에러 처리기]
 * - 인증되지 않은 사용자가 보호된 리소스에 접근할 때 호출됩니다.
 * - 스마트팜 STO 플랫폼에서 로그인이 필요한 서비스임을 클라이언트에게 명확히 알립니다.
 * - 스프링 시큐리티의 기본 에러 페이지 대신 커스텀 JSON 응답을 보냅니다.
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		org.springframework.security.core.AuthenticationException authException) throws IOException, ServletException {

		log.error("인증 실패: 로그인이 필요한 서비스입니다. - {}", authException.getMessage());

		// [보안 응답 설정] 클라이언트에게 401 에러를 JSON 형태로 전달합니다.
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().println("{ \"error\": \"401\", \"message\": \"로그인이 필요한 서비스입니다.\" }");

	}
}
