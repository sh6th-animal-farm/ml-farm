package com.animalfarm.mlf.domain.token.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public class TokenIssueDTO {
	private String tokenName;
	private String tickerSymbol;
	private BigDecimal totalSupply;
	private Long projectId;
	private BigDecimal issuePrice;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private OffsetDateTime createdAt;
}
