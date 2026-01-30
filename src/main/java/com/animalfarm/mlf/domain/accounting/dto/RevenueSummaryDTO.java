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
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RevenueSummaryDTO {
	Long rsId;
	Long projectId;
	String status;
	BigDecimal totalRevenue;
	BigDecimal totalExpense;
	BigDecimal netProfit;

}
