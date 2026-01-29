package com.animalfarm.mlf.domain.carbon.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarbonOrderCompleteDTO {
	private String impUid;
	private String merchantUid;
	private Long cpId;
	private BigDecimal amount;
}
