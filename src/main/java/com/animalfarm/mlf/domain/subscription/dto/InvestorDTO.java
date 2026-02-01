package com.animalfarm.mlf.domain.subscription.dto;

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
public class InvestorDTO {
	private Long shId;
	private Long userId;
	private BigDecimal subscriptionAmount;
}
