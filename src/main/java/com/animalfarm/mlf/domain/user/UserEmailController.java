package com.animalfarm.mlf.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.domain.user.dto.EmailSendRequestDTO;
import com.animalfarm.mlf.domain.user.dto.EmailVerifyRequestDTO;

@RestController
@RequestMapping("/api/auth/email")
public class UserEmailController {

	@Autowired
	private UserEmailService userEmailService;

	@PostMapping(value = "/verification", produces = "text/plain; charset=UTF-8")
	public String sendVerificationCode(@RequestBody
	EmailSendRequestDTO request) {
		userEmailService.sendCode(request.getEmail());
		return "이메일 코드 발송 완료";
	}

	@PostMapping(value = "/verification/confirmation", produces = "text/plain; charset=UTF-8")
	public String confirmVerificationCode(@RequestBody
	EmailVerifyRequestDTO request) {
		boolean ok = userEmailService.verifyCode(request.getEmail(), request.getCode());
		return ok ? "이메일 인증 성공" : "이메일 인증 실패";
	}
}
