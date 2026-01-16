package com.animalfarm.mlf.domain.project;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.animalfarm.mlf.domain.project.dto.ProjectDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectDetailDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectListDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectSearchReqDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectStarredDTO;

@Service
public class ProjectService {
	@Autowired
	ProjectRepository projectRepository;

	public List<ProjectDTO> selectAll() {
		return projectRepository.selectAll();
	}

	public ProjectDetailDTO selectDetail(Long projectId) {
		return projectRepository.selectDetail(projectId);
	}

	public List<ProjectListDTO> selectByCondition(ProjectSearchReqDTO searchDTO) {
		return projectRepository.selectByCondition(searchDTO);
	}

	//관심 프로젝트 신규 생성
	public boolean insertStrarredProject(ProjectStarredDTO projectStarredDTO) {
		try {
			projectRepository.insertStrarredProject(projectStarredDTO);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	//관심 프로젝트 등록/해제
	public boolean updateStarred(ProjectStarredDTO projectStarredDTO) {
		try {
			projectRepository.updateStarred(projectStarredDTO);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
