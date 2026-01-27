package com.animalfarm.mlf.domain.subscription;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
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
import com.animalfarm.mlf.domain.retry.ApiRetryQueueDTO;
import com.animalfarm.mlf.domain.retry.ApiRetryService;
import com.animalfarm.mlf.domain.retry.ApiType;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionHistDTO;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionInsertDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {
	private final ApplicationContext applicationContext;
	private final SubscriptionRepository subscriptionRepository;
	private final RefundRepository refundRepository;
	private final ExternalApiUtil externalApiUtil;
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	@Autowired
	private SubscriptionService self;

	// 강황증권 API 서버 주소
	@Value("${api.kh-stock.url}")
	private String KH_BASE_URL;

	public boolean selectAndCancel(Long projectId) throws Exception {
		Long userId = SecurityUtil.getCurrentUserId();

		// 청약 내역 조회
		SubscriptionHistDTO subscriptionHistDTO = subscriptionRepository.select(userId, projectId);
		if (subscriptionHistDTO == null) {
			throw new Exception("청약 내역이 존재하지 않습니다.");
		}

		// 멱등성 키 생성
		String idempotencyKey = "SUB-CANCEL-" + subscriptionHistDTO.getShId();

		// url 생성
		String url = KH_BASE_URL + "/api/project/cancel/" + subscriptionHistDTO.getExternalRefId();
		RefundDTO refundDTO = null;
		try {
			// 취소 및 환불 요청
			refundDTO = externalApiUtil.callApi(url, HttpMethod.POST, subscriptionHistDTO,
				new ParameterizedTypeReference<ApiResponse<RefundDTO>>() {}, idempotencyKey);

			if (refundDTO == null) {
				throw new Exception("환불 처리에 실패했습니다.");
			}

			log.info("[Service] 증권사 청약 취소 완료");

			afterSubsRefundRequest(subscriptionHistDTO, refundDTO);

		} catch (RuntimeException e) {
			// 유틸리티에서 던진 구체적인 에러 메시지("잔액 부족" 등)가 이곳으로 전달됨
			log.error("[Service] 청약 취소 실패. 재시도 큐에 등록합니다. 사유: {}", e.getMessage());

			Object[] params = new Object[] {subscriptionHistDTO.getExternalRefId()};

			ApiRetryService apiRetryService = applicationContext.getBean(ApiRetryService.class);
			apiRetryService.registerRetry(
				ApiType.SUB_CANCEL,
				subscriptionHistDTO,
				params,
				idempotencyKey);

			return false;
		}

		return true;
	}

	// 증권사 환불 요청 성공 이후 작업
	private void afterSubsRefundRequest(SubscriptionHistDTO subscriptionHistDTO,
		RefundDTO refundDTO) throws Exception {
		refundDTO.setShId(subscriptionHistDTO.getShId());
		refundDTO.setProjectId(subscriptionHistDTO.getProjectId());
		refundDTO.setUclId(refundDTO.getWalletId());
		refundDTO.setExternalRefId(refundDTO.getTransactionId());
		refundDTO.setUserId(subscriptionHistDTO.getUserId());
		refundDTO.setRefundType("ALL"); // 환불 완료 상태
		refundDTO.setReasonCode("USER_CANCEL"); // 사유
		refundDTO.setStatus("SUCCESS"); // 처리 상태

		subscriptionHistDTO.setSubscriptionStatus("CANCELED");
		subscriptionHistDTO.setPaymentStatus("REFUNDED");
		subscriptionHistDTO.setCanceledAt(OffsetDateTime.now());

		self.updateRefundAndSubsTable(refundDTO, subscriptionHistDTO);
	}

	// 증권사 환불 요청 "재시도" 성공 이후 작업
	@Transactional(rollbackFor = Exception.class)
	public void afterSubsRefundRetry(ApiRetryQueueDTO retry, RefundDTO refundDTO) throws Exception {
		// 저장된 Payload(JSON)를 다시 DTO로 변환
		SubscriptionHistDTO hist = objectMapper.readValue(retry.getPayload(), SubscriptionHistDTO.class);

		refundDTO.setShId(hist.getShId());
		refundDTO.setProjectId(hist.getProjectId());
		refundDTO.setUclId(refundDTO.getWalletId());
		refundDTO.setExternalRefId(refundDTO.getTransactionId());
		refundDTO.setUserId(hist.getUserId());
		refundDTO.setRefundType("ALL"); // 환불 완료 상태
		refundDTO.setReasonCode("USER_CANCEL"); // 사유
		refundDTO.setStatus("SUCCESS"); // 처리 상태

		// 기존에 만들어둔 업데이트 메서드 재사용
		self.updateRefundAndSubsTable(refundDTO, hist);
	}

	// 환불 내역, 청약 내역 DB 수정
	@Transactional(rollbackFor = Exception.class)
	public void updateRefundAndSubsTable(RefundDTO refundDTO, SubscriptionHistDTO subscriptionHistDTO)
		throws Exception {
		if (refundRepository.insertRefund(refundDTO) <= 0) {
			throw new Exception("내부 환불 내역 기록 실패 (DB 오류)");
		}
		if (subscriptionRepository.update(subscriptionHistDTO) <= 0) {
			throw new Exception("청약 상태 변경 실패 (DB 오류)");
		}
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
		String targetUrl = KH_BASE_URL + "api/project/application/" + tokenId
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
