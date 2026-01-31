package com.animalfarm.mlf.domain.accounting.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

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
	private Long projectId;

	// 소수점 0자리로 관리될 금액들
	private BigDecimal amountBfTax;
	private BigDecimal tax;
	private BigDecimal amountAftTax;

	// 배당 방식: CASH, CROP
	private String dividendType;

	// 배당 상태: POLLING, DECIDED, COMPLETED
	private String status;

	// 실제 지급 금액 (작물 선택 시 0원 또는 차액)
	private BigDecimal paidAmount;

	private OffsetDateTime pollEndDate;
	private OffsetDateTime selectionAt;

	// 메일 전송시 필요
	private String userEmail;
	private String userName;

	// 배당 내역 증권사 기록시 필요
	private Long tokenId;
	private Long walletId;
}