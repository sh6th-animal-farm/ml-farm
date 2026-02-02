package com.animalfarm.mlf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.animalfarm.mlf.common.JwtProvider;
import com.animalfarm.mlf.common.RedisUtil;
import com.animalfarm.mlf.common.security.JwtAccessDeniedHandler;
import com.animalfarm.mlf.common.security.JwtAuthenticationEntryPoint;
import com.animalfarm.mlf.common.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtProvider jwtProvider;
	private final RedisUtil redisUtil;
	private final JwtAuthenticationEntryPoint entryPoint;
	private final JwtAccessDeniedHandler accessDeniedHandler;

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {

		// 정적 리소스(CSS, JS, 이미지)와 Swagger는 보안 필터 자체를 거치지 않음 (성능 최적화)
		return (web) -> web.ignoring()
			.antMatchers("/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs/**", "/webjars/**",
				"/resources/**", "/favicon.ico", "/error");

	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// 1. 기본 설정 (JWT 사용을 위해 세션/CSRF/FormLogin 비활성화)
		http
			.httpBasic().disable()
			.csrf().disable()
			.cors().configurationSource(corsConfigurationSource())
			.and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and();

		// 2. 권한 규칙 설정
		http.authorizeRequests()
			// =========================================================
			// [A] 누구나 접근 가능 (Public) - 로그인 불필요
			// =========================================================
			// 1. 인증/로그인 관련 API
			.antMatchers("/api/auth/**").permitAll()

			// 2. 화면(View) 페이지 [이미지 명세 반영]
			// 메인, 로그인/가입, 약관, 공지사항, 프로젝트 목록/상세, 토큰 목록/상세
			.antMatchers("/", "/main", "/auth/**", "/policy", "/notice/list").permitAll()
			.antMatchers("/project/**").permitAll() // /project/list/fragment 포함됨
			.antMatchers("/token/**").permitAll() // 토큰 거래소 상세 화면
			.antMatchers("/token").permitAll() // 토큰 거래소 화면
			.antMatchers("/carbon/**").permitAll() // 탄소 마켓 화면
			.antMatchers("/mypage/**").permitAll() // 마이페이지 화면

			// 3. 조회 전용 API (GET 요청만 허용) [API 명세 반영]
			// 프로젝트 조회, 사진 조회, 위치 조회 등
			.antMatchers(HttpMethod.GET, "/api/project/**").permitAll()
			// 토큰 시세, 차트, 호가창 조회 등
			.antMatchers(HttpMethod.GET, "/api/token/**").permitAll()
			.antMatchers(HttpMethod.GET, "/api/token").permitAll()
			.antMatchers(HttpMethod.GET, "/api/accounts/**").permitAll()

			// =========================================================
			// [B] 권한별 접근 제어 (Role Based)
			// =========================================================
			// 4. 관리자(ADMIN) 전용
			// 어드민 페이지 전체, 프로젝트 생성/수정/삭제 API
			.antMatchers("/admin/**").hasRole("ADMIN")
			.antMatchers("/api/project/insert", "/api/project/update").hasRole("ADMIN")
			.antMatchers(HttpMethod.DELETE, "/api/project/picture/**").hasRole("ADMIN")

			// 5. 기업(ENTERPRISE) 전용 [탄소 마켓]
			// 탄소 마켓 관련 API 전체
			.antMatchers("/api/carbon/**").hasRole("ENTERPRISE")
			// =========================================================
			// [C] 로그인한 사용자 공통 (Authenticated)
			// =========================================================
			// 6. 프로젝트 관련 액션 (청약, 좋아요, 좋아요 취소)
			.antMatchers("/api/project/subscription", "/api/project/favorite", "/api/project/favorite/**",
				"/api/project/confirm-user")
			.authenticated()

			// 7. 토큰 거래 액션 (주문, 취소) 및 계좌 잔액 조회
			.antMatchers("/api/token/order/**", "/api/token/order-cancel/**").authenticated()
			//.antMatchers("/api/accounts/**").authenticated() // 잔액 조회는 본인만!

			// 8. 마이페이지 전체
			.antMatchers("/api/mypage/**").authenticated()

			// 9. 그 외 정의되지 않은 모든 요청은 로그인 필요
			.anyRequest().authenticated();

		// 3. 에러 핸들링 및 필터 추가
		http
			.exceptionHandling()
			.authenticationEntryPoint(entryPoint) // 401 (로그인 필요)
			.accessDeniedHandler(accessDeniedHandler) // 403 (권한 부족 - 예: 일반유저가 탄소페이지 접근)
			.and()
			.addFilterBefore(new JwtAuthenticationFilter(jwtProvider, redisUtil),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration configuration = new CorsConfiguration();
		// 프론트엔드 도메인 허용
		configuration.addAllowedOrigin("https://mlfarm.3jun.store");
		configuration.addAllowedOrigin("http://localhost:9999");
		configuration.addAllowedOrigin("http://localhost:5173");
		configuration.addAllowedMethod("*"); // GET, POST, PUT, DELETE 등 모두 허용
		configuration.addAllowedHeader("*"); // 모든 헤더 허용
		configuration.setAllowCredentials(true); // 쿠키/인증정보 포함 허용

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;

	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}