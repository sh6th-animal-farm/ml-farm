package com.animalfarm.mlf.domain.project;

import java.util.List;

import com.animalfarm.mlf.domain.project.dto.ProjectDTO;

public interface ProjectInterface {
	public abstract List<ProjectDTO> selectAll();
}
