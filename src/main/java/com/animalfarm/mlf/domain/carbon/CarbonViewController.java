package com.animalfarm.mlf.domain.carbon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/carbon")
public class CarbonViewController {

	@Autowired
	private CarbonService carbonService;

	@GetMapping("/list")
	public String carbonListPage(
		@RequestParam(value = "category", required = false)
		String category,
		Model model) {
		model.addAttribute("contentPage", "/WEB-INF/views/carbon/carbon_list.jsp");
		model.addAttribute("activeMenu", "carbon-market");
		model.addAttribute("carbonList", carbonService.selectByCategory(category));

		return "layout";
	}

	@GetMapping("/detail/{id}")
	public String carbonDetailPage(@PathVariable
	Long id, Model model) {
		model.addAttribute("cpId", id); // JS에서 API 호출할 때 쓰라고 넘겨줌
		model.addAttribute("contentPage", "/WEB-INF/views/carbon/carbon_detail.jsp");
		model.addAttribute("activeMenu", "carbon");
		return "layout";
	}

}
