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
public class UserBenefitDTO {
	private BigDecimal userMaxLimit; // 계산된 구매 한도
	private BigDecimal discountRate; // 결정된 할인율
	private BigDecimal currentPrice; // 최종 적용 단가
	private BigDecimal myTokenBalance;// 내 토큰 보유량
}
