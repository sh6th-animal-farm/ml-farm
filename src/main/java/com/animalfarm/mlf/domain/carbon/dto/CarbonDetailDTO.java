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
}
