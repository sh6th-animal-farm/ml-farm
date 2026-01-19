package com.animalfarm.mlf.domain.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // @RestController가 아닌 일반 @Controller여야 JSP로 이동합니다.
public class LoginViewController {

	@GetMapping("/auth/login")
	public String loginPage(Model model) {
		// WEB-INF/views/user/login.jsp 파일로 이동하라는 뜻
		// (ViewResolver 설정에 따라 경로가 다를 수 있습니다)
		model.addAttribute("contentPage", "/WEB-INF/views/auth/login.jsp");
		return "layout";
	}

	@GetMapping("/home")
	public String homePage(Model model) {
		// prefix(/WEB-INF/views/) + "home" + suffix(.jsp)가 합쳐져
		// /WEB-INF/views/home.jsp 파일을 찾아갑니다.
		model.addAttribute("contentPage", "/WEB-INF/views/home.jsp");
		return "layout";
	}
}
