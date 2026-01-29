package com.animalfarm.mlf.domain.token.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenPendingDTO {
	private Long orderId;               // 주문 번호
	private String orderSide;           // 주문 방향 (BUY or SELL)
	private BigDecimal orderPrice;      // 주문 가격
	private BigDecimal orderVolume;     // 주문 수량
	private BigDecimal remainingToken;  // 미체결 수량
	private OffsetDateTime createdAt;   // 주문 시각
}
