package com.animalfarm.mlf.batch;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.animalfarm.mlf.domain.accounting.DividendRepository;
import com.animalfarm.mlf.domain.accounting.RevenueSummaryRepository;
import com.animalfarm.mlf.domain.accounting.dto.RevenueSummaryDTO;
import com.animalfarm.mlf.domain.token.TokenRepository;
import com.animalfarm.mlf.domain.token.dto.TokenDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DividendBatchService {
	private final JobLauncher jobLauncher;
	private final Job dividendJob;
	private final Job dividendClosingJob;

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
				.addString("executeDate",
					LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
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

	// 매일 새벽 2시에 실행
	@Scheduled(cron = "0 0 2 * * *")
	public void runDividendClosingBatch() {
		try {
			JobParameters params = new JobParametersBuilder()
				// 배치 실행 이력을 날짜별로 남겨 가독성 확보
				.addString("executeDate",
					LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
				.toJobParameters();

			// 결정 안한 사람 자동으로 확정
			dividendRepository.updateAutoDecide();

			log.info(">>> 배당 확정 및 증권사 전송 배치 시작");
			JobExecution execution = jobLauncher.run(dividendClosingJob, params);

			log.info(">>> 배치 종료 상태: {}", execution.getStatus());

			if (execution.getStatus().isUnsuccessful()) {
				log.error(">>> 배당 배치 실행 실패! 이력을 확인하세요.");
			}

		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
			| JobParametersInvalidException e) {
			log.error(">>> 배치 실행 중 예외 발생: {}", e.getMessage());
		} catch (Exception e) {
			log.error(">>> 기타 예외 발생: {}", e.getMessage());
		}
	}

}
