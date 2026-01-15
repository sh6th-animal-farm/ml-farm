package com.animalfarm.mlf.domain.user;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.animalfarm.mlf.common.MailService;

@Service
public class UserEmailService {

	private static final long EXPIRE_MIN = 5;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private MailService mailService;

	public void sendCode(String email) {
		String code = createCode();
		String key = "EMAIL_AUTH:" + email;

		redisTemplate.opsForValue()
			.set(key, code, EXPIRE_MIN, TimeUnit.MINUTES);

		mailService.sendAuthCode(email, code);
	}

	public boolean verifyCode(String email, String inputCode) {
		String key = "EMAIL_AUTH:" + email;
		Object savedCode = redisTemplate.opsForValue().get(key);

		if (savedCode == null) {
			return false;
		}

		boolean success = savedCode.toString().equals(inputCode);
		if (success) {
			redisTemplate.delete(key);
		}

		return success;
	}

	private String createCode() {
		return String.valueOf(100000 + new Random().nextInt(900000));
	}
}
