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
public class SnapshotResponseDTO {
	private Long userId;
	private Long walletId;
	private Long tokenId;
	private BigDecimal tokenBalance; // 보유 토큰 개수
	private Long rsId; // 정산 요약 ID
	private Long projectId;
}