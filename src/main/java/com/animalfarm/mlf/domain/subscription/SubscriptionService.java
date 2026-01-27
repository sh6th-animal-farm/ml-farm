package com.animalfarm.mlf.domain.subscription;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.animalfarm.mlf.common.http.ApiResponse;
import com.animalfarm.mlf.common.http.ExternalApiUtil;
import com.animalfarm.mlf.common.security.SecurityUtil;
import com.animalfarm.mlf.domain.refund.RefundDTO;
import com.animalfarm.mlf.domain.refund.RefundRepository;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionHistDTO;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionInsertDTO;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionSelectDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {
	private final SubscriptionRepository subscriptionRepository;
	private final RefundRepository refundRepository;
	private final ExternalApiUtil externalApiUtil;
	private final RestTemplate restTemplate;

	@Autowired
	private SubscriptionService self;

	private static final String BASE_URL = "http://54.167.85.125:9090/";

	// 강황증권 API 서버 주소
	@Value("${api.kh-stock.url}")
	private String khUrl;

	public boolean selectAndCancel(Long projectId) throws Exception {
		Long userId = SecurityUtil.getCurrentUserId();
		SubscriptionHistDTO subscriptionHistDTO = subscriptionRepository.select(userId, projectId);
		if (subscriptionHistDTO == null) {
			throw new Exception("청약 내역이 존재하지 않습니다.");
		}

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

		//		refundDTO = RefundDTO.builder()
		//			.userId(userId)
		//			.projectId(projectId)
		//			.shId(subscriptionHistDTO.getShId()) // 원천 청약 PK
		//			.uclId(31L)
		//			.amount(subscriptionHistDTO.getSubscriptionAmount()) // 환불 금액 = 청약 금액
		//			.refundType("ALL") // 환불 완료 상태
		//			.reasonCode("USER_CANCEL") // 사유
		//			.status("SUCCESS")
		//			.externalRefId(subscriptionHistDTO.getExternalRefId()) // 증권사 거래 번호 그대로 인계 (Long)
		//			.build();

		if (refundDTO == null) {
			throw new Exception("환불 처리에 실패했습니다.");
		}

		refundDTO.setShId(subscriptionHistDTO.getShId());
		refundDTO.setProjectId(projectId);
		refundDTO.setUserId(userId);
		refundDTO.setRefundType("ALL"); // 환불 완료 상태
		refundDTO.setReasonCode("USER_CANCEL"); // 사유
		refundDTO.setStatus("SUCCESS"); // 처리 상태

		subscriptionHistDTO.setSubscriptionStatus("CANCELED");
		subscriptionHistDTO.setPaymentStatus("REFUNDED");
		subscriptionHistDTO.setCanceledAt(OffsetDateTime.now());

		self.updateRefundAndSubscriptionHist(refundDTO, subscriptionHistDTO);

		return true;
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateRefundAndSubscriptionHist(RefundDTO refundDTO, SubscriptionHistDTO subscriptionHistDTO)
		throws Exception {
		if (refundRepository.insertRefund(refundDTO) <= 0) {
			throw new Exception("내부 환불 내역 기록 실패 (DB 오류)");
		}
		if (subscriptionRepository.update(subscriptionHistDTO) <= 0) {
			throw new Exception("청약 상태 변경 실패 (DB 오류)");
		}
	}

	@Transactional
	public boolean selectAndCancel(SubscriptionSelectDTO subscriptionSelectDTO) {
		SubscriptionHistDTO subscriptionHistDTO = subscriptionRepository.select(subscriptionSelectDTO);
		System.out.println(subscriptionHistDTO);
		//		String url = BASE_URL + "/api/project/cancel/" + subscriptionHistDTO.getShId();
		//		try {
		//			externalApiUtil.callApi(url, HttpMethod.POST, subscriptionHistDTO, new ParameterizedTypeReference<ApiResponse<Void>>() {});
		//			log.info("[Service] 증권사 청약 취소 완료");
		//		} catch (RuntimeException e) {
		//			// 유틸리티에서 던진 구체적인 에러 메시지("잔액 부족" 등)가 이곳으로 전달됨
		//            log.error("[Service] 청약 취소 실패 사유: {}", e.getMessage());
		//
		//            // 트랜잭션 롤백을 위해 예외를 다시 던지거나, 사용자 정의 예외로 변환
		//            throw e;
		//		}
		return true;
	}

	public boolean subscriptionApplication(SubscriptionInsertDTO subscriptionInsertDTO) {
		//Long userId = SecurityUtil.getCurrentUserId();
		//subscriptionInsertDTO.setUserId(userId);
		return subscriptionRepository.subscriptionApplication(subscriptionInsertDTO);
	}

	@Transactional
	public void postApplication(SubscriptionInsertDTO subscriptionInsertDTO) {
		Long tokenId = subscriptionInsertDTO.getTokenId();
		Long shId = subscriptionInsertDTO.getShId();
		BigDecimal amount = subscriptionInsertDTO.getSubscriptionAmount();
		System.out.println(subscriptionInsertDTO);
		//Long userId = SecurityUtil.getCurrentUserId();
		//System.out.println(userId);
		//subscriptionInsertDTO.setUserId(userId);
		// 1. 증권사 명세서 규격에 맞게 맵 구성
		String targetUrl = khUrl + "api/project/application/" + tokenId
			+ "?subscriptionId=" + shId
			+ "&walletId=" + 2 // 또는 dto.getUclId()
			+ "&amount=" + amount;
		try {
			// 2. restTemplate으로 직접 호출 (껍데기 ApiResponse.class를 지정)
			ResponseEntity<ApiResponse> responseEntity = restTemplate.postForEntity(targetUrl, null, ApiResponse.class);

			int status = responseEntity.getStatusCodeValue();
			if (status == 200) {
				ApiResponse response = responseEntity.getBody();

				// 3. 드디어 message와 payload를 둘 다 꺼낼 수 있습니다!
				String msg = response.getMessage(); // "청약 신청이 완료되었습니다."
				Object payload = response.getPayload();

				Long uclId = subscriptionRepository.selectUclId(subscriptionInsertDTO);

				System.out.println("증권사 메시지: " + msg);
				System.out.println("payload: " + payload);
				if (payload != null) {
					subscriptionInsertDTO.setPaymentStatus("PAID");
					subscriptionInsertDTO.setExternalRefId((Long)payload);
					subscriptionRepository.subscriptionApplicationResponse(subscriptionInsertDTO);
					subscriptionRepository.updatePlusAmount(subscriptionInsertDTO);
				} else {
					subscriptionInsertDTO.setPaymentStatus("FAILED");
					subscriptionRepository.subscriptionApplicationResponse(subscriptionInsertDTO);
					throw new RuntimeException("empty_payload");
				}
			}
		} catch (Exception e) {
			System.out.println("통신 실패: " + e.getMessage());
			throw e;
		}
	}

}
