package com.animalfarm.mlf.domain.subscription;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.animalfarm.mlf.common.MailService;
import com.animalfarm.mlf.common.http.ApiResponse;
import com.animalfarm.mlf.common.http.ExternalApiUtil;
import com.animalfarm.mlf.common.security.SecurityUtil;
import com.animalfarm.mlf.domain.refund.RefundDTO;
import com.animalfarm.mlf.domain.refund.RefundRepository;
import com.animalfarm.mlf.domain.retry.ApiRetryQueueDTO;
import com.animalfarm.mlf.domain.retry.ApiRetryService;
import com.animalfarm.mlf.domain.retry.ApiType;
import com.animalfarm.mlf.domain.subscription.dto.ProjectStartCheckDTO;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionApplicationDTO;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionHistDTO;
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

	@Autowired
	private MailService mailService;

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
		String url = KH_BASE_URL + "api/project/cancel/" + subscriptionHistDTO.getExternalRefId();
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
	public void afterSubsRefundRetry(ApiRetryQueueDTO retry, RefundDTO refundDTO) throws Exception {
		// 저장된 Payload(JSON)를 다시 DTO로 변환
		SubscriptionHistDTO hist = objectMapper.readValue(retry.getPayload(), SubscriptionHistDTO.class);

		afterSubsRefundRequest(hist, refundDTO);
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

	public boolean subscriptionApplication(SubscriptionApplicationDTO subscriptionInsertDTO) {
		Long userId = SecurityUtil.getCurrentUserId();
		subscriptionInsertDTO.setUserId(userId);
		return subscriptionRepository.subscriptionApplication(subscriptionInsertDTO);
	}

	// 1. 외부 API 호출 (트랜잭션 없음)
	public void postApplication(SubscriptionApplicationDTO dto) {
		Long uclId = subscriptionRepository.selectUclId(dto.getUserId());
		dto.setUclId(uclId);
		String targetUrl = KH_BASE_URL + "api/project/application/" + dto.getTokenId()
			+ "?subscriptionId=" + dto.getShId()
			+ "&walletId=" + dto.getUclId()
			+ "&amount=" + dto.getSubscriptionAmount();

		try {
			// [API 호출 전] DB 커넥션 안 잡음
			ResponseEntity<ApiResponse> responseEntity = restTemplate.postForEntity(targetUrl, null, ApiResponse.class);

			if (responseEntity.getStatusCodeValue() == 200) {
				Object payload = responseEntity.getBody().getPayload();

				if (payload != null) {
					dto.setPaymentStatus("PAID");
					dto.setExternalRefId(Long.valueOf(String.valueOf(payload)));
					// [성공 시 DB 처리 호출] 이 시점에만 트랜잭션 시작
					updateDatabaseInfo(dto);
				} else {
					dto.setPaymentStatus("FAILED");
					// [실패 시 DB 처리 호출]
					updateDatabaseInfo(dto);
					throw new RuntimeException("empty_payload");
				}
			}
		} catch (Exception e) {
			log.error("통신 실패: {}", e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	// 2. 내 DB 동작 (이 메서드만 트랜잭션 처리)
	@Transactional
	public void updateDatabaseInfo(SubscriptionApplicationDTO dto) {
		// 여기서부터 DB 커넥션을 잡고 처리함
		subscriptionRepository.updateSubscriptionStatus(dto);

		if ("PAID".equals(dto.getPaymentStatus())) {
			subscriptionRepository.updatePlusAmount(dto);
		}
	}

	public void projectStartCheck() {
		BigDecimal rate70 = new BigDecimal("70");
		BigDecimal rate90 = new BigDecimal("90");
		BigDecimal rate100 = new BigDecimal("100");

		List<ProjectStartCheckDTO> projectstartCheckList = subscriptionRepository.selectExpiredSubscriptions();
		for (ProjectStartCheckDTO data : projectstartCheckList) {
			try {
				Long projectId = data.getProjectId();
				BigDecimal rate = data.getSubscriptionRate();
				Long tokenId = data.getTokenId();
				Long subscriberCount = 50L;//subscriptionRepository.subscriberCount(data);
				int extensionCount = data.getExtensionCount();
				if (rate.compareTo(rate70) < 0 || subscriberCount < 49) {
					System.out.println(rate + " 프로젝트 폐기");
					projectCanceled(data);
					selectAndAllCancel(projectId);
					//tokenClosed(tokenId);
				} else if (rate.compareTo(rate70) >= 0 && rate.compareTo(rate90) < 0) {
					if (extensionCount == 0) {
						System.out.println(rate + " 프로젝트 종료일 +2일");
						subscriptionRepository.updateProjectTwoDay(projectId);
						noticeEmail(projectId); // 여기에 사용자에게 이메일 보내는 것 추가하기
					} else {
						System.out.println("프로젝트 폐기");
						projectCanceled(data);
						selectAndAllCancel(projectId);
						//tokenClosed(tokenId);
					}
				} else if (rate.compareTo(rate90) >= 0 && rate.compareTo(rate100) < 0) {
					//마리팜이 충당할 가격
					BigDecimal leftAmount = data.getTargetAmount().subtract(data.getActualAmount());
					SubscriptionApplicationDTO applicationDTO = new SubscriptionApplicationDTO();
					applicationDTO.setProjectId(data.getProjectId());
					applicationDTO.setSubscriptionAmount(leftAmount);
					applicationDTO.setTokenId(tokenId);
					subscriptionApplication(applicationDTO);
					System.out.println(applicationDTO);
					postApplication(applicationDTO);
					System.out.println(rate + " 마리팜 회사가 나머지 충당");
					subscriptionRepository.updateProjectInProgress(projectId);
				} else {
					subscriptionRepository.updateProjectInProgress(projectId);
					System.out.println(rate + " 그대로 진행");
				}
			} catch (Exception e) {
				log.error("[프로젝트 시작 체크 에러] 프로젝트 ID: {} 처리 중 오류 발생: {}", data.getProjectId(), e.getMessage());
			}
		}
	}

	public void projectCanceled(ProjectStartCheckDTO projectStartCheckDTO) {
		Long projectId = projectStartCheckDTO.getProjectId();
		Long tokenId = projectStartCheckDTO.getTokenId();

		// DB 업데이트 (프로젝트 상태 변경 및 토큰 삭제)
		subscriptionRepository.updateProjectCanceled(projectId);
		subscriptionRepository.updateTokenDelete(tokenId);
		System.out.println("ID: " + projectId + " 번 프로젝트 및 토큰(" + tokenId + ") 폐기 완료");
	}

	public void noticeEmail(Long projectId) {
		List<String> userEmails = subscriptionRepository.selectUserEmail(projectId);
		for (String userEmail : userEmails) {
			mailService.sendNoticeEmail(userEmail);
		}
	}

	public void selectAndAllCancel(Long projectId) throws Exception {
		List<Long> userIds = subscriptionRepository.selectSubscriberUserIds(projectId);
		for (Long userId : userIds) {
			// 청약 내역 조회
			SubscriptionHistDTO subscriptionHistDTO = subscriptionRepository.selectPaid(userId, projectId);
			if (subscriptionHistDTO == null) {
				throw new Exception("청약 내역이 존재하지 않습니다.");
			}

			// 멱등성 키 생성
			String idempotencyKey = "SUB-REJECTED-" + subscriptionHistDTO.getShId();

			// url 생성
			String url = KH_BASE_URL + "api/project/cancel/" + subscriptionHistDTO.getExternalRefId();
			RefundDTO refundDTO = null;
			try {
				// 취소 및 환불 요청
				refundDTO = externalApiUtil.callApi(url, HttpMethod.POST, subscriptionHistDTO,
					new ParameterizedTypeReference<ApiResponse<RefundDTO>>() {}, idempotencyKey);

				if (refundDTO == null) {
					throw new Exception("환불 처리에 실패했습니다.");
				}

				log.info("[Service] 증권사 청약 취소 완료");

				projectFailRefundRequest(subscriptionHistDTO, refundDTO);

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
			}
		}
	}

	//일단 사용하지 말고 두기
	private void tokenClosed(Long tokenId) {
		String url = KH_BASE_URL + "api/project/close/" + tokenId;
		try {
			externalApiUtil.callApi(url, HttpMethod.POST, null,
				new ParameterizedTypeReference<ApiResponse<Object>>() {});

			log.info("강황 증권에 토큰 소각 완료");
		} catch (Exception e) {
			log.error("정산 실패 메시지: {}", e.getMessage());
		}
	}

	// 증권사 환불 요청 성공 이후 작업
	private void projectFailRefundRequest(SubscriptionHistDTO subscriptionHistDTO,
		RefundDTO refundDTO) throws Exception {
		refundDTO.setShId(subscriptionHistDTO.getShId());
		refundDTO.setProjectId(subscriptionHistDTO.getProjectId());
		refundDTO.setUclId(refundDTO.getWalletId());
		refundDTO.setExternalRefId(refundDTO.getTransactionId());
		refundDTO.setUserId(subscriptionHistDTO.getUserId());
		refundDTO.setRefundType("ALL"); // 환불 완료 상태
		refundDTO.setReasonCode("FAIL_UNDER_70"); // 사유
		refundDTO.setStatus("SUCCESS"); // 처리 상태

		subscriptionHistDTO.setSubscriptionStatus("CANCELED");
		subscriptionHistDTO.setPaymentStatus("REFUNDED");
		subscriptionHistDTO.setCanceledAt(OffsetDateTime.now());

		self.updateRefundAndSubsTable(refundDTO, subscriptionHistDTO);
	}
}
