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

	private final RevenueSummaryRepository summaryRepo;
	private final RevenueRepository revRepo;
	private final ExpenseRepository expRepo;

	@Override
	public void write(List<? extends RevenueSummaryDTO> items) throws Exception {
		for (RevenueSummaryDTO summary : items) {
			summaryRepo.insertSummary(summary); // 1. INSERT

			Map<String, Object> params = new HashMap<>();
			params.put("rsId", summary.getRsId());
			params.put("projectId", summary.getProjectId());

			revRepo.updateSummaryId(params); // 2. UPDATE Revenue
			expRepo.updateSummaryId(params); // 3. UPDATE Expense
		}
	}
}