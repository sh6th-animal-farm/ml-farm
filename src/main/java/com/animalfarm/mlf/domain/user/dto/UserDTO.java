package com.animalfarm.mlf.domain.user.dto;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

	private Long userId;
	private String userName;
	private String email;
	private String password;
	private String phoneNumber;

	private String role;
	private String brn;
	private String investorType;

	private Boolean pushYn;
	private Boolean receiveEmailYn;

	private OffsetDateTime createdAt;
	private OffsetDateTime updatedAt;
	private OffsetDateTime deletedAt;
}
