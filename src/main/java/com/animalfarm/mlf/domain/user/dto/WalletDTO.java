package com.animalfarm.mlf.domain.user.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WalletDTO {
	private String accountNo;			  		    // 계좌번호
	private String bankName;				  	    // 은행명
	private BigDecimal cashBalance;		  		// 예수금
	private BigDecimal frozenAmount;		  	// 동결 금액
	private BigDecimal totalPurchasedValue; // 매입 금액
	private BigDecimal totalMarketValue;		// 평가 금액
	private BigDecimal totalBalance;			  // 총자산
	private BigDecimal profitLoss;				  // 평가손익
	private BigDecimal profitLossRate;			// 수익률
}
