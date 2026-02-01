package com.animalfarm.mlf.domain.token.dto;

import java.math.BigDecimal;

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
public class OrderDTO {
	private Long walletId;            // 지갑 번호
	private Long tokenId;             // 토큰 고유 번호
	private String orderSide;         // 주문 방향(BUY, SELL)
	private String orderType;         // 주문 유형(LIMIT, MARKET)
	private BigDecimal orderPrice;    // 주문 단가 (시장가는 0)
	private BigDecimal orderVolume;   // 주문 수량 (시장가 매수는 0)
	private BigDecimal totalPrice;    // 주문 총액 (매도는 0)
}
