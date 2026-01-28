package com.animalfarm.mlf.batch.writer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.animalfarm.mlf.domain.accounting.ExpenseRepository;
import com.animalfarm.mlf.domain.accounting.RevenueRepository;
import com.animalfarm.mlf.domain.accounting.RevenueSummaryRepository;
import com.animalfarm.mlf.domain.accounting.dto.RevenueSummaryDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SettlementWriter implements ItemWriter<RevenueSummaryDTO> {
	private final RevenueSummaryRepository summaryRepository;
	private final RevenueRepository revenueRepository;
	private final ExpenseRepository expenseRepository;

	@Override
	public void write(List<? extends RevenueSummaryDTO> items) throws Exception {
		for (RevenueSummaryDTO summary : items) {
			// 요약 테이블 INSERT
			summaryRepository.insertSummary(summary);

			// 해당 프로젝트의 정산 안 된 원본들에 summary_id 업데이트
			Map<String, Object> params = new HashMap<>();
			params.put("summaryId", summary.getRsId());
			params.put("projectId", summary.getProjectId());

			expenseRepository.updateSummaryId(params);
			revenueRepository.updateSummaryId(params);
		}
	}
}