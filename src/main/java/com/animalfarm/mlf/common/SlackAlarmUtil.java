package com.animalfarm.mlf.common;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SlackAlarmUtil {
	private final RestTemplate restTemplate;

	@Value("${slack.webhook.url}") // application.properties에 URL 등록 필요
	private String slackUrl;

	public void sendAdminAlarm(String message) {
		try {
			Map<String, String> body = Map.of("text", "🚨 [재시도 최종 실패 알림]\n" + message);
			restTemplate.postForEntity(slackUrl, body, String.class);
		} catch (Exception e) {
			log.error("슬랙 알림 전송 중 에러 발생", e);
		}
	}
}