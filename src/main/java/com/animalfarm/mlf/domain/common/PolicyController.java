package com.animalfarm.mlf.domain.common;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PolicyController {

	@GetMapping("/policy")
	public String policy(
		@RequestParam(value = "tab", required = false, defaultValue = "marifarm")
		String tab,
		Model model) {
		model.addAttribute("contentPage", "/WEB-INF/views/policy/policy.jsp");
		model.addAttribute("policyTab", tab);
		return "layout";
	}
}
