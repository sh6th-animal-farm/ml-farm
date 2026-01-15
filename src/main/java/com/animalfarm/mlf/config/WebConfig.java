package com.animalfarm.mlf.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * 프로젝트의 웹 관련 설정을 담당하는 클래스
 * @Configuration: 스프링 설정 클래스임을 명시
 * @EnableWebMvc: 스프링 MVC 기능을 활성화 (기본 설정들을 불러옴)
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    
    /**
     * 스프링에서 데이터를 변환하는 '컨버터'를 설정하는 메서드
     * 여기서는 JSON 변환기인 Jackson의 동작 방식을 정의함
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        
        // Jackson2ObjectMapperBuilder: Jackson의 핵심 객체인 ObjectMapper를 편하게 생성해주는 빌더
        // .json(): JSON 형식에 최적화된 빌더 인스턴스를 시작함
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                
                // Java 8의 시간 타입(OffsetDateTime, LocalDateTime 등)을 Jackson이 인식하도록 모듈 추가
                // 이게 없으면 Java 8 날짜 타입을 직렬화/역직렬화 할 때 에러가 발생함
                .modules(new JavaTimeModule())
                
                // 날짜를 숫자 배열(Timestamps, 예: [2024,12,9...]) 형식이 아닌, 
                // ISO-8601 표준 문자열(예: "2024-12-09T11:35:00...")로 보내도록 설정
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                
                // 위 설정들이 적용된 최종 ObjectMapper 객체를 생성
                .build();

        // 생성한 커스텀 ObjectMapper를 MappingJackson2HttpMessageConverter에 담아서
        // 스프링이 사용할 컨버터 목록(converters)에 추가함
        converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
    }
}