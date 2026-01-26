package com.animalfarm.mlf.domain.subscription;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animalfarm.mlf.common.http.ApiResponse;
import com.animalfarm.mlf.common.http.ExternalApiUtil;
import com.animalfarm.mlf.common.security.SecurityUtil;
import com.animalfarm.mlf.domain.refund.RefundDTO;
import com.animalfarm.mlf.domain.refund.RefundRepository;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionHistDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {
	@Autowired
	SubscriptionRepository subscriptionRepository;
	@Autowired
	RefundRepository refundRepository;

	private final ExternalApiUtil externalApiUtil;

	private static final String BASE_URL = "http://54.167.85.125:9090/";

	@Transactional(rollbackFor = Exception.class)
	public boolean selectAndCancel(Long projectId) throws Exception {
		Long userId = SecurityUtil.getCurrentUserId();
		SubscriptionHistDTO subscriptionHistDTO = subscriptionRepository.select(userId, projectId);
		if (subscriptionHistDTO == null) {
			throw new Exception("청약 내역이 존재하지 않습니다.");
		}

		subscriptionHistDTO.setSubscriptionStatus("CANCELED");
		String url = BASE_URL + "/api/project/cancel/" + subscriptionHistDTO.getExternalRefId();

		RefundDTO refundDTO = null;
		try {
			refundDTO = externalApiUtil.callApi(url, HttpMethod.POST, subscriptionHistDTO,
				new ParameterizedTypeReference<ApiResponse<RefundDTO>>() {});

			log.info("[Service] 증권사 청약 취소 완료");
		} catch (RuntimeException e) {
			// 유틸리티에서 던진 구체적인 에러 메시지("잔액 부족" 등)가 이곳으로 전달됨
			log.error("[Service] 청약 취소 실패 사유: {}", e.getMessage());

			// 트랜잭션 롤백을 위해 예외를 다시 던지거나, 사용자 정의 예외로 변환
			throw e;
		}

		refundDTO = RefundDTO.builder()
			.userId(userId)
			.projectId(projectId)
			.shId(subscriptionHistDTO.getShId()) // 원천 청약 PK
			.uclId(31L)
			.amount(subscriptionHistDTO.getSubscriptionAmount()) // 환불 금액 = 청약 금액
			.refundType("ALL") // 환불 완료 상태
			.reasonCode("USER_CANCEL") // 사유
			.status("SUCCESS")
			.externalRefId(subscriptionHistDTO.getExternalRefId()) // 증권사 거래 번호 그대로 인계 (Long)
			.build();

		if (refundDTO == null) {
			throw new Exception("환불 처리에 실패했습니다.");
		}

		refundDTO.setUserId(userId);
		refundDTO.setProjectId(projectId);

		// 환불 내역 저장
		if (refundRepository.insertRefund(refundDTO) <= 0) {
			throw new Exception("내부 환불 내역 기록 실패 (DB 오류)");
		}
		subscriptionHistDTO.setPaymentStatus("REFUNDED");
		subscriptionHistDTO.setCanceledAt(OffsetDateTime.now());

		if (subscriptionRepository.update(subscriptionHistDTO) <= 0) {
			throw new Exception("청약 상태 변경 실패 (DB 오류)");
		}

		return true;
	}

}
