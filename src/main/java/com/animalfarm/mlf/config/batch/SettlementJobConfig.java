package com.animalfarm.mlf.config.batch;

import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.animalfarm.mlf.batch.processor.SettlementProcessor;
import com.animalfarm.mlf.batch.writer.SettlementWriter;
import com.animalfarm.mlf.domain.accounting.dto.RevenueSummaryDTO;

@Configuration
@EnableBatchProcessing // 배치 인프라 활성화
public class SettlementJobConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	@Autowired
	private SettlementProcessor settlementProcessor;

	@Autowired
	private SettlementWriter settlementWriter;

	@Autowired
	private PlatformTransactionManager transactionManager; // 추가

	// [JOB] 정산 작업의 시작점
	@Bean
	public Job settlementJob() {
		return jobBuilderFactory.get("settlementJob")
			.start(createSummaryStep())
			.build();
	}

	//	@Bean
	//	public MyBatisBatchItemWriter<RevenueSummaryDTO> settlementWriter() {
	//		return new MyBatisBatchItemWriterBuilder<RevenueSummaryDTO>()
	//			.sqlSessionFactory(sqlSessionFactory)
	//			.statementId("com.animalfarm.mlf.domain.accounting.RevenueSummaryRepository.insertSummary")
	//			.build();
	//	}

	// [STEP] 실제 로직이 수행되는 단위
	@Bean
	public Step createSummaryStep() {
		return stepBuilderFactory.get("createSummaryStep")
			.<Map<String, Object>, RevenueSummaryDTO>chunk(10) // 10개 단위로 트랜잭션 처리 => @Transactional 필요없음
			.reader(revenueExpenseReader()) // 읽기: 정산 안 된 데이터들
			.processor(settlementProcessor) // 처리: 합산 및 순이익 계산
			.writer(settlementWriter) // 쓰기: 요약 테이블 INSERT & 마킹
			.transactionManager(transactionManager)
			.build();
	}

	@Bean
	public MyBatisPagingItemReader<Map<String, Object>> revenueExpenseReader() {
		return new MyBatisPagingItemReaderBuilder<Map<String, Object>>()
			.sqlSessionFactory(sqlSessionFactory)
			.queryId("com.animalfarm.mlf.domain.accounting.RevenueSummaryRepository.selectSettlementTargets")
			.pageSize(10)
			.build();
	}
}