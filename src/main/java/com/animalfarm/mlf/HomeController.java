package com.animalfarm.mlf;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles requests for the application home page.
 */
@Controller
@RequestMapping("/")
public class HomeController {

	@GetMapping
	public String home(Locale locale, Model model) {
	    // 본문에 보여줄 JSP 경로만 지정
	    model.addAttribute("contentPage", "/WEB-INF/views/home.jsp");
	    return "layout"; // 항상 layout.jsp를 리턴
	}
	
	@GetMapping("/main")
	public String mainPage(Model model) {
	    // 본문에 보여줄 JSP 경로만 지정
	    model.addAttribute("contentPage", "/WEB-INF/views/home.jsp");
	    return "layout"; // 항상 layout.jsp를 리턴
	}

}
