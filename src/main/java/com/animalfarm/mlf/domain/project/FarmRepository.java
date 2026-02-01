package com.animalfarm.mlf.domain.project;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.animalfarm.mlf.domain.project.dto.FarmDTO;

@Mapper
public interface FarmRepository {

	public List<FarmDTO> selectAllFarm();

}
