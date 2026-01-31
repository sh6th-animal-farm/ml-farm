package com.animalfarm.mlf.domain.token.dto;

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
public class TokenDetailDTO {
	private Long tokenId;
	private String tokenName;
	private String tickerSymbol;
	private BigDecimal openingPrice;
	private BigDecimal highPrice;
	private BigDecimal lowPrice;
	private BigDecimal closingPrice;
	private BigDecimal tradeVolume;
	private BigDecimal prevClosingPrice;
}
