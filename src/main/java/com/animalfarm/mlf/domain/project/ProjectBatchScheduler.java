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

import com.animalfarm.mlf.domain.project.dto.ProjectDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectStatusDTO;
import com.animalfarm.mlf.domain.subscription.SubscriptionService;
import com.animalfarm.mlf.domain.token.TokenService;

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
	

	@Scheduled(cron = "0 * * * * *")
	public void runBatch() {
		List<ProjectStatusDTO> status = projectService.selectStatus();
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
                	Long tokenId = tokenService.selectByProjectId(projectId).getTokenId();
                    log.info(">>> 프로젝트 종료 배치 시작: ProjectID={}, TokenId={}", projectId, tokenId);

                    JobParameters jobParameters = new JobParametersBuilder()
                            .addLong("projectId", projectId)
                            .addLong("tokenId", tokenId)
                            .addString("datetime", LocalDateTime.now().toString())
                            .toJobParameters();

                    jobLauncher.run(projectClosingJob, jobParameters);

                } catch (Exception e) {
                    log.error(">>> 프로젝트 종료 배치 실행 중 에러 발생: {}", e.getMessage());
                }
        	}
        } else {
            log.info(">>> 현재 종료 대상 프로젝트가 없습니다.");
        }
    }
	
}
