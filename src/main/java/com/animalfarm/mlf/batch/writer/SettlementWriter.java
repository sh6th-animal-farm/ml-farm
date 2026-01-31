package com.animalfarm.mlf.batch.writer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
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
	private final SqlSessionFactory sqlSessionFactory;

	@Override
	public void write(List<? extends RevenueSummaryDTO> items) throws Exception {
		try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
			RevenueSummaryRepository summaryRepo = sqlSession.getMapper(RevenueSummaryRepository.class);
			RevenueRepository revRepo = sqlSession.getMapper(RevenueRepository.class);
			ExpenseRepository expRepo = sqlSession.getMapper(ExpenseRepository.class);

			for (RevenueSummaryDTO summary : items) {
				summaryRepo.insertSummary(summary); // 1. INSERT

				// INSERT를 즉시 실행해서 DB로부터 rsId를 받아옴. 이거 없으면 rsId 전달 안됨.
				sqlSession.flushStatements();

				Map<String, Object> params = new HashMap<>();
				params.put("rsId", summary.getRsId());
				params.put("projectId", summary.getProjectId());

				revRepo.updateSummaryId(params); // 2. UPDATE Revenue
				expRepo.updateSummaryId(params); // 3. UPDATE Expense
			}
			sqlSession.flushStatements();
		}
	}
}