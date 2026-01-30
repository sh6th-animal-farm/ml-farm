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
@NoArgsConstructor
@AllArgsConstructor
public class TradePriceDTO {
	BigDecimal price;         // 체결 금액
	BigDecimal volume;        // 체결 수량
	String takerSide;              // 주문 방향
	OffsetDateTime createdAt; // 체결 시각
}
