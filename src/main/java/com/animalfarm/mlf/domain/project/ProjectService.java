package com.animalfarm.mlf.domain.project;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.animalfarm.mlf.domain.project.dto.ProjectDTO;

@Service
public class ProjectService {
	@Autowired
	ProjectRepository projectRepository;
	
	public List<ProjectDTO> selectAll() {
		return projectRepository.selectAll();
	}
}
