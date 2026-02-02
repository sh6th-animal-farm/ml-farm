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

	// [설정] true: 테스트 모드 (팀원 협업용), false: 실전 모드 (보안 강화)
	private static final boolean IS_TEST_MODE = true;

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		// Swagger와 정적 리소스는 여기서 한 번에 '무시' 처리 (가장 깔끔)
		return (web) -> web.ignoring()
			.antMatchers("/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs/**", "/webjars/**",
				"/resources/**");
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// [서버의 정체성 선언: JWT 기반 무상태(Stateless) 서버]
		// 이 설정이 완료되어야 스프링 시큐리티가 세션을 버리고 토큰 기반으로 작동합니다.
		http
			// [팝업창 차단] 브라우저의 기본 로그인 ID/PW 팝업창을 사용하지 않음
			.httpBasic().disable()

			// [CSRF 방어 해제] 세션/쿠키를 사용하지 않으므로 CSRF 공격으로부터 자유로움 (REST API 최적화)
			.csrf().disable()

			// [cors 설정 추가]
			.cors().configurationSource(corsConfigurationSource())
			.and()

			// [무상태성(Stateless) 강제] 가장 핵심 설정!
			// 서버는 세션을 생성하지도 않고, 이미 존재하는 세션을 사용하지도 않음
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

			.and(); // 설정 체인을 이어가기 위한 구분자

		// [인가 정책 설정]
		http.authorizeRequests()
			// 1. 공용 API (Swagger permitAll 중복 제거됨)
			.antMatchers("/api/auth/**").permitAll()

			// 2. 요청하신 테스트 전용 경로 (권한 로직 실전 테스트용)
			.antMatchers("/api/test/carbon").hasRole("ENTERPRISE") // ENTERPRISE 권한 필요
			.antMatchers("/api/test/admin").hasRole("ADMIN") // ADMIN 권한 필요
			.antMatchers("/api/test/auth").authenticated(); // 로그인만 하면 허용

		if (IS_TEST_MODE) {
			// 3-A. [테스트 모드] 위에서 정의하지 않은 나머지 모든 요청은 팀원들을 위해 허용
			http.authorizeRequests().anyRequest().permitAll();
		} else {
			// 3-B. [실전 모드] 실제 서비스 운영을 위한 엄격한 권한 설정
			http.authorizeRequests()
				// 1. 누구나 접근 가능한 경로 (로그인, 회원가입, 단순 조회)
				.antMatchers("/api/auth/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/carbon/**", "/api/project/**", "/api/token/**").permitAll()

				// 2. 관리자 전용 (이미지의 admin 태그 반영)
				.antMatchers("/api/project/insert", "/api/project/update").hasRole("ADMIN")

				// 3. 기업 회원 전용 (탄소 배출권 마켓 관련)
				.antMatchers("/api/carbon/order/**", "/api/carbon/order-verification").hasRole("ENTERPRISE")

				// 4. 로그인한 유저 공통 (토큰 거래, 마이페이지, 청약)
				.antMatchers("/api/token/order/**", "/api/my/**", "/api/project/subscription").authenticated()

				.anyRequest().authenticated();
		}

		http
			// 4. 예외 처리 핸들러 등록
			.exceptionHandling()
			.authenticationEntryPoint(entryPoint)
			.accessDeniedHandler(accessDeniedHandler)
			.and()

			// 5. JWT 필터 배치 (UsernamePasswordAuthenticationFilter 앞에 배치)
			.addFilterBefore(new JwtAuthenticationFilter(jwtProvider, redisUtil),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// 허용할 도메인 (배포 주소 및 로컬 주소)
		configuration.addAllowedOrigin("https://mlfarm.3jun.store");
		configuration.addAllowedOrigin("http://localhost:9999"); // 로컬 테스트용 프론트 주소
		configuration.addAllowedOrigin("http://localhost:5173"); // Vite 기본 주소

		// 허용할 HTTP 메서드
		configuration.addAllowedMethod("*");

		// 허용할 헤더
		configuration.addAllowedHeader("*");

		// 자격 증명(쿠키, 인증 헤더 등) 허용
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	// 회원 가입시 비밀번호 암호화 하는 코드
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}