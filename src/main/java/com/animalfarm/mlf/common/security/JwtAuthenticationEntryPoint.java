package com.animalfarm.mlf.common.security;

import java.io.IOException;
import java.io.PrintWriter;

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

		// 1. [중요] 응답 타입을 JSON이 아니라 HTML로 변경합니다.
		response.setContentType("text/html; charset=UTF-8");

		// 2. 상태 코드는 401로 유지 (프론트엔드 로직 호환성 위해)
		// 단, 브라우저가 401을 받으면 자체 에러 페이지를 띄울 수도 있으니
		// 무조건 스크립트를 실행시켜야 한다면 200 OK로 속이는 방법도 있습니다. 
		// 하지만 여기선 정석대로 401을 주되 내용을 HTML로 채웁니다.
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		// 3. 알림창(alert)을 띄우고 로그인 페이지로 튕겨내는 스크립트 작성
		PrintWriter writer = response.getWriter();
		String loginPage = request.getContextPath() + "/auth/login"; // /mlf/auth/login 자동 계산

		writer.println("<script>");
		writer.println("alert('로그인이 필요한 서비스입니다.');");
		writer.println("location.href = '" + loginPage + "';");
		writer.println("</script>");
		writer.flush();
	}
}
