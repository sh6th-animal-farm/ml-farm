package com.animalfarm.mlf.domain.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class SignUpViewController {

	@GetMapping("/signup")
	public String signupPage() {
		return "user/signup";
	}
}