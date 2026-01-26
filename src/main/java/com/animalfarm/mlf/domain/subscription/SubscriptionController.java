package com.animalfarm.mlf.domain.subscription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.common.http.ApiResponse;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionInsertDTO;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionSelectDTO;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {

	@Autowired
	SubscriptionService subscriptionService;

	@PostMapping("/cancel")
	public ResponseEntity<ApiResponse<Object>> cancelSubscription(@RequestBody
	SubscriptionSelectDTO subscriptionSelectDTO) {
		boolean isSuccess = subscriptionService.selectAndCancel(subscriptionSelectDTO);

		if (isSuccess) {
			// 디자인 가이드에 따른 성공 메시지 반환
			return ResponseEntity.ok(ApiResponse.message("청약 취소가 완료되었습니다."));
		} else {
			// 실패 시 처리
			return ResponseEntity.badRequest().body(ApiResponse.message("청약 취소에 실패했습니다. 내역을 확인해주세요."));
		}
	}

	@PostMapping("/application")
	public ResponseEntity<ApiResponse<Object>> applicationSubscription(@RequestBody
	SubscriptionInsertDTO subscriptionInsertDTO) {
		subscriptionService.subscriptionApplication(subscriptionInsertDTO);
		return ResponseEntity.ok(ApiResponse.message("청약 취소가 완료되었습니다."));
	}

}
