package com.kanghwang.khholdings.domain.market.dto;

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
public class MarketDTO {
	private Long tokenId;
	private String tokenName;
	private String tickerSymbol;
	private BigDecimal marketPrice;
	private BigDecimal dailyTradeVolume;
}
