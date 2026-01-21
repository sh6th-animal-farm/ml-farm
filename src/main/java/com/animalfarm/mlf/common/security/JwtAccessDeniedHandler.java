package com.animalfarm.mlf.common.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * [403 Forbidden 처리기]
 * - 로그인은 했지만 해당 리소스에 접근할 권한이 없는 경우(예: 관리자 페이지 접근) 호출됩니다.
 * - 자산 관리 권한이 없는 투자자의 접근을 차단하고 JSON 메시지를 보냅니다.
 */
@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
	@Override

	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException, ServletException {

		log.error("권한 부족: 접근 권한이 없습니다. - {}", accessDeniedException.getMessage());

		// [보안 응답 설정] 클라이언트에게 403 에러를 JSON 형태로 전달합니다.
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.getWriter().println("{ \"error\": \"403\", \"message\": \"해당 리소스에 접근할 권한이 없습니다.\" }");

	}

}
