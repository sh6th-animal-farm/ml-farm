package com.animalfarm.mlf;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import lombok.RequiredArgsConstructor;

/**
 * Handles requests for the application home page.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class HomeController {

	@GetMapping({"", "/"})
	public String index() {
		return "redirect:/main";
	}
	
	@GetMapping("/main")
	public String mainPage(Model model) {
	    model.addAttribute("contentPage", "/WEB-INF/views/home.jsp");
	    return "layout"; // 항상 layout.jsp를 리턴
	}

}
