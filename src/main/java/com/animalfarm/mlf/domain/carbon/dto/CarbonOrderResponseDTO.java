package com.animalfarm.mlf.domain.carbon.dto;

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
public class CarbonOrderResponseDTO {
	private Long cpId;
	private String cpTitle;

	private BigDecimal orderAmount;

	private BigDecimal unitPrice;
	private BigDecimal supplyAmount;
	private BigDecimal vatAmount;
	private BigDecimal totalAmount;

	private BigDecimal userMaxLimit; // 최대 구매 가능 수량
	private BigDecimal remainAmount; // 상품 잔여 수량
	private BigDecimal discountRate;

}
