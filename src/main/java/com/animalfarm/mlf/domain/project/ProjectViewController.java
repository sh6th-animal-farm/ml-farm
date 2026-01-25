package com.animalfarm.mlf.domain.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	@GetMapping("/{id}")
	public String selectDetail(@PathVariable
	Long id, Model model) {
		model.addAttribute("projectData", projectService.selectDetail(id));
		model.addAttribute("contentPage", "/WEB-INF/views/project/project_detail.jsp");
		model.addAttribute("myWallet", projectService.selectMyWallet());
		return "layout";
	}

	@GetMapping("/list")
	public String projectListPage(Model model, ProjectSearchReqDTO searchReqDTO) {
		model.addAttribute("contentPage", "/WEB-INF/views/project/project_list.jsp");
		model.addAttribute("activeMenu", "project");
		model.addAttribute("projectList", projectService.selectByCondition(searchReqDTO));
		return "layout"; // 항상 layout.jsp를 리턴
	}

	@GetMapping("/list/fragment")
	public String projectListFragment(Model model, ProjectSearchReqDTO searchReqDTO) {
		System.out.println(searchReqDTO);
		model.addAttribute("projectList", projectService.selectByCondition(searchReqDTO));
		return "project/project_card_list";
	}
}
