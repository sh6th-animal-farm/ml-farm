package com.animalfarm.mlf.config.batch;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.animalfarm.mlf.batch.processor.DividendProcessor;
import com.animalfarm.mlf.batch.processor.SettlementProcessor;
import com.animalfarm.mlf.batch.writer.SettlementWriter;
import com.animalfarm.mlf.common.MailService;
import com.animalfarm.mlf.domain.accounting.DividendRepository;
import com.animalfarm.mlf.domain.accounting.dto.DividendDTO;
import com.animalfarm.mlf.domain.accounting.dto.RevenueSummaryDTO;
import com.animalfarm.mlf.domain.accounting.dto.SnapshotResponseDTO;
import com.animalfarm.mlf.domain.project.ProjectService;

@Configuration
@EnableBatchProcessing
public class DividendSettlementJobConfig {
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

	@Autowired
	private ProjectService projectService;
	@Autowired
	private MailService mailService; // 스프링 메일 설정 필요

	@Autowired
	private DividendRepository dividendRepository;

	@Bean
	@StepScope // 실행 시점에 JobParameters를 바인딩하기 위해 필수
	public DividendProcessor dividendProcessor(
		@Value("#{jobParameters[totalAmount]}")
		BigDecimal totalAmount,
		@Value("#{jobParameters[totalIssueVolume]}")
		BigDecimal totalIssueVolume) {

		// 이 시점에 JobParameters에 있는 값이 생성자로 주입됩니다.
		return new DividendProcessor(totalAmount, totalIssueVolume);
	}

	@Bean
	public Job dividendJob() {
		return jobBuilderFactory.get("dividendJob")
			.start(createSummaryStep())
			.start(calculateDividendStep())
			.build();
	}

	// ======== Step ========

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
	public Step calculateDividendStep() {
		return stepBuilderFactory.get("calculateDividendStep")
			.<SnapshotResponseDTO, DividendDTO>chunk(100)
			.reader(dividendListItemReader(null, null)) // 파라미터 주입
			.processor(dividendProcessor(null, null))
			.writer(dividendJobWriter())
			.build();
	}

	@Bean
	public Step sendEmailStep() {
		return stepBuilderFactory.get("sendEmailStep")
			.<DividendDTO, DividendDTO>chunk(10) // 10개 단위로 메일 발송
			.reader(emailTargetReader())
			.writer(emailItemWriter())
			.build();
	}

	// ======== Reader ========

	@Bean
	public MyBatisPagingItemReader<Map<String, Object>> revenueExpenseReader() {
		return new MyBatisPagingItemReaderBuilder<Map<String, Object>>()
			.sqlSessionFactory(sqlSessionFactory)
			.queryId("com.animalfarm.mlf.domain.accounting.RevenueSummaryRepository.selectSettlementTargets")
			.pageSize(10)
			.build();
	}

	@Bean
	@StepScope
	public ListItemReader<SnapshotResponseDTO> dividendListItemReader(
		@Value("#{jobParameters[projectId]}")
		Long projectId,
		@Value("#{jobParameters[rsId]}")
		Long rsId) {

		// 외부 API 호출
		List<SnapshotResponseDTO> snapshot = projectService.getDividendSnapshot(projectId);

		// 모든 DTO에 rsId 세팅
		snapshot.forEach(s -> {
			s.setRsId(rsId);
			s.setProjectId(projectId);
		});

		return new ListItemReader<>(snapshot);
	}

	@Bean
	public MyBatisPagingItemReader<DividendDTO> emailTargetReader() {
		return new MyBatisPagingItemReaderBuilder<DividendDTO>()
			.sqlSessionFactory(sqlSessionFactory)
			.queryId("com.animalfarm.mlf.domain.accounting.DividendRepository.selectPollingList")
			.pageSize(10)
			.build();
	}

	// ======== Writer ========

	@Bean
	public MyBatisBatchItemWriter<DividendDTO> dividendJobWriter() {
		return new MyBatisBatchItemWriterBuilder<DividendDTO>()
			.sqlSessionFactory(sqlSessionFactory)
			.statementId("com.animalfarm.mlf.domain.accounting.DividendRepository.insertDividend")
			.build();
	}

	@Bean
	public ItemWriter<DividendDTO> emailItemWriter() {
		return items -> {
			for (DividendDTO item : items) {
				// 메일 발송
				mailService.sendDividendPollEmail(
					item.getUserEmail(), // DTO에 email 필드 추가 필요
					item.getUserName(),
					item.getAmountAftTax().toString(),
					item.getPollEndDate().toString(),
					item.getDividendId());

				// 상태 POLLING으로 수정
				dividendRepository.updateStatusToPolling(item.getDividendId());

				// 0.5초(500ms) 지연 주입
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					// 로그 기록 등 예외 처리
				}
			}
		};
	}
}
