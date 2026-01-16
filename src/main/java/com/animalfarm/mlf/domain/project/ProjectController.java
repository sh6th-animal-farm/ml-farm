package com.animalfarm.mlf.domain.project;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.domain.project.dto.ProjectDetailDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectListDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectSearchReqDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectStarredDTO;

@RestController
public class ProjectController {

	@Autowired
	ProjectService projectService;

	@GetMapping("/projects")
	public String projectListPage() {
		return "";
	}

	@GetMapping("/api/project/{projectId}")
	public ProjectDetailDTO selectDetail(@PathVariable("projectId")
	Long projectId) {
		return projectService.selectDetail(projectId);
	}

	@GetMapping("/api/projects")
	public List<ProjectListDTO> selectByCondition(@ModelAttribute
	ProjectSearchReqDTO searchDTO) {
		return projectService.selectByCondition(searchDTO);
	}

	@PostMapping("/api/projects/starred/newinsert")
	public String insertStrarredProject(@ModelAttribute
	ProjectStarredDTO projectStarredDTO) {
		String message = null;
		if (projectService.insertStrarredProject(projectStarredDTO)) {
			message = "success";
		} else {
			message = "fail";
		}
		return message;
	}

	@PostMapping("/api/projects/starred/interest")
	public String updateStarredInterest(@ModelAttribute
	ProjectStarredDTO projectStarredDTO) {
		String message = null;
		if (projectService.updateStarredInterest(projectStarredDTO)) {
			message = "success";
		} else {
			message = "fail";
		}
		return message;
	}

	@PostMapping("/api/projects/starred/disinterest")
	public String updateStarredDisinterest(@ModelAttribute
	ProjectStarredDTO projectStarredDTO) {
		String message = null;
		if (projectService.updateStarredDisinterest(projectStarredDTO)) {
			message = "success";
		} else {
			message = "fail";
		}
		return message;
	}

}
