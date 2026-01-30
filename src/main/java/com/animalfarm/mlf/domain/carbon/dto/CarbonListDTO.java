package com.animalfarm.mlf.domain.carbon.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarbonListDTO {

	// 1. 상품 기본 정보
	private Long cpId;
	private Long projectId;
	private String cpTitle;
	private String category; // <= cp_type과 매핑 (제거형/감축형)
	private BigDecimal cpPrice;
	private BigDecimal cpAmount;
	private String vintageYear;
	private String thumbnailUrl;

	// 2. 로그인한 유저의 동적 혜택 (상세페이지 구조와 동일하게 유지)
	private UserBenefitDTO userBenefit;
}
