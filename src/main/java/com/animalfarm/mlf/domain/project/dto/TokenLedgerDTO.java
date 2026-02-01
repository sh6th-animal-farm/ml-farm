package com.animalfarm.mlf.domain.project.dto;

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
public class TokenLedgerDTO {
	// 1. 식별 정보
	private Long tokenId; // 토큰 번호 (BIGINT)

	// 2. 사용자 정보 (1-3 요구사항 반영: from_user_id는 null 허용 위해 Long 사용)
	private Long fromUserId; // 보낸 사용자 번호 (BIGINT)
	private Long toUserId; // 받은 사용자 번호 (BIGINT)

	// 3. 거래 및 주문 정보
	private String transactionId; // 거래 고유 식별 번호 (VARCHAR(20))
	private Long externalRefId; // 증권사 체결/주문 번호 (VARCHAR(20))
	private BigDecimal orderAmount; // 주문 수량 (DECIMAL(20,4))
	private BigDecimal contractAmount;// 체결 수량 (DECIMAL(20,4))

	// 4. 상태 및 유형 (ENUM 매핑)
	private String status; // 상태 (PENDING, COMPLETED, FAILED, CANCELED)
	private BigDecimal fee; // 거래수수료 (DECIMAL(20,4))
	private String transactionType; // 거래 종류 (ISSUE, SUBSCRIPTION, TRADE 등)

	// 5. 잔액 정보
	private BigDecimal from_balanceAfter; // 송금 후 잔액 (DECIMAL(20,4))
	private BigDecimal to_balanceAfter; // 수금 후 잔액 (DECIMAL(20,4))

	// 6. 무결성 및 보안 (해시 체이닝)
	private String hashValue; // 해시값 (이전해시+현 데이터) (TEXT)
	private String prevHashValue; // 이전해시값인데 이전이 없으니 0

	private BigDecimal totalSupply; // 토큰 총 발행량
}
