package com.animalfarm.mlf.config.batch;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.animalfarm.mlf.batch.processor.DividendProcessor;
import com.animalfarm.mlf.domain.accounting.dto.DividendDTO;
import com.animalfarm.mlf.domain.accounting.dto.DividendResponseDTO;
import com.animalfarm.mlf.domain.project.ProjectService;

@Configuration
@EnableBatchProcessing
public class DividendJobConfig {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	@Autowired
	private ProjectService projectService;

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
			.start(calculateDividendStep())
			.build();
	}

	@Bean
	public Step calculateDividendStep() {
		return stepBuilderFactory.get("calculateDividendStep")
			.<DividendResponseDTO, DividendDTO>chunk(100)
			.reader(dividendListItemReader(null, null)) // 파라미터 주입
			.processor(dividendProcessor(null, null))
			.writer(dividendWriter())
			.build();
	}

	@Bean
	@StepScope
	public ListItemReader<DividendResponseDTO> dividendListItemReader(
		@Value("#{jobParameters[projectId]}")
		Long projectId,
		@Value("#{jobParameters[rsId]}")
		Long rsId) {

		// 외부 API 호출
		List<DividendResponseDTO> snapshot = projectService.getDividendSnapshot(projectId);

		// 모든 DTO에 rsId 세팅
		snapshot.forEach(s -> s.setRsId(rsId));

		return new ListItemReader<>(snapshot);
	}

	@Bean
	public MyBatisBatchItemWriter<DividendDTO> dividendWriter() {
		return new MyBatisBatchItemWriterBuilder<DividendDTO>()
			.sqlSessionFactory(sqlSessionFactory)
			.statementId("com.animalfarm.mlf.domain.accounting.DividendRepository.insertDividend")
			.build();
	}
}
