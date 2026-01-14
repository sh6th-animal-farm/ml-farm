package com.animalfarm.mlf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * [스프링 시큐리티 설정 클래스]
 * - 프로젝트 전체의 보안 규칙(어떤 페이지를 열고 막을지)을 정의합니다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	/**
	 * [비밀번호 암호화 빈 등록]
	 * - 로그인 시 비밀번호를 안전하게 비교하기 위해 사용합니다.
	 */
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * [1. 정적 리소스 보안 예외 설정]
	 * - 보안 필터를 아예 거치지 않아도 되는 경로들을 지정합니다.
	 * - Swagger UI 관련 리소스들이 여기에 해당합니다.
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(
			"/v2/api-docs", // Swagger용 데이터 경로
			"/swagger-resources/**", // Swagger 리소스
			"/swagger-ui.html", // Swagger 메인 화면
			"/webjars/**", // Swagger에 필요한 자바스크립트/CSS 라이브러리
			"/swagger/**");
	}

	/**
	 * [2. HTTP 보안 상세 설정]
	 * - 실제 API 요청에 대한 권한을 설정합니다.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			// 1) REST API이므로 CSRF 보안은 끕니다. (Postman 테스트를 위해 필수)
			.csrf().disable()

			// 2) JWT를 사용하므로 세션을 사용하지 않도록 설정 (Stateless)
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

			.and()

			// 3) 요청에 대한 권한 체크
			.authorizeRequests()
			.antMatchers("/api/user/login", "/api/user/refresh").permitAll() // 로그인, 재발급은 누구나 접근 가능
			.anyRequest().authenticated(); // 그 외 모든 요청은 인증(토큰)이 있어야 함

		// 4) 나중에 여기에 .addFilterBefore(new JwtAuthenticationFilter(...))를 추가할 예정입니다.
	}
}