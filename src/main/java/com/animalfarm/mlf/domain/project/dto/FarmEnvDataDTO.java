package com.animalfarm.mlf.domain.project.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

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
public class FarmEnvDataDTO {
	private Long feId; // 환경 데이터 ID (PK)
	private Long farmId; // 농가 ID (FK)

	// 내부 환경 정보
	private BigDecimal humidityInside; // 내부 습도 (%)
	private BigDecimal temperatureInside; // 내부 온도 (℃)

	// 일사량 정보
	private BigDecimal solarRadiation; // 일사량 (W/㎡)

	// 외부 환경 정보
	private BigDecimal humidityOutside; // 외부 습도 (%)
	private BigDecimal temperatureOutside; // 외부 온도 (℃)

	// 데이터 수집 시점 (DB 스키마에 따라 추가 가능)
	private ZonedDateTime createdAt;
}
