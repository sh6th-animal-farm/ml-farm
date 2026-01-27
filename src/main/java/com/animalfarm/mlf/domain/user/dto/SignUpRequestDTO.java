package com.animalfarm.mlf.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequestDTO {

	private String userName;
	private String email;
	private String password;
	private String phoneNumber;
	private String brn;
	private String role;
}
