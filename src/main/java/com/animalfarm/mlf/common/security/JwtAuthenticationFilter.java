package com.animalfarm.mlf.common.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.animalfarm.mlf.common.JwtProvider;
import com.animalfarm.mlf.common.RedisUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * [JWT 인증 필터]
 * - 모든 HTTP 요청을 가로채서 헤더의 JWT 유효성을 검증합니다.
 * - Redis를 조회해 로그아웃 여부를 확인한 뒤 인증된 사용자에게만 접근을 허용합니다.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;
	private final RedisUtil redisUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		// 1. [토큰 추출] Authorization 헤더에서 토큰을 꺼내옵니다.
		String token = resolveToken(request);

		// 2. [검증 단계] 토큰이 존재하고 서명이 유효한지 확인합니다.
		if (token != null && jwtProvider.validateToken(token)) {
			// 3. [블랙리스트 확인] Redis를 조회해 로그아웃된 토큰인지 확인합니다.
			if (!redisUtil.isBlackList(token)) {
				// 4. [인증 승인] 이상이 없으면 시큐리티 세션에 인증 객체를 저장합니다.
				Authentication auth = jwtProvider.getAuthentication(token);
				SecurityContextHolder.getContext().setAuthentication(auth);
				log.info("인증 성공: {}", auth.getName());
			}
		}

		// 5. [필터 통과] 다음 단계로 요청을 넘깁니다.
		filterChain.doFilter(request, response);

	}

	/**
	 * [헤더 파싱] "Bearer "를 제외한 순수 토큰값만 추출합니다.
	 */
	private String resolveToken(HttpServletRequest request) {
		// 1. 요청 헤더에서 "Authorization" 항목의 값을 가져옵니다.
		String bearerToken = request.getHeader("Authorization");

		// 2. 가져온 값이 비어있지 않은지, 그리고 "Bearer "로 시작하는지 확인합니다.
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			// 3. 앞의 "Bearer " (7글자)를 잘라내고 순수 JWT만 반환합니다.
			return bearerToken.substring(7);
		}

		// 4. 조건에 맞지 않으면 토큰이 없는 것으로 간주하고 null을 반환합니다.
		return null;
	}
}
