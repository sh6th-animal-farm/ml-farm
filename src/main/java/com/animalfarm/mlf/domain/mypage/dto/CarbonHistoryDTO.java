package com.animalfarm.mlf.domain.mypage.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarbonHistoryDTO {

	private String cpType; // 상품 유형 (REMOVAL, REDUCTION)
	private String projectName; // 프로젝트 정보
	private String dateStr; // 구매일
	private String endDateStr; // 계산된 만료일 (date + 1년)
	private BigDecimal amount; // 구매량
	private BigDecimal price; // discounted_price (최종 금액)
}
