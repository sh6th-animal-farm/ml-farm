package com.animalfarm.mlf.domain.carbon;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/carbon")
public class CarbonViewController {

	@GetMapping("/list")
	public String carbonListPage(Model model) {
		model.addAttribute("contentPage", "/WEB-INF/views/carbon/carbon_list.jsp");
		model.addAttribute("activeMenu", "carbon-market");
		return "layout";
	}

}
