package com.animalfarm.mlf.domain.project;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.animalfarm.mlf.domain.project.dto.FarmDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectDetailDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectInsertDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectListDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectPictureDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectSearchReqDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectStarredDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectStatusDTO;

@Mapper
public interface ProjectRepository {

	public abstract List<ProjectDTO> selectAll();

	public abstract List<ProjectListDTO> selectByCondition(ProjectSearchReqDTO projectSearchDTO);

	public abstract ProjectDetailDTO selectDetail(Long projectId);

	public abstract boolean selectStarredProject(ProjectStarredDTO projectStarredDTO);

	public abstract boolean getStarredStatus(ProjectStarredDTO projectStarredDTO);

	public abstract void insertStrarredProject(ProjectStarredDTO projectStarredDTO);

	public abstract void updateStarred(ProjectStarredDTO projectStarredDTO);

	public abstract void insertProject(ProjectInsertDTO projectInsertDTO);

	public abstract List<FarmDTO> selectAllFarm();

	public abstract void insertToken(ProjectInsertDTO projectInsertDTO);

	public abstract void updateProject(ProjectDTO projectDTO);

	public abstract List<ProjectPictureDTO> selectPictures(Long projectId);

	public abstract void insertPictureList(List<ProjectPictureDTO> newPictureDTOs);

	public abstract void deletePictureList(List<Long> deletedPictureIds);

	public abstract List<ProjectStatusDTO> selectStatus();

	public abstract void updateProjectStatus(ProjectStatusDTO projectStatusDTO);

	public abstract ProjectDTO selectByProjectId(Long projectId);

	public abstract Long selectMyWalletId(Long userId);
}
