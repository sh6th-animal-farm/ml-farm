package com.animalfarm.mlf.domain.mypage.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HoldingDTO {

	private String tokenName; // 토큰명
	private String tickerSymbol; // 종목 코드
	private BigDecimal tokenBalance; // 보유 수량
	private BigDecimal purchasedValue; // 매입 금액
	private BigDecimal marketValue; // 평가 금액
	private BigDecimal profitLoss; // 평가 손익
	private BigDecimal profitLossRate; // 수익률
}
