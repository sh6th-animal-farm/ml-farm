package com.animalfarm.mlf.domain.carbon;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.animalfarm.mlf.domain.carbon.dto.CarbonDetailDTO;
import com.animalfarm.mlf.domain.carbon.dto.CarbonListDTO;

public interface CarbonRepository {

	// [전체] 보유 토큰 기반 상품 리스트
	List<CarbonListDTO> selectAll(@Param("tokenIds")
	List<Long> tokenIds);

	// [카테고리] 카테고리 + 보유 토큰 기반 상품 리스트
	List<CarbonListDTO> selectByCondition(@Param("category")
	String category, @Param("tokenIds")
	List<Long> tokenIds);

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

	// 프로젝트 ID로 총 투자금액(actual_amount)만 따로 가져오는 메서드
	BigDecimal getActualAmount(@Param("projectId")
	Long projectId);

	//할인율
	BigDecimal getDiscountRate(@Param("sharePercent")
	BigDecimal sharePercent);

	String selectCpTitle(@Param("cpId")
	Long cpId);

	// 상품 잔여 수량 조회
	BigDecimal selectCpAmount(@Param("cpId")
	Long cpId);

}
