package com.animalfarm.mlf.domain.token.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenListDTO {
	private Long tokenId;						// 토큰 고유 번호
	private String tokenName;					// 토큰 이름
	private String tickerSymbol;				// 종목 코드
	private BigDecimal marketPrice;				// 현재가
	private BigDecimal dailyTradeVolume;		// 거래 대금
	private BigDecimal changeRate;				// 등락률
}
