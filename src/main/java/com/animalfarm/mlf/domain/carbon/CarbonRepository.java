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

	// 상세 조회
	CarbonDetailDTO selectDetail(Long cpId);

	// 유저 ID로 지갑 번호(ucl_id) 가져오기
	Long getWalletIdByUserId(@Param("userId")
	Long userId);

	//지분이 있는 토큰 ID 리스트를 받아 필터링된 상품들을 가져옵
	List<CarbonDetailDTO> selectProductsByTokenIds(@Param("tokenIds")
	List<Long> tokenIds);

	// 프로젝트 ID로 강황증권의 토큰 ID 조회
	Long getTokenIdByProjectId(@Param("projectId")
	Long projectId);

	//할인율
	BigDecimal getDiscountRate(@Param("sharePercent")
	BigDecimal sharePercent);

}
