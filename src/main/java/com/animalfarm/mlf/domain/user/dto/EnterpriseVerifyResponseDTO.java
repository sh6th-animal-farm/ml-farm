package com.animalfarm.mlf.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnterpriseVerifyResponseDTO {
	private boolean verified;
	private String status;
	private String message;

	// 디버깅용
	@JsonIgnore
	private String statusRaw;

	@JsonIgnore
	private String validateRaw;

}
