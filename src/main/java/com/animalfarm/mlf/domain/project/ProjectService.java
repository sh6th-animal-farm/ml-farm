package com.animalfarm.mlf.domain.project;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.animalfarm.mlf.domain.project.dto.FarmDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectDetailDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectInsertDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectListDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectPictureDTO;
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

	public boolean insertProject(ProjectInsertDTO projectInsertDTO) {
		try {
			BigDecimal subscriptionRate = projectInsertDTO.getActualAmount()
				.divide(projectInsertDTO.getTargetAmount(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
			projectInsertDTO.setSubscriptionRate(subscriptionRate);
			projectRepository.insertProject(projectInsertDTO);
			System.out.println("생성된 프로젝트 ID: " + projectInsertDTO.getProjectId());
			projectRepository.insertToken(projectInsertDTO);
			return true;
		} catch (Exception e) {
			System.out.println("오류");
			e.printStackTrace();
			return false;
		}
	}

	public List<FarmDTO> selectAllFarm() {
		return projectRepository.selectAllFarm();
	}

	public boolean updateProject(ProjectDTO projectDTO) {
		try {
			projectRepository.updateProject(projectDTO);
			return true; // 성공 시 true 반환
		} catch (DataAccessException e) {
			e.printStackTrace();
			return false; // 실패 시 false 반환
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<ProjectPictureDTO> selectPictures(Long projectId) {
		return projectRepository.selectPictures(projectId);
	}
}
