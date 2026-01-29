package com.animalfarm.mlf.domain.token.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenListDTO {
	private Long tokenId;
	private String tokenName;
	private String tickerSymbol;
	private BigDecimal marketPrice;			// 현재가(시장가)
	private BigDecimal dailyTradeVolume;	// 오늘 누적 거래 대금

	private BigDecimal openPrice;			// 시가: 오늘 or 어제 오전 9시 시작 가격
	private BigDecimal highPrice;   		// 고가 (추가)
	private BigDecimal lowPrice;    		// 저가 (추가)
	private BigDecimal changeRate;			// 등락률
}

