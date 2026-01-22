package com.animalfarm.mlf.domain.carbon.dto;

import java.math.BigDecimal;

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
public class CarbonListDTO {

	private Long cpId;
	private Long projectId;

	private String cpTitle;
	private String category; // <= cp_type과 매핑 (제거형/감축형)

	private BigDecimal cpPrice;
	private BigDecimal cpAmount;

	private String productCertificate;
	private String vintageYear;
	private String cpDetail;
}
