package com.animalfarm.mlf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//스프링 컨테이너에 Redis 연결 통로를 등록하는 설정 파일
/**
 * [Redis 설정 클래스]
 * Spring Legacy 환경에서는 @Configuration을 통해 자바 설정으로 빈(Bean)을 등록합니다.
 */
@Configuration
//1. 별도로 만든 프로퍼티 파일의 경로를 지정합니다.
@PropertySource("classpath:config/redis.properties")
public class RedisConfig {

	// application.properties에서 값을 읽어옵니다.
	@Value("${redis.host}")
	private String redisHost;

	@Value("${redis.port}")
	private int redisPort;

	@Value("${redis.password:}") // 비밀번호가 없을 경우를 대비해 기본값을 비워둠
	private String redisPassword;

	/**
	 * Redis 서버와의 물리적인 연결을 생성하는 공장(Factory) 설정입니다.
	 * Redis 연결을 위한 ConnectionFactory 설정
	 * Jedis 라이브러리를 사용하여 로컬 Redis(localhost:6379)에 연결합니다.
	 */
	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		//1. Redis 서버 정보 설정 (기본값: localhost, 6379)
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
		// 비밀번호가 설정되어 있다면 적용
		if (redisPassword != null && !redisPassword.isEmpty()) {
			config.setPassword(redisPassword);
		}

		//2. Jedis 커넥션 팩토리 생성
		JedisConnectionFactory factory = new JedisConnectionFactory(config);

		/**
		 * [중요] Spring Legacy에서는 afterPropertiesSet()을 수동 호출해야
		 * 팩토리가 정상 초기화되어 "not initialized" 에러를 방지할 수 있습니다.
		 */
		factory.afterPropertiesSet();

		return factory;
	}

	/**
	 * Redis 명령어를 실행하기 위한 높은 수준의 추상화 도구(Template)입니다.
	 * 데이터를 읽고 쓰기 위한 RedisTemplate 설정
	 * 이 객체를 통해 Redis의 String, Hash, List 등의 자료구조를 제어합니다.
	 */
	@Bean(name = "redisTemplate")
	public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory factory) {

		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

		// 1. 위에서 만든 커넥션 팩토리를 연결
		redisTemplate.setConnectionFactory(factory);

		//Redis 서버에 저장될 떄 key와 value를 어떤 방식으로 직렬화(변환)할 지 결정
		//StringRedisSerializer를 설정해야 redis-cli에서 데이터를 사람이 읽을 수 있는 형태로 볼 수 있다
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());

		// 2. 템플릿 초기화 완료
		redisTemplate.afterPropertiesSet();

		return redisTemplate;

	}

}
