package com.animalfarm.mlf.domain.project;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.domain.project.dto.ProjectDTO;

@RestController
public class ProjectController {
	
	@Autowired
	ProjectService projectService;
	
	@GetMapping("/projects")
	public List<ProjectDTO> selectAll() {
		return projectService.selectAll();
	}
}
