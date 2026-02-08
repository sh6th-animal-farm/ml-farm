package com.animalfarm.mlf.domain.project;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.animalfarm.mlf.domain.accounting.RevenueSummaryRepository;
import com.animalfarm.mlf.domain.accounting.dto.RevenueSummaryDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectStatusDTO;
import com.animalfarm.mlf.domain.subscription.SubscriptionService;
import com.animalfarm.mlf.domain.token.TokenService;
import com.animalfarm.mlf.domain.token.dto.TokenDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectBatchScheduler {
	@Autowired
	ProjectService projectService;

	@Autowired
	SubscriptionService subscriptionService;
	
	private final TokenService tokenService;
	private final JobLauncher jobLauncher;
	private final Job projectClosingJob;
	private final RevenueSummaryRepository summaryRepo;
	
	// 1분마다 실행
	@Scheduled(cron = "0 * * * * *")
	public void runBatch() {
		projectService.selectStatus();
		subscriptionService.projectStartCheck();
	}
	
	// 매일 자정
	// 프로젝트 종료 작업 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void runProjectClosingJob() {
        List<ProjectDTO> targetProjectList = projectService.selectEndTargetProject();

        if (targetProjectList != null && targetProjectList.size()>0) {
        	for (ProjectDTO targetProject: targetProjectList) {
                try {
                	Long projectId = targetProject.getProjectId();
                	TokenDTO token = tokenService.selectByProjectId(projectId);
                	
                	// DividendBatchService에서 했던 것처럼 정산 요약 정보도 가져와야 함
                    RevenueSummaryDTO summary = summaryRepo.selectByProjectId(projectId); 

                    if (summary == null || token == null) {
                        log.warn(">>> 프로젝트 {}의 정산 정보나 토큰 정보가 없어 건너뜁니다.", projectId);
                        continue;
                    }

                    JobParameters jobParameters = new JobParametersBuilder()
                            .addLong("projectId", projectId)
                            .addLong("tokenId", token.getTokenId())
                            .addLong("rsId", summary.getRsId())
                            .addDouble("totalAmount", summary.getNetProfit().doubleValue())
                            .addDouble("totalIssueVolume", token.getTotalSupply().doubleValue())
                            .addString("datetime", LocalDateTime.now().toString())
                            .toJobParameters();
                    
                    log.info(">>> 프로젝트 종료 배치 시작: ProjectID={}, TokenId={}", projectId, token.getTokenId());

                    jobLauncher.run(projectClosingJob, jobParameters);

                } catch (Exception e) {
                    log.error(">>> 프로젝트 종료 배치 실행 중 에러 발생: {}", e.getMessage());
                }
        	}
        } else {
        	System.out.println(">>> 현재 종료 대상 프로젝트가 없습니다.");
        }
    }
	
}
