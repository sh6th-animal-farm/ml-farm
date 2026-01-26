package com.animalfarm.mlf.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;

@Configuration // 1. 이 클래스가 XML을 대체하는 스프링 설정 클래스임을 선언합니다.
@EnableTransactionManagement // 2. @Transactional 어노테이션을 활성화합니다. 이게 없으면 서비스에 @Transactional을 붙여도 작동하지 않습니다.
@ComponentScan(basePackages = "com.animalfarm.mlf", excludeFilters = {
	/* * 4. [중요] 필터 설정: Controller 계층은 제외합니다.
	 * 왜 제외하나요? 컨트롤러는 웹 설정(ServletConfig)에서 별도로 관리해야 합니다.
	 * 여기서 컨트롤러까지 스캔하면 트랜잭션이 중복되거나 꼬일 수 있기 때문입니다.
	 */
	@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class),
	@ComponentScan.Filter(type = FilterType.ANNOTATION, value = RestController.class)
})
public class RootConfig {
	// DataSource, TransactionManager, SqlSessionFactory 설정...
	// root-context.xml에 DB 설정이 다 있는 경우 해당 클래스에 아무것도 없어도 괜찮음.
}
