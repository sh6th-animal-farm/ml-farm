package com.animalfarm.mlf.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnterpriseVerifyRequestDTO {
	@JsonProperty("bNo")
	private String bNo; // 사업자 번호

	private String startDt; // 사업 시작일
	private String pNm; // 대표 이름
	private String bNm; // 사업자명
}
