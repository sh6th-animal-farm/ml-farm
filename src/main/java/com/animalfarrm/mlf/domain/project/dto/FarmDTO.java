package com.animalfarrm.mlf.domain.project.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

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
public class FarmDTO {
	private Long farmId; // 농가 ID (PK)
	private String farmName; // 농가 이름

	// 주소 정보
	private String addressSido; // 시/도
	private String addressSigungu; // 시/군/구
	private String addressStreet; // 도로명 주소
	private String addressDetails; // 상세 주소

	// 위치 정보 (GPS)
	private BigDecimal latitude; // 위도 (DECIMAL(11,8))
	private BigDecimal longtitude; // 경도 (DECIMAL(11,8))
	private BigDecimal altitude; // 고도 (DECIMAL(11,8))

	private String farmType; // 농가 유형 (VARCHAR)
	private BigDecimal area; // 면적 (DECIMAL(15,2))
	private String description; // 농가 설명 (VARCHAR)
	private String thumbnailUrl; // 대표 이미지 URL

	private ZonedDateTime openAt; // 농가 개업/오픈 일시

	private List<FarmEnvDataDTO> envDatas;
}
