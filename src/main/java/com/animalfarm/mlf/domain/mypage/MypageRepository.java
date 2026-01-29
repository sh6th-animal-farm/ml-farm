package com.animalfarm.mlf.domain.mypage;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.animalfarm.mlf.domain.mypage.dto.CarbonHistoryDTO;

@Mapper
public interface MypageRepository {
	List<CarbonHistoryDTO> selectCarbonHistoryByUserId(Long userId);
}
