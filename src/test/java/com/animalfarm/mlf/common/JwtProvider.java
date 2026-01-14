package com.animalfarm.mlf.common;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {

	//토큰을 만드는 것뿐만 아니라
	//나중에 필터에서 사용할 검증 및 정보 추출 기능까지 포함된 완성본입니다

	// STO 서비스 특성상 보안을 위해 충분히 긴 시크릿 키를 사용해야 합니다.
	private String salt = "SmartFarm_STO_Project_Secret_Key_For_AnimalFarm_MLF_2026";
	private Key secretKey;

	//토큰 유효 시간 설정
	private final long acceessTokenValidTime = 60 * 60 * 10000L; //1시간
	private final long refreshTokenValidTime = 30 * 24 * 60 * 60 * 1000L; //30일

	@PostConstruct
	protected void init() {
		//시크릿 키를 HMAC SHA 알고리즘에 적합한 Key 객체로 변환
		secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
	}

	//Access Token 생성
	public String createAccessToken(String email, String role) {

		//laims: 토큰 안에 담기는 실제 정보 조각들, 토큰의 Payload(내용물) 부분에 저장되는 데이터
		//Jwts: JJWT 라이브러리의 메인 공장입니다. 토큰을 만들거나(Builder), 토큰 안에 뭐가 들었는지 해석하는(Parser)
		Claims claims = Jwts.claims().setSubject(email);
		claims.put("role", role);
		Date now = new Date();

		return Jwts.builder()
			.setSubject(email)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + refreshTokenValidTime))
			.signWith(secretKey, SignatureAlgorithm.HS256)
			.compact();

	}

	/**
	 * Refresh Token 생성: 보안을 위해 이메일 정보만 최소한으로 담음
	 */
	public String createRegreshToken(String email) {
		Date now = new Date();
		return Jwts.builder()
			.setSubject(email)
			.setIssuedAt(now)
			.setExpiration(now)
			.setExpiration(new Date(now.getTime() + refreshTokenValidTime))
			.signWith(secretKey, SignatureAlgorithm.HS256)
			.compact();
	}

	/**
	 * 토큰에서 사용자 이메일(Subject) 추출
	 */
	public String getUserEmail(String token) {
		// Jwts.parserBuilder()에서 토큰을 열어서 내용을 읽음
		return Jwts.parserBuilder().setSigningKey(secretKey).build()
			.parseClaimsJws(token).getBody().getSubject();
	}

	/**
	 * 토큰의 유효성 및 만료 여부 확인
	 */
	public boolean validateToken(String token) {
		try {
			// 토큰을 파싱했을 때 예외가 발생하지 않고, 만료 시간이 현재보다 미래라면 true
			Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
			return !claims.getBody().getExpiration().before(new Date());
		} catch (Exception e) {
			// 서명이 일치하지 않거나, 만료된 토큰인 경우 false 반환
			return false;
		}
	}

}
