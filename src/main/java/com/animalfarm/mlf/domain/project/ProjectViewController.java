package com.animalfarm.mlf.domain.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProjectViewController {

	@Autowired
	ProjectService projectService;

	@GetMapping("/project/{id}")
	public String selectDetail(@PathVariable
	Long id, Model model) {
		model.addAttribute("projectData", projectService.selectDetail(id));
		model.addAttribute("contentPage", "/WEB-INF/views/project/project_detail.jsp");
		return "layout";
	}
}
