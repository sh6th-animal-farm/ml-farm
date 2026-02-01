package com.animalfarm.mlf.batch;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.domain.project.ProjectBatchScheduler;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/batch")
@RequiredArgsConstructor
public class BatchController {

	final DividendBatchService dividendBatchService;
	final ProjectBatchScheduler projectBatchScheduler;

	@GetMapping("/dividend/settlement")
	public String runSettlement() {
		return dividendBatchService.runSettlementBatch();
	}

	@GetMapping("/dividend/run")
	public ResponseEntity<String> runDividend(@RequestParam
	Long projectId) {
		try {
			dividendBatchService.runDividendBatch(projectId);
			return ResponseEntity.ok("성공했습니다. DB를 확인해주세요.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@GetMapping("/dividend/email")
	public ResponseEntity<String> runSendEmail() {
		try {
			dividendBatchService.runEmailBatch();
			return ResponseEntity.ok("성공했습니다. DB를 확인해주세요.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@GetMapping("/dividend/close")
	public ResponseEntity<String> runCloseDividend() {
		try {
			dividendBatchService.runDividendClosingBatch();
			return ResponseEntity.ok("성공했습니다. DB를 확인해주세요.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	@GetMapping("/project/close")
	public ResponseEntity<String> runCloseProject() {
		try {
			projectBatchScheduler.runProjectClosingJob();
			return ResponseEntity.ok("성공했습니다. DB를 확인해주세요.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

}