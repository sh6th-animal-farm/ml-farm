package com.animalfarm.mlf.domain.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.domain.user.dto.SignUpRequestDTO;
import com.animalfarm.mlf.domain.user.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping(value = "/signup", produces = "text/plain; charset = UTF-8")
	public ResponseEntity<String> signup(@RequestBody
	SignUpRequestDTO request) {
		userService.signUp(request);
		return ResponseEntity.ok("회원가입 완료");
	}
}
