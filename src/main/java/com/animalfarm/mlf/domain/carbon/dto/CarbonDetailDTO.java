package com.animalfarm.mlf.domain.carbon.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CarbonDetailDTO {

	// 1. 상품 기본 정보
	private Long projectId; // 프로젝트 FK (로직용)
	private String projectName; // 프로젝트 이름 (화면 표시용)
	private String cpType; // REMOVAL, REDUCTION
	private String vintageYear;
	private BigDecimal basePrice; // 할인 전 단가
	private BigDecimal stock; // 잔여 수량 (cp_amount)

	// 2. 유저 맞춤형 계산 정보
	private BigDecimal userMaxLimit; // 기업 지분 비례 최대 구매 한도
	private BigDecimal userDiscountRate; // 지분 구간별 할인율
	private BigDecimal finalPrice; // 최종 결제 단가 (VAT 별도)
	private BigDecimal userTokenBalance; // 유저가 보유한 토큰 수량
}
