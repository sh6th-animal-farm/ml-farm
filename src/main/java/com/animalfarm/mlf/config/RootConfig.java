package com.animalfarm.mlf.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@EnableTransactionManagement
@EnableAsync
@ComponentScan(basePackages = "com.animalfarm.mlf", excludeFilters = {
	/* * 필터 설정: Controller 계층은 제외합니다.
	 * 왜 제외하나요? 컨트롤러는 웹 설정(ServletConfig)에서 별도로 관리
	 */
	@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class),
	@ComponentScan.Filter(type = FilterType.ANNOTATION, value = RestController.class)
})
public class RootConfig {
	// root-context.xml에 DB 설정이 다 있는 경우 해당 클래스에 아무것도 없어도 괜찮음.

	//  재시도 전용 스레드 풀 설정
	@Bean(name = "retryExecutor")
	public Executor retryExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5); // 최소 유지 스레드 수
		executor.setMaxPoolSize(10); // 최대 확장 스레드 수
		executor.setQueueCapacity(100); // 대기열 크기
		executor.setThreadNamePrefix("RetryWorker-");
		executor.initialize();
		return executor;
	}
}
