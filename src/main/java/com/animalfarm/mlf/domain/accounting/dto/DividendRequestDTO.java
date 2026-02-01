package com.animalfarm.mlf.domain.accounting.dto;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@ApiModel(value = "DividendRequestDTO", description = "배당금 지급 요청 정보")
public class DividendRequestDTO {
	private Long dividendId;
	private Long walletId;
	private Long tokenId;
	private Long userId;
	private BigDecimal beforeTaxAmount;
	private BigDecimal afterTaxAmount;

	// DividendDTO를 증권사 포맷으로 변환하는 정적 메서드
	public static DividendRequestDTO from(DividendDTO dividendDTO) {
		return DividendRequestDTO.builder()
			.dividendId(dividendDTO.getDividendId())
			.walletId(dividendDTO.getWalletId())
			.tokenId(dividendDTO.getTokenId())
			.userId(dividendDTO.getUserId())
			.beforeTaxAmount(dividendDTO.getAmountBfTax())
			.afterTaxAmount(dividendDTO.getAmountAftTax())
			.build();
	}
}
