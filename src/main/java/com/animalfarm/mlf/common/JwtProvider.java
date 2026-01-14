package com.animalfarm.mlf.common;

import java.security.Key;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;

/**
 * [JWT 생성 및 검증기]
 * JJWT 라이브러리를 사용하여 실제 토큰 문자열을 생성합니다.
 */
@Component
public class JwtProvider {

	// 토큰 서명에 사용할 비밀키 (외부 유출 절대 금지)
	// STO 서비스 특성상 보안을 위해 충분히 긴 시크릿 키를 사용해야 합니다.
	private String salt = "SmartFarm_STO_Secret_Key_Animal_Farm_MLF_2026_Project_Full_Security";
	private Key secretKey;

	/**
	 * [토큰 만료 시간 설정]
	 * Access Token: 60분 (요구사항 반영)
	 * Refresh Token: 30일 (Redis 보관 기간과 동일하게 설정)
	 */
	private final long accessTokenExp = 60 * 60 * 10000L; //1시간
	private final long refreshTokenExp = 30L * 24 * 60 * 60 * 1000L; //30일

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
	public String createRegreshToken(String email) {
		Date now = new Date();
		return Jwts.builder()
			.setSubject(email)
			.setIssuedAt(now)
			.setExpiration(now)
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
			Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			//서명이 잘못되었거나 형식이 틀린 경우
		} catch (ExpiredJwtException e) {
			//유효시간이 만료된 경우
		} catch (UnsupportedJwtException | IllegalArgumentException e) {
			// 기타 지원하지 않는 토큰인 경우
		}
		return false;
	}

}
