package com.animalfarm.mlf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource({
	"classpath:config/enterprise.properties",
	"classpath:config/payment.properties"})
public class EnterpriseConfig {

	// @Value(${...}) 를 동작시키는 핵심 빈 (static 필수)
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
