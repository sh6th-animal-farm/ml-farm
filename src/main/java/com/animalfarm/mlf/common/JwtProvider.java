package com.animalfarm.mlf.common;

import java.security.Key;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * [JWT 생성 및 검증기]
 * JJWT 라이브러리를 사용하여 실제 토큰 문자열을 생성합니다.
 */
@Component
public class JwtProvider {

	//Authentication 객체를 생성하기 위한 UserDetailsService
	private final UserDetailsService userDetailsService;

	// 토큰 서명에 사용할 비밀키 (외부 유출 절대 금지)
	// STO 서비스 특성상 보안을 위해 충분히 긴 시크릿 키를 사용해야 합니다.
	private String salt = "SmartFarm_STO_Secret_Key_Animal_Farm_MLF_2026_Project_Full_Security";
	private Key secretKey;

	//final 필드인 UserDetailsService를 초기화 하기 위해 생성자를 통해 주입
	@Autowired
	public JwtProvider(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	/*
	 * [토큰 만료 시간 설정]
	 * Access Token: 60분 (요구사항 반영)
	 * Refresh Token: 30일 (Redis 보관 기간과 동일하게 설정)
	 */
	private final long accessTokenExp = 60 * 60 * 1000L; //1시간
	private final long refreshTokenExp = 14L * 24 * 60 * 60 * 1000L; //14일
	

	
	@PostConstruct
	protected void init() {
		//시크릿 키를 HMAC SHA 알고리즘에 적합한 Key 객체로 변환
		this.secretKey = Keys.hmacShaKeyFor(salt.getBytes());
	}

	/**
	 * [Access Token 발급]
	 * @param email 토큰의 주체(Subject)가 될 사용자의 이메일
	 * @param role 사용자 권한 (예: ROLE_USER)
	 */
	public String createAccessToken(String email, String role) {

		//claims: 토큰 안에 담기는 실제 정보 조각들, 토큰의 Payload(내용물) 부분에 저장되는 데이터
		//Jwts: JJWT 라이브러리의 메인 공장입니다. 토큰을 만들거나(Builder), 토큰 안에 뭐가 들었는지 해석하는(Parser)
		Claims claims = Jwts.claims().setSubject(email); // 데이터 조각(Claim) 생성 및 이메일 주입
		claims.put("role", role); // 권한 정보 추가
		Date now = new Date();

		return Jwts.builder()
			.setClaims(claims) // 정보 담기
			.setIssuedAt(now) //발행 시간 기록
			.setExpiration(new Date(now.getTime() + accessTokenExp))
			.signWith(secretKey, SignatureAlgorithm.HS256)
			.compact(); // 최종 문자열로 압축

	}

	/**
	 * [Refresh Token 발급]
	 * Access Token보다 유효기간이 훨씬 길며, Redis에 저장되어 AT 재발급용으로 쓰입니다.
	 */
	public String createRefreshToken(String email) {
		Date now = new Date();
		return Jwts.builder()
			.setSubject(email)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + refreshTokenExp))
			.signWith(secretKey, SignatureAlgorithm.HS256)
			.compact();
	}

	/**
	 * [토큰 데이터 추출]
	 * 암호화된 토큰을 풀어서(Parsing) 그 안에 담긴 사용자 이메일을 꺼냅니다.
	 */
	public String getUserEmail(String token) {
		// Jwts.parserBuilder()에서 토큰을 열어서 내용을 읽음
		return Jwts.parserBuilder().setSigningKey(secretKey).build()
			.parseClaimsJws(token).getBody().getSubject();
	}

	/**
	 * [토큰 유효성 검증]
	 * 토큰이 변조되지 않았는지, 만료되지 않았는지 체크합니다.
	 */
	public boolean validateToken(String token) {

		try {
			// 파싱에 성공하면 유효한 토큰
			// [중요] salt 문자열이 아니라, init()에서 만든 this.secretKey 객체를 넣어야 합니다!
			Jwts.parserBuilder()
				.setSigningKey(this.secretKey)
				.build()
				.parseClaimsJws(token);

			return true;
		} catch (io.jsonwebtoken.security.SignatureException e) {
			System.out.println("DEBUG: 서명이 일치하지 않습니다. (열쇠 불일치)");
		} catch (Exception e) {
			System.out.println("DEBUG: 토큰 검증 실패: " + e.getMessage());
		}
		return false;
	}

	/**
	 * [시큐리티 인증 객체(신분증) 생성]
	 * - 검증이 완료된 토큰을 바탕으로 스프링 시큐리티가 인식할 수 있는 시큐리티용 Authentication 객체를 생성합니다.
	 * - 이 객체가 SecurityContextHolder에 저장되어야만 로그인이 성공한 것으로 간주됩니다.
	 */
	public Authentication getAuthentication(String token) {
		// 1. [사용자 식별자 추출] 토큰의 Subject 영역에서 이메일(혹은 ID)을 꺼냅니다.
		String email = this.getUserEmail(token);

		// 2. [상세 정보 로드] DB에서 해당 유저의 정보와 권한(Role)을 포함한 UserDetails 객체를 가져옵니다.
		// 이 과정에서 실제 존재하는 회원인지 한 번 더 검증됩니다.
		UserDetails userDetails = userDetailsService.loadUserByUsername(email);

		// 3. [인증 토큰 생성] 유저 정보와 권한 목록을 담은 시큐리티 전용 인증 토큰을 반환합니다.
		// 두 번째 인자인 패스워드(Credentials)는 이미 토큰으로 인증되었으므로 빈 문자열로 둡니다.
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

}
