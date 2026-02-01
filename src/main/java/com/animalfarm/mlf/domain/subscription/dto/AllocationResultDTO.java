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
public class AllocationResultDTO {
	// 청약 정산 Response
	private Long walletId;
	private Long passTxId; // 청약 당첨된 거래내역 번호 (증권사 체결 번호)
	private Long failTxId; // 환불 처리된 거래내역 번호 (증권사 체결 번호)
	private BigDecimal passVolume; // 지급된 토큰 개수
	private BigDecimal passAmount; // 토큰 개당 가격 * 지급된 토큰 개수
									// (-> 청약 신청 금액에서 이 값 빼서 환불 금액 계산 부탁드립니다...)
}
