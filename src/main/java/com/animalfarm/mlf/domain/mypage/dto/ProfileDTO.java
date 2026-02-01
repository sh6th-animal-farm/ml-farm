package com.animalfarm.mlf.domain.mypage.dto;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDTO {

	private String userName;
	private String investorType;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	private OffsetDateTime createdAt;
	private String email;
	private String phoneNumber;
	private String address;
	private Boolean pushYn;
	private Boolean receiveEmailYn;

}
