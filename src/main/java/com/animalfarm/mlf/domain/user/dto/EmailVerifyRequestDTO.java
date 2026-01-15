package com.animalfarm.mlf.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerifyRequestDTO {
	private String email;
	private String code;
}
