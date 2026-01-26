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
public class SubscriptionInsertDTO {
	private Long shId;
	private Long projectId;
	private Long userId;
	private BigDecimal subscriptionAmount;

	// 청약 상태: PENDING, APPROVED, REJECTED, CANCELED
	private String subscriptionStatus;

	// 결제 상태: RESERVED, PAID, FAILED, REFUNDED
	private String paymentStatus;

	// kh증권의 wallet_id
	private Long uclId;
}
