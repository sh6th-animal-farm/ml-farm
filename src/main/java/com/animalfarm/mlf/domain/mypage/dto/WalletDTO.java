package com.animalfarm.mlf.domain.mypage.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletDTO {

	private String accountNo; // 계좌번호
	private String bankName; // 은행명
	private BigDecimal cashBalance; // 예수금
	private BigDecimal frozenAmount; // 동결 금액
	private BigDecimal totalPurchasedValue; // 총 매입 금액
	private BigDecimal totalMarketValue; // 총 평가 금액
	private BigDecimal totalBalance; // 총 자산 (예수금 + 평가금액)
	private BigDecimal profitLoss; // 평가 손익
	private BigDecimal profitLossRate; // 수익률
}
