package com.animalfarm.mlf.domain.refund;

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
public class RefundDTO {

	private Long refundId; // 환불 고유 ID
	private Long userId; // 환불 대상 사용자 ID
	private Long projectId; // 관련 프로젝트 ID (필요 시)
	private Long shId; // 원천 청약 이력 ID (SubscriptionHists 참조)
	private Long uclId; // 증권사 연동 ID
	private Long walletId;

	// 환불 금액: numeric 대응을 위해 BigDecimal 사용
	private BigDecimal amount;

	// 환불 유형: ALL, PARTIAL
	private String refundType;
	// 환불 사유: FAIL_UNDER_70, PRO_RATA_RESIDUE, USER_CANCEL, ADMIN_FORCED, FINAL_SETTLEMENT
	private String reasonCode;
	// 환불 상태: PENDING, INPROGRESS, SUCCESS, FAILED
	private String status;

	// 외부 거래 참조 ID: kh_holdings.transaction_hists의 PK와 매핑
	private Long transactionId;
	private Long externalRefId;

	private OffsetDateTime createdAt; // 환불 발생 일시
	private OffsetDateTime processedAt; // 환불 처리 완료 일시
}