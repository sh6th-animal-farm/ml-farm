package com.animalfarm.mlf.domain.carbon.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//강황증권 API 응답 전용 DTO
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class CarbonDiscountDTO {

	private Long tokenId; // 토큰(프로젝트) 번호
	private BigDecimal myBalance; // 내(기업) 보유량
	private BigDecimal enterpriseTotal; // 기업 총 보유량
}
