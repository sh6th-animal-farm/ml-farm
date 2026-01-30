package com.animalfarm.mlf.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/test/batch")
@RequiredArgsConstructor
public class BatchTestController {

	private final JobLauncher jobLauncher;
	private final Job settlementJob; // JobConfig에서 만든 빈 이름

	@GetMapping("/runSettlement")
	public String runSettlement() {
		try {
			// 배치는 동일한 파라미터로 실행하면 '이미 성공했다'고 판단해 실행되지 않음
			// 테스트를 위해 실행 시간을 파라미터로 넣어 매번 새로운 Job으로 인식하게 함
			JobParameters params = new JobParametersBuilder()
				.addLong("requestTime", System.currentTimeMillis())
				.toJobParameters();

			jobLauncher.run(settlementJob, params);
			return "Batch Job Started! Check your logs.";
		} catch (Exception e) {
			e.printStackTrace();
			return "Batch Job Failed: " + e.getMessage();
		}
	}

}