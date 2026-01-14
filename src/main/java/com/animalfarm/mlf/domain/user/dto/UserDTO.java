package com.animalfarm.mlf.domain.user.dto;

import java.time.OffsetDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {

	private Long userId;
	private String userName;
	private String email;
	private String password;
	private String phoneNumber;

	private OffsetDateTime createdAt;
	private OffsetDateTime updatedAt;
	private OffsetDateTime deletedAt;

	private String role;
	private String brn;
	private String investorType;

	private Boolean pushYn;
	private Boolean receiveEmailYn;
}
