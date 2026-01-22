package com.animalfarm.mlf.domain.carbon;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.animalfarm.mlf.domain.carbon.dto.CarbonDetailDTO;
import com.animalfarm.mlf.domain.carbon.dto.CarbonListDTO;

public interface CarbonRepository {

	List<CarbonListDTO> selectAll();

	List<CarbonListDTO> selectByCategory(@Param("category")
	String category);

	CarbonDetailDTO selectDetail(Long cpId);

	BigDecimal getDiscountRate(@Param("sharePercent")
	BigDecimal sharePercent); //할인율
}
