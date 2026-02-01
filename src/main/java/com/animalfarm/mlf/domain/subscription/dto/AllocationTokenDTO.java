package com.animalfarm.mlf.domain.subscription.dto;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AllocationTokenDTO {
	//최소 구매 금액
	private BigDecimal minAmountPerInvestor;
	//총 모금액
	private BigDecimal actualAmount;
	//실제 청약 참여 인원
	private BigDecimal subscriberCount;
	//기준 금액
	private BigDecimal standardAmount;
	private BigDecimal targetAmount;
	//projectId
	private Long projectId;

	//tokenId
	private Long tokenId;
	//totalSupply
	private BigDecimal totalSupply;

	// DB의 JSONB_AGG 결과를 담는 필드 (필수 추가)
	private String investorList;

	// JSON -> List<InvestorDTO>
	public List<InvestorDTO> getInvestors() {
		if (this.investorList == null) {
			return Collections.emptyList();
		}
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(this.investorList, new TypeReference<List<InvestorDTO>>() {});
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
}
