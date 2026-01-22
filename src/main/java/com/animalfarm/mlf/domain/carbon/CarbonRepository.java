package com.animalfarm.mlf.domain.carbon;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.animalfarm.mlf.domain.carbon.dto.CarbonListDTO;

public interface CarbonRepository {

	List<CarbonListDTO> selectAll();

	List<CarbonListDTO> selectByCondition(@Param("category")
	String category);

}
