package com.animalfarm.mlf.domain.subscription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.common.http.ApiResponse;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionApplicationDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {

	@Autowired
	SubscriptionService subscriptionService;

	@PostMapping("/cancel")
	public ResponseEntity<ApiResponse<Object>> cancelSubscription(@RequestBody
	Long projectId) {
		try {
			boolean isSuccess = subscriptionService.selectAndCancel(projectId);

			if (isSuccess) {
				// 디자인 가이드에 따른 성공 메시지 반환
				return ResponseEntity.ok(ApiResponse.message("청약 취소가 완료되었습니다."));
			} else {
				// 실패 시 처리
				return ResponseEntity.badRequest().body(ApiResponse.message("청약 취소에 실패했습니다. 내역을 확인해주세요."));
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(ApiResponse.message(e.getMessage()));
		}
	}

	@PostMapping("/application")
	public ResponseEntity<String> applicationSubscription(@RequestBody
	SubscriptionApplicationDTO subscriptionInsertDTO) {
		if (subscriptionService.subscriptionApplication(subscriptionInsertDTO)) {
			try {
				subscriptionService.postApplication(subscriptionInsertDTO);
				return ResponseEntity.ok("success");
			} catch (Exception e) {
				log.error("증권사 전송 중 오류 발생: {}", e.getMessage());
				if ("empty_payload".equals(e.getMessage())) {
					return ResponseEntity.ok("empty_payload");
				}
				return ResponseEntity.ok("api_fail");
			}
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("청약 신청 중 서버 오류가 발생했습니다.");
		}
	}

}
