package com.animalfarm.mlf.domain.token.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketDTO {
	private Long tokenId;
	private String tokenName;
	private String tickerSymbol;
	private BigDecimal marketPrice;
	private BigDecimal dailyTradeVolume;
}
