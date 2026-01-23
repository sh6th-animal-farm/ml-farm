package com.animalfarm.mlf.domain.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class SignUpViewController {

	@GetMapping("/signup")
	public String mainPage(Model model) {
		// 본문에 보여줄 JSP 경로만 지정
		model.addAttribute("contentPage", "/WEB-INF/views/auth/signup.jsp");
		return "layout"; // 항상 layout.jsp를 리턴
	}
}