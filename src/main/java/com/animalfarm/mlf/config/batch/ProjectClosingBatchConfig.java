package com.animalfarm.mlf.config.batch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.animalfarm.mlf.batch.DividendBatchService;
import com.animalfarm.mlf.batch.processor.RefundAfterBurnProcessor;
import com.animalfarm.mlf.batch.writer.ProjectClosingWriter;
import com.animalfarm.mlf.domain.accounting.dto.RefundTokenLedgerDTO;
import com.animalfarm.mlf.domain.project.ProjectClosingService;
import com.animalfarm.mlf.domain.refund.RefundDTO;
import com.animalfarm.mlf.domain.token.TokenBurnTasklet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ProjectClosingBatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final TokenBurnTasklet tokenBurnTasklet;
    private final ProjectClosingWriter projectClosingWriter;
    private final RefundAfterBurnProcessor refundProcessor;
    
    private final ProjectClosingService projectClosingService;

    @Bean
    public Job projectClosingJob() {
        return jobBuilderFactory.get("projectClosingJob")
                .start(executeFinalDividendStep())   // 1. 배당
                .next(requestTokenBurnStep())        // 2. 소각 API
                .next(updateFinalStatusStep())       // 3. 환불 내역, 토큰 원장 기록
                .next(completeProjectStep())         // 4. 프로젝트 상태 변경
                .build();
    }

    // 최종 배당 (Tasklet 방식)
    @Bean
    public Step executeFinalDividendStep() {
        return stepBuilderFactory.get("executeFinalDividendStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>> 최종 배당 작업을 실시합니다.");
                    Long projectId = (Long) chunkContext.getStepContext().getJobParameters().get("projectId");
                    projectClosingService.processFinalDividends(projectId);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    // 토큰 소각 API 요청 (Tasklet 방식)
    @Bean
    public Step requestTokenBurnStep() {
        return stepBuilderFactory.get("requestTokenBurnStep")
                .tasklet(tokenBurnTasklet)
                .build();
    }

    // DB 최종 정리
    @Bean
    public Step updateFinalStatusStep() {
    	return stepBuilderFactory.get("updateFinalStatusStep")
                .<RefundDTO, RefundTokenLedgerDTO>chunk(100)
                .reader(refundListReader(null))
                .processor(refundProcessor)  
                .writer(projectClosingWriter)
                .build();
    }
    
    @Bean
    public Step completeProjectStep() {
        return stepBuilderFactory.get("completeProjectStep")
                .tasklet((contribution, chunkContext) -> {
                    Long projectId = (Long) chunkContext.getStepContext().getJobParameters().get("projectId");
                    Long tokenId = (Long) chunkContext.getStepContext().getJobParameters().get("tokenId");
                    log.info(">>> 프로젝트 {} 상태를 COMPLETED로 변경합니다.", projectId);
                    projectClosingService.updateProjectAndTokenStatus(projectId, tokenId);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
    
    @Bean
    @StepScope
    public ListItemReader<RefundDTO> refundListReader(
        @Value("#{jobExecutionContext['refundList']}") List<RefundDTO> refundList) {
        return new ListItemReader<>(refundList != null ? refundList : new ArrayList<>());
    }
}