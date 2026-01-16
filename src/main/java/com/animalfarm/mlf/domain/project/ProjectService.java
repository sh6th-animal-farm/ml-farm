package com.animalfarm.mlf.domain.project;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.animalfarm.mlf.domain.project.dto.FarmDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectDetailDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectInsertDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectListDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectSearchReqDTO;

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

	public void insertProject(ProjectInsertDTO projectInsertDTO) {
		String result = "출력";
		try {
			BigDecimal subscriptionRate = projectInsertDTO.getActualAmount()
				.divide(projectInsertDTO.getTargetAmount(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
			projectInsertDTO.setSubscriptionRate(subscriptionRate);
			projectRepository.insertProject(projectInsertDTO);
			System.out.println("생성된 프로젝트 ID: " + projectInsertDTO.getProjectId());
			projectRepository.insertToken(projectInsertDTO);
		} catch (Exception e) {
			System.out.println("오류");
			e.printStackTrace();
		}
	}

	public List<FarmDTO> selectAllFarm() {
		return projectRepository.selectAllFarm();
	}
}
