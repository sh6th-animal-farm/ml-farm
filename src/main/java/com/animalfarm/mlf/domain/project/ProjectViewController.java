package com.animalfarm.mlf.domain.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.animalfarm.mlf.domain.project.dto.ProjectSearchReqDTO;

@Controller
@RequestMapping("/project")
public class ProjectViewController {

	@Autowired
	ProjectService projectService;

	@GetMapping({"", "/"})
	public String index() {
		return "redirect:/project/list";
	}

	@GetMapping("/list")
	public String projectListPage(Model model, ProjectSearchReqDTO searchReqDTO) {
		model.addAttribute("contentPage", "/WEB-INF/views/project/project_list.jsp");
		model.addAttribute("activeMenu", "project");
		model.addAttribute("projectList", projectService.selectByCondition(searchReqDTO));
		return "layout"; // 항상 layout.jsp를 리턴
	}
}
