package com.animalfarm.mlf.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MailService {

	@Autowired
	private JavaMailSender mailSender;

	public void sendAuthCode(String toEmail, String authCode) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setTo(toEmail);
			helper.setSubject("[마이리틀 스마트팜] 이메일 인증번호 안내");

			String html = loadHtmlTemplate("templates/email-auth.html");
			html = html.replace("{{AUTH_CODE}}", authCode);

			helper.setText(html, true);
			ClassPathResource image = new ClassPathResource("templates/logo.png");
			if (image.exists()) {
				helper.addInline("logoImage", image);
			}
			mailSender.send(message);

		} catch (Exception e) {
			throw new RuntimeException("메일 발송 실패", e);
		}
	}

	private String loadHtmlTemplate(String path) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
			new ClassPathResource(path).getInputStream(),
			StandardCharsets.UTF_8))) {

			return reader.lines().collect(Collectors.joining("\n"));
		} catch (Exception e) {
			throw new RuntimeException("메일 템플릿 로드 실패", e);
		}
	}

	// 배당금 수령 방식 설문 메일 발송
	public void sendDividendPollEmail(String toEmail, String userName, String amount, String endDate, Long dividendId) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(toEmail);
			helper.setSubject("[마이리틀 스마트팜] 배당금 수령 방식 선택 안내");

			// 배당용 HTML 템플릿 로드 (아래 2번 단계에서 생성)
			String html = loadHtmlTemplate("templates/dividend-poll.html");

			// 데이터 치환
			html = html.replace("{{USER_NAME}}", userName);
			html = html.replace("{{AMOUNT}}", amount);
			html = html.replace("{{END_DATE}}", endDate);
			html = html.replace("{{POLL_URL}}", "http://localhost:8080/project/dividend/poll?id=" + dividendId);

			helper.setText(html, true);

			mailSender.send(message);

		} catch (Exception e) {
			log.error("[Email] 메일 발송 중 상세 에러 발생: ", e);
			throw new RuntimeException("배당 안내 메일 발송 실패", e);
		}
	}

	public void sendNoticeEmail(String toEmail) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setTo(toEmail);
			helper.setSubject("[마이리틀 스마트팜] 청약 기간 연장 안내");

			String html = loadHtmlTemplate("templates/subscription-extension.html");

			helper.setText(html, true);
			ClassPathResource image = new ClassPathResource("templates/logo.png");
			if (image.exists()) {
				helper.addInline("logoImage", image);
			}

			mailSender.send(message);

		} catch (Exception e) {
			log.error("[Email] 메일 발송 중 상세 에러 발생: ", e);
			throw new RuntimeException("메일 발송 실패", e);
		}
	}
}
