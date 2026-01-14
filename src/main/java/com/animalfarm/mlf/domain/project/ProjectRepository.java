package com.animalfarm.mlf.domain.project;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.animalfarm.mlf.domain.project.dto.ProjectDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectDetailDTO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ProjectRepository implements ProjectInterface {

	@Autowired
	SqlSession sqlSession;

	String namespace = "com.animalfarm.mlf.project.";

	@Override
	public List<ProjectDTO> selectAll() {
		return sqlSession.selectList(namespace + "selectAll");
	}

	@Override
	public ProjectDetailDTO selectDetail(Long projectId) {
		return sqlSession.selectOne(namespace + "selectDetail", projectId);
	}
}
