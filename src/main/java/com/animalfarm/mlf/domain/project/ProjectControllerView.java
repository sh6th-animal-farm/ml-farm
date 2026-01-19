package com.animalfarm.mlf.domain.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProjectControllerView {

	@Autowired
	ProjectService projectService;

	@GetMapping("/project/detail.do")
	public String selectDetail(Model model) {
		model.addAttribute("projectData", projectService.selectDetail(4L));
		model.addAttribute("contentPage", "/WEB-INF/views/project/projectDetail.jsp");
		return "layout";
	}
}
