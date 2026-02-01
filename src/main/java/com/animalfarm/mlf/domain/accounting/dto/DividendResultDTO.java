package com.animalfarm.mlf.domain.accounting.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DividendResultDTO {
	private Long transactionId;
	private Long walletId;
	private BigDecimal amount;
}
