package com.animalfarm.mlf.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	/**
	 * 1. Redis 연결을 위한 ConnectionFactory 설정
	 * Jedis 라이브러리를 사용하여 로컬 Redis(localhost:6379)에 연결합니다.
	 */
	public JedisConnectionFactory jedisConnectionFactory() {
		//기본적으로 localhost:6379로 설정되지만, 명시적으로 작성하는 것이 안전
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
		//만약 redis에 비밀번호가 설정되어 있다면 아래 주석을 해제하고 설정
		//config.setPassword("your_password");

		JedisConnectionFactory factory = new JedisConnectionFactory(config);
		factory.afterPropertiesSet();

		return factory;
	}

	/**
	 * 2. 데이터를 읽고 쓰기 위한 RedisTemplate 설정
	 * 이 객체를 통해 Redis의 String, Hash, List 등의 자료구조를 제어합니다.
	 */
	@Bean(name = "redisTemplate")
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory());

		//Redis 서버에 저장될 떄 key와 value를 어떤 방식으로 직렬화(변환)할 지 결정
		//StringRedisSerializer를 설정해야 redis-cli에서 데이터를 사람이 읽을 수 있는 형태로 볼 수 있다
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());

		//Hash 구조를 사용할 경우를 대비한 설정
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(new StringRedisSerializer());

		redisTemplate.afterPropertiesSet();

		return redisTemplate;

	}

}
