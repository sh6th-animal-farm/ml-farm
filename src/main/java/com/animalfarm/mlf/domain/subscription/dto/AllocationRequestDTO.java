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
public class AllocationRequestDTO {
	private Long subscriptionId;
	private Long walletId;
	private BigDecimal passPrice; // 배정 금액
	private BigDecimal passVolume; // 배정 수량
}
