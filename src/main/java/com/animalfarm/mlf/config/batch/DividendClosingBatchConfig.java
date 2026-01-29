package com.animalfarm.mlf.config.batch;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.animalfarm.mlf.domain.accounting.DividendService;
import com.animalfarm.mlf.domain.accounting.dto.DividendDTO;
import com.animalfarm.mlf.domain.accounting.dto.DividendRequestDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DividendClosingBatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SqlSessionFactory sqlSessionFactory;
    private final DividendService dividendService;

    @Bean
    public Job dividendClosingJob() {
        return jobBuilderFactory.get("dividendClosingJob")
                .start(autoDecideStep()) // 1단계: 미응답자 자동 확정
                .next(sendToBrokerageStep()) // 2단계: 증권사 API 전송
                .build();
    }

    // --- Step 1: 미응답자 자동 확정 (Tasklet) ---
    @Bean
    public Step autoDecideStep() {
        return stepBuilderFactory.get("autoDecideStep")
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
                .build();
    }

    // --- Step 2: 증권사 API 전송 (Chunk) ---
    @Bean
    public Step sendToBrokerageStep() {
        return stepBuilderFactory.get("sendToBrokerageStep")
                .<DividendDTO, DividendRequestDTO>chunk(100) // 100건씩 끊어서 처리
                .reader(dividendReader())
                .processor((ItemProcessor<DividendDTO, DividendRequestDTO>) DividendRequestDTO::from)
                .writer(dividendWriter())
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(3) // API 실패 시 3번까지 재시도
                .build();
    }

    @Bean
    public MyBatisCursorItemReader<DividendDTO> dividendReader() {
        return new MyBatisCursorItemReaderBuilder<DividendDTO>()
                .sqlSessionFactory(sqlSessionFactory)
                .queryId("com.animalfarm.mlf.repository.DividendRepository.findAllDecidedForApi")
                .build();
    }

    @Bean
    public ItemWriter<DividendRequestDTO> dividendWriter() {
    	return items -> {
            // 그룹화하여 전송
            Map<Long, List<DividendRequestDTO>> grouped = items.stream()
                .collect(Collectors.groupingBy(DividendRequestDTO::getTokenId));

            for (Map.Entry<Long, List<DividendRequestDTO>> entry : grouped.entrySet()) {
                log.info("TokenId: {} 에 대해 {}건 전송 시작", entry.getKey(), entry.getValue().size());
                
                try {
                    dividendService.sendDividendData(entry.getKey(), entry.getValue());
                } catch (Exception e) {
                    log.error("API 전송 중 치명적 에러 발생: {}", e.getMessage());
                    // 여기서 예외를 던져야 배치의 retryLimit(3)이 작동합니다.
                    throw new RuntimeException("증권사 API 전송 실패", e);
                }
            }
        };
    		// 전송 후 디비 전송 완료 표시,처리
        
    }
}