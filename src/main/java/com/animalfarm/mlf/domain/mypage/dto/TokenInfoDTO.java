package com.animalfarm.mlf.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TokenInfoDTO {

	private Long externalRefId;
	private String tokenName;
	private String tickerSymbol;
}
