package com.animalfarm.mlf.config.batch;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.animalfarm.mlf.common.MailService;
import com.animalfarm.mlf.domain.accounting.DividendRepository;
import com.animalfarm.mlf.domain.accounting.dto.DividendDTO;

@Configuration
public class EmailBatchConfig {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	@Autowired
	private DividendRepository dividendRepository;
	@Autowired
	private MailService mailService; // 스프링 메일 설정 필요

	@Bean
	public Job emailJob() {
		return jobBuilderFactory.get("emailJob")
			.start(sendEmailStep())
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

	@Bean
	public MyBatisPagingItemReader<DividendDTO> emailTargetReader() {
		return new MyBatisPagingItemReaderBuilder<DividendDTO>()
			.sqlSessionFactory(sqlSessionFactory)
			.queryId("com.animalfarm.mlf.domain.accounting.DividendRepository.selectPollingList")
			.pageSize(10)
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