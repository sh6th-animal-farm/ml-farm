package com.animalfarrm.mlf.domain.project;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectRepository implements ProjectInterface {

	@Autowired
	SqlSession sqlSession;

	String namespace = "com.animalfarm.mlf.project";

}
