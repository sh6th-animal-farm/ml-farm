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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DividendDTO {
	private Long dividendId;
	private Long rsId;
	private Long userId;

	// 소수점 0자리로 관리될 금액들
	private BigDecimal amountBfTax;
	private BigDecimal tax;
	private BigDecimal amountAftTax;

	private String dividendType;
}