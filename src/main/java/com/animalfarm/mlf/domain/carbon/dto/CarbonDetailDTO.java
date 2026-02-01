package com.animalfarm.mlf.domain.carbon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarbonDetailDTO {

	// 1. 상품 그 자체의 정보 (정적 데이터)
	private CarbonDTO carbonInfo;

	// 2. 로그인한 유저가 받는 혜택 정보 (동적 계산 데이터)
	private UserBenefitDTO userBenefit;

	// 3. 농장 및 주소 정보 (매퍼에서 조인으로 채울 예정)
	private String addressSido;
	private String addressSigungu;
	private String addressStreet;
	private String addressDetails;
	private String farmName;
	private String thumbnailUrl;
}
