package com.animalfarm.mlf.common;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	/**
	 * 데이터 저장 (유효시간 설정)
	 * @param key 저장할 키
	 * @param value 저장할 값 (Refresh Token 등)
	 * @param duration 만료 시간 (초 단위)
	 */
	//토큰 발급 시
	public void setDateExpire(String key, String value, long duration) {
		redisTemplate.opsForValue().set(key, value, duration, TimeUnit.SECONDS);
	}

	/**
	 * 데이터 조회
	 * @param key 조회할 키
	 * @return 저장된 값
	 */
	//토큰 검증 시
	public String getData(String key) {
		return (String)redisTemplate.opsForValue().get(key);
	}

	/**
	 * 데이터 삭제
	 * @param key 삭제할 키
	 */
	//로그아웃 시
	public void deleteData(String key) {
		redisTemplate.delete(key);
	}

	/**
	 * 키 존재 여부 확인
	 * @param key 확인할 키
	 * @return 존재 여부 (true/false)
	 */
	// Redis에 특정 키(Key)가 존재하는지 확인
	public boolean hasKey(String key) {
		// redisTemplate.hasKey는 Boolean(객체)을 반환하므로 null이 올 수 있습니다.
		Boolean result = redisTemplate.hasKey(key);

		// result가 null이 아니고, 그 값이 true인 경우에만 true 반환
		return result != null && result;
	}

}
