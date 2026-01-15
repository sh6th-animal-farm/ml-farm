package com.animalfarm.mlf.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableWebMvc
public class SwaggerConfig {

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
			.select()
			.apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
			.paths(PathSelectors.any())
			.build()
			// --- 아래 두 설정을 추가해야 Authorize 버튼이 생깁니다 ---
			.securityContexts(Arrays.asList(securityContext()))
			.securitySchemes(Arrays.asList(apiKey()));
	}

	/**
	 * [1. API Key 정의]
	 * Authorization 이라는 이름의 헤더를 사용하며, API Key 방식으로 인증함을 정의합니다.
	 */
	private ApiKey apiKey() {
		return new ApiKey("JWT", "Authorization", "header");
	}

	/**
	 * [2. 보안 컨텍스트 설정]
	 * 어떤 경로에 대해 보안 설정을 적용할지 결정합니다. 여기서는 모든 경로(any)에 적용합니다.
	 */
	private SecurityContext securityContext() {
		return SecurityContext.builder()
			.securityReferences(defaultAuth())
			.forPaths(PathSelectors.any())
			.build();
	}

	/**
	 * [3. 기본 인증 참조 설정]
	 * 전역(global) 범위를 지정하여 모든 API 요청 시 인증 헤더가 포함되도록 합니다.
	 */
	private List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
	}
}