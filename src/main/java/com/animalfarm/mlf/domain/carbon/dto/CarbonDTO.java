package com.animalfarm.mlf.domain.carbon.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

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
public class CarbonDTO {

	private Long cpId;
	private Long projectId;
	private OffsetDateTime createAt;
	private String productCertificate;
	private BigDecimal cpAmount;
	private String cpType; // ENUM: REMOVAL, REDUCTION
	private String cpDetail;
	private BigDecimal cpPrice;
	private BigDecimal initAmount;
	private String vintageYear;
	private String cpTitle;
}
