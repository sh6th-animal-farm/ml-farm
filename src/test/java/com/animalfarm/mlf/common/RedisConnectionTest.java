package com.animalfarm.mlf.common;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class) //스프링과 junit 연결
@ContextConfiguration(classes = {RedisConfig.class, RedisUtil.class}) //테스트할 설정 및 빈 로드
public class RedisConnectionTest {

	@Autowired
	private RedisUtil redisUtil;

	@Test
	public void redis_데이터저장및조회test() {
		// 1. 테스트 데이터 준비
		String testKey = "sto:test:key";
		String testValue = "hello_smart_farm";

		// 2. Redis에 저장( 유효시간 60초)
		redisUtil.setDateExpire(testKey, testValue, 60L);

		// 3. Redis에서 데이터 읽기
		String resultValue = redisUtil.getData(testKey);

		// 4. 검증(저장한 값과 가져온 값이 일치하는 지 확인)
		System.out.println("===============");
		System.out.println("Redis에서 가져온 값: " + resultValue);
		System.out.println("================");

		assertNotNull("가져온 데이터는 null이 아니어야 합니다.", resultValue);
		assertEquals(testValue, resultValue);

	}

}
