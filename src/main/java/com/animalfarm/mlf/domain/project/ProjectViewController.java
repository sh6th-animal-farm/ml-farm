package com.animalfarm.mlf.domain.project;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/project")
public class ProjectViewController {

	@GetMapping({"", "/"})
	public String index() {
		return "redirect:/project/list";
	}

	@GetMapping("/list")
	public String projectListPage(Model model) {
		model.addAttribute("contentPage", "/WEB-INF/views/project/project_list.jsp");
		model.addAttribute("activeMenu", "project");
		return "layout"; // 항상 layout.jsp를 리턴
	}
}
