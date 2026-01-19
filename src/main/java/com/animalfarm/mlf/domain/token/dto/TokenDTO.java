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
public class TokenDTO {
	private Long tokenId;
	private Long projectId;
	private String tokenName;
	private String tickerSymbol;
	private BigDecimal totalSupply;
}
