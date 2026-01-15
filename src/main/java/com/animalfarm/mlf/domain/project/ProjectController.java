package com.animalfarm.mlf.domain.project;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.domain.project.dto.ProjectDetailDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectListDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectSearchReqDTO;

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

}
