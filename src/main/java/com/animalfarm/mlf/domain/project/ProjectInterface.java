package com.animalfarm.mlf.domain.project;

import java.util.List;

import com.animalfarm.mlf.domain.project.dto.ProjectDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectDetailDTO;

public interface ProjectInterface {
	public abstract List<ProjectDTO> selectAll();

	public abstract ProjectDetailDTO selectDetail(Long projectId);
}
