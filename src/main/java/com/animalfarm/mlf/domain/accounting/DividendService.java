package com.animalfarm.mlf.domain.accounting;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animalfarm.mlf.common.security.SecurityUtil;
import com.animalfarm.mlf.domain.accounting.dto.DividendDTO;
import com.animalfarm.mlf.domain.accounting.dto.RevenueSummaryDTO;
import com.animalfarm.mlf.domain.token.TokenRepository;
import com.animalfarm.mlf.domain.token.dto.TokenDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DividendService {
	private final JobLauncher jobLauncher;
	private final Job dividendJob;
	private final Job emailJob;
	private final RevenueSummaryRepository summaryRepo; // 정산 데이터 조회용
	private final TokenRepository tokenRepository; // 토큰 발행량 조회용
	private final DividendRepository dividendRepository;

	public void runDividendBatch(Long projectId) throws Exception {
		// rsId를 기반으로 정산 요약 정보 조회 (DB에서 직접 가져옴)
		RevenueSummaryDTO summary = summaryRepo.selectByProjectId(projectId);

		if (summary == null) {
			throw new RuntimeException("정산 대기 중인 내역이 없습니다.");
		}
		// 해당 프로젝트의 토큰 정보 조회
		TokenDTO token = tokenRepository.selectByProjectId(projectId);

		if (token == null) {
			throw new RuntimeException("해당 프로젝트에 발행된 토큰이 없습니다.");
		}

		// 배치 파라미터 구성
		JobParameters params;
		try {
			params = new JobParametersBuilder()
				.addLong("requestTime", System.currentTimeMillis())
				.addLong("projectId", summary.getProjectId())
				.addLong("rsId", summary.getRsId())
				// BigDecimal -> Double 변환 (JobParameter 제약 때문)
				.addDouble("totalAmount", summary.getNetProfit().doubleValue())
				.addDouble("totalIssueVolume", token.getTotalSupply().doubleValue())
				.toJobParameters();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("파라미터 전달 오류");
		}

		// 배치 실행
		jobLauncher.run(dividendJob, params);
	}

	public void sendEmail() throws Exception {
		JobParameters params = new JobParametersBuilder()
			.addLong("requestTime", System.currentTimeMillis())
			.toJobParameters();
		jobLauncher.run(emailJob, params);
	}

	public DividendDTO getDividendByID(Long dividendId) {
		return dividendRepository.selectById(dividendId);
	}

	@Transactional
	public void processUserSelection(Long dividendId, String dividendType) throws Exception {
		Long curUserId = SecurityUtil.getCurrentUserId();
		DividendDTO dividend = getDividendByID(dividendId);
		if (!curUserId.equals(dividend.getUserId())) {
			throw new Exception("투자자 본인만 결정할 수 있습니다.");
		}
		dividendRepository.updateUserSelection(dividendId, dividendType);
	}

}
