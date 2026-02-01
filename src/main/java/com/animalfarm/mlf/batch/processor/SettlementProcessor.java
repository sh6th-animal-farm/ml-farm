package com.animalfarm.mlf.batch.processor;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.animalfarm.mlf.domain.accounting.dto.RevenueSummaryDTO;

@Component
public class SettlementProcessor implements ItemProcessor<Map<String, Object>, RevenueSummaryDTO> {
	@Override
	public RevenueSummaryDTO process(Map<String, Object> item) throws Exception {
		Long projectId = (Long)item.get("projectId");
		BigDecimal totalRev = (BigDecimal)item.get("totalRevenue");
		BigDecimal totalExp = (BigDecimal)item.get("totalExpense");

		// 여기서 순이익 계산
		return RevenueSummaryDTO.builder()
			.projectId(projectId)
			.totalRevenue(totalRev)
			.totalExpense(totalExp)
			.netProfit(totalRev.subtract(totalExp))
			.build();
	}
}