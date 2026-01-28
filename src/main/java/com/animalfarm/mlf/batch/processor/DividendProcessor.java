package com.animalfarm.mlf.batch.processor;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.animalfarm.mlf.domain.accounting.dto.DividendDTO;
import com.animalfarm.mlf.domain.accounting.dto.DividendResponseDTO;

@Component
public class DividendProcessor implements ItemProcessor<DividendResponseDTO, DividendDTO> {

	private final BigDecimal totalDividendAmount; // 정산 요약에서 나온 net_profit
	private final BigDecimal totalIssueVolume; // 토큰 전체 발행량

	public DividendProcessor(
		@Value("#{jobParameters[totalAmount]}")
		BigDecimal totalDividendAmount,
		@Value("#{jobParameters[totalIssueVolume]}")
		BigDecimal totalIssueVolume) {
		this.totalDividendAmount = totalDividendAmount;
		this.totalIssueVolume = totalIssueVolume;
	}

	@Override
	public DividendDTO process(DividendResponseDTO snap) throws Exception {

		BigDecimal taxRate = new BigDecimal("0.154");
		BigDecimal amountBfTax = totalDividendAmount
			.multiply(snap.getTokenBalance())
			.divide(totalIssueVolume, 0, RoundingMode.FLOOR);

		// 2. 세금 계산 (정수화)
		BigDecimal tax = amountBfTax.multiply(taxRate).setScale(0, RoundingMode.FLOOR);

		// 3. 세후 실지급액
		BigDecimal amountAftTax = amountBfTax.subtract(tax);

		return DividendDTO.builder()
			.userId(snap.getUserId())
			.amountBfTax(amountBfTax)
			.tax(tax)
			.amountAftTax(amountAftTax)
			.dividendType("CASH")
			.build();
	}
}