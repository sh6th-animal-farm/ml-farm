package com.animalfarm.mlf.common;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

//Redis를 사용하여 데이터를 저장, 조회, 삭제하는 실무 유틸리티입니다.
/**
 * [Redis 조작 유틸리티]
 * 토큰의 저장 기간(TTL) 관리 및 블랙리스트 로직을 전담합니다.
 */
@Component
public class RedisUtil {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	/**
	 * [Refresh Token 저장]
	 * @param email 사용자 식별값 (Key의 일부로 사용)
	 * @param refreshToken 저장할 리프레시 토큰 값
	 * 요구사항: TTL 30일 (TimeUnit.DAYS 사용)
	 */
	//토큰 발급 시
	public void saveRefreshToken(String email, String refreshToken) {
		String key = "RT:" + email; //key 형식 예시 -> RT:user@naver.com
		redisTemplate.opsForValue().set(key, refreshToken, 30, TimeUnit.DAYS); //30일
	}

	/**
	 * [데이터 조회]
	 * @param key 조회할 키 (RT 검증 시 사용)
	 * @return 저장된 값
	 */
	//토큰 검증 시
	public String getData(String key) {
		return (String)redisTemplate.opsForValue().get(key);
	}

	/**
	 * [블랙리스트 등록]
	 * 로그아웃하거나 토큰이 탈취되었을 때, 해당 토큰을 '사용 불가' 상태로 등록합니다.
	 * @param accessToken 무효화할 엑세스 토큰
	 * @param minutes 해당 토큰이 만료될 때까지 남은 분(min) 단위 시간
	 */
	public void setBlackList(String accessToken, String msg, long minutes) {
		String key = "BL:" + accessToken; //BL은 blackList의 약자
		redisTemplate.opsForValue().set(key, msg, minutes, TimeUnit.MINUTES);
	}

	/**
	 * [블랙리스트 여부 확인]
	 * 요청으로 들어온 토큰이 블랙리스트에 있는지 체크합니다.
	 */
	public boolean isBlackList(String accessToken) {
		// 해당 키가 존재하면(true) 블랙리스트에 등록된 '만료된토큰'임
		return Boolean.TRUE.equals(redisTemplate.hasKey("BL:" + accessToken));
	}

	/**
	 * [데이터 삭제]
	 * 로그아웃 시 Redis에 저장된 Refresh Token을 즉시 제거합니다.
	 */
	public void deleteData(String key) {
		redisTemplate.delete(key);
	}

}
