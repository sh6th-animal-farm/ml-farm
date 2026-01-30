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
