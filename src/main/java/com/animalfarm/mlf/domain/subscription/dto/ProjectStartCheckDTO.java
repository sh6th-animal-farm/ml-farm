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
public class ProjectStartCheckDTO {
	private Long projectId;
	private BigDecimal subscriptionRate;
	private String projectStatus;
	private int extensionCount;
	private BigDecimal targetAmount;
	private BigDecimal actualAmount;

	private Long subscriberCount;
	private Long userId;

	private Long tokenId;

	private Long shId;
}
