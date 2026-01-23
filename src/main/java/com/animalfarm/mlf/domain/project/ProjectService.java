package com.animalfarm.mlf.domain.project;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animalfarm.mlf.domain.project.dto.FarmDTO;
import com.animalfarm.mlf.domain.project.dto.ImgEditable;
import com.animalfarm.mlf.domain.project.dto.ProjectDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectDetailDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectInsertDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectListDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectPictureDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectSearchReqDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectStarredDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectStatusDTO;

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

	public boolean getStarredStatus(ProjectStarredDTO projectStarredDTO) {
		return projectRepository.getStarredStatus(projectStarredDTO);
	}

	//select하여 있다면 관심 프로젝트 등록/해제 없다면 관심 프로젝트 신규 생성
	public boolean upsertStrarredProject(ProjectStarredDTO projectStarredDTO) {
		try {
			boolean isExist = projectRepository.selectStarredProject(projectStarredDTO);
			if (isExist) {
				projectRepository.updateStarred(projectStarredDTO);
				return true;
			} else {
				projectRepository.insertStrarredProject(projectStarredDTO);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional
	public boolean insertProject(ProjectInsertDTO projectInsertDTO) {
		try {
			BigDecimal subscriptionRate = projectInsertDTO.getActualAmount()
				.divide(projectInsertDTO.getTargetAmount(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
			projectInsertDTO.setSubscriptionRate(subscriptionRate);
			projectRepository.insertProject(projectInsertDTO);
			if (projectInsertDTO.getProjectImageNames() != null && !projectInsertDTO.getProjectImageNames().isEmpty()) {
				projectRepository.insertPictureList(extractPictureDTO(projectInsertDTO));
			}
			projectRepository.insertToken(projectInsertDTO);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("프로젝트 등록 중 오류 발생: " + e.getMessage());
		}
	}

	public List<FarmDTO> selectAllFarm() {
		return projectRepository.selectAllFarm();
	}

	@Transactional
	public boolean updateProject(ProjectDTO projectDTO) {
		try {
			projectRepository.updateProject(projectDTO);
			if (projectDTO.getProjectImageNames() != null && !projectDTO.getProjectImageNames().isEmpty()) {
				projectRepository.insertPictureList(extractPictureDTO(projectDTO));
			}
			if (projectDTO.getDeletedPictureIds() != null && !projectDTO.getDeletedPictureIds().isEmpty()) {
				projectRepository.deletePictureList(projectDTO.getDeletedPictureIds());
			}
			return true; // 성공 시 true 반환
		} catch (DataAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("프로젝트 수정 중 오류 발생: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("프로젝트 수정 중 오류 발생: " + e.getMessage());
		}
	}

	public List<ProjectPictureDTO> selectPictures(Long projectId) {
		return projectRepository.selectPictures(projectId);
	}

	public List<ProjectPictureDTO> extractPictureDTO(ImgEditable imgEditableDTO) {
		List<ProjectPictureDTO> newPictureDTOList = new ArrayList<>();
		for (String projectName : imgEditableDTO.getProjectImageNames()) {
			ProjectPictureDTO newPicture = new ProjectPictureDTO();
			newPicture.setProjectId(imgEditableDTO.getProjectId());
			newPicture.setImageUrl(projectName);
			newPictureDTOList.add(newPicture);
		}
		return newPictureDTOList;
	}

	public List<ProjectStatusDTO> selectStatus() {
		List<ProjectStatusDTO> dto = projectRepository.selectStatus();
		for (ProjectStatusDTO projectStatusDTO : dto) {
			String current = projectStatusDTO.getProjectStatus();
			String next = "";

			switch (current) {
				case "PREPARING": {
					next = "ANNOUNCEMENT";
					projectStatusDTO.setNextStatus(next);
					break;
				}
				case "ANNOUNCEMENT": {
					next = "SUBSCRIPTION";
					projectStatusDTO.setNextStatus(next);
					break;
				}
				case "SUBSCRIPTION": {
					next = "SUBSCRIPTION";
					projectStatusDTO.setNextStatus(next);
					break;
				}
			}
			projectRepository.updateProjectStatus(projectStatusDTO);
		}
		return projectRepository.selectStatus();
	}

}
