package com.animalfarm.mlf.domain.project;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.animalfarm.mlf.domain.project.dto.ProjectDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectListDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectSearchReqDTO;

@Mapper
public interface ProjectRepository {

	public abstract List<ProjectDTO> selectAll();

	public abstract List<ProjectListDTO> selectByCondition(ProjectSearchReqDTO projectSearchDTO);
}
