package com.animalfarm.mlf.domain.token;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/token")
public class TokenViewController {

	@GetMapping("/detail")
	public String tokenDetailPage(Model model) {
		model.addAttribute("contentPage", "/WEB-INF/views/token/token_detail.jsp");

		return "layout";
	}
}
