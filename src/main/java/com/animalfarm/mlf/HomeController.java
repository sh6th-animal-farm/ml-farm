package com.animalfarm.mlf;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.animalfarm.mlf.domain.token.TokenService;
import com.animalfarm.mlf.domain.token.dto.TokenListDTO;

import lombok.RequiredArgsConstructor;

/**
 * Handles requests for the application home page.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class HomeController {

	@Autowired
	private TokenService tokenService;

	@GetMapping({"", "/"})
	public String index() {
		return "redirect:/main";
	}
	
	@GetMapping("/main")
	public String mainPage(Model model) {
		List<TokenListDTO> tokenAllList = tokenService.selectAll(); // 이미 거래대금 기준 내림차순
		List<TokenListDTO> tokenList = tokenAllList.stream()
			.limit(10)
			.collect(Collectors.toList());

		model.addAttribute("tokenList", tokenList);
	    model.addAttribute("contentPage", "/WEB-INF/views/home.jsp");
	    return "layout"; // 항상 layout.jsp를 리턴
	}

}
