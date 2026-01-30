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
@NoArgsConstructor
@AllArgsConstructor
public class OrderPriceDTO {
	BigDecimal price;       // 호가
	BigDecimal totalVolume; // 총 주문수량
	String side;            // BUY or SELL
}
