package com.animalfarm.mlf.domain.subscription;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.animalfarm.mlf.common.http.ApiResponse;
import com.animalfarm.mlf.common.http.ExternalApiUtil;
import com.animalfarm.mlf.domain.subscription.dto.ProjectStartCheckDTO;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionApplicationDTO;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionHistDTO;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionSelectDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {
	@Autowired
	SubscriptionRepository subscriptionRepository;

	@Autowired
	private RestTemplate restTemplate;

	private final ExternalApiUtil externalApiUtil;

	private static final String BASE_URL = "http://54.167.85.125:9090/";

	// 강황증권 API 서버 주소
	@Value("${api.kh-stock.url}")
	private String khUrl;

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

	public boolean subscriptionApplication(SubscriptionApplicationDTO subscriptionInsertDTO) {
		//Long userId = SecurityUtil.getCurrentUserId();
		//subscriptionInsertDTO.setUserId(userId);
		return subscriptionRepository.subscriptionApplication(subscriptionInsertDTO);
	}

	// 1. 외부 API 호출 (트랜잭션 없음)
	public void postApplication(SubscriptionApplicationDTO dto) {
		String targetUrl = khUrl + "api/project/application/" + dto.getTokenId()
			+ "?subscriptionId=" + dto.getShId()
			+ "&walletId=" + 2
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
		BigDecimal threshold70 = new BigDecimal("70");
		BigDecimal threshold90 = new BigDecimal("90");
		BigDecimal threshold100 = new BigDecimal("100");

		List<ProjectStartCheckDTO> projectstartCheckList = subscriptionRepository.selectExpiredSubscriptions();
		for (ProjectStartCheckDTO data : projectstartCheckList) {
			Long projectId = data.getProjectId();
			BigDecimal rate = data.getSubscriptionRate();
			Long tokenId = data.getTokenId();
			Long subscriberCount = 50L;//subscriptionRepository.subscriberCount(data);
			if (rate.compareTo(threshold70) < 0 || subscriberCount < 49) {
				System.out.println(rate + " 프로젝트 폐기");
				projectCanceled(data);
			} else if (rate.compareTo(threshold70) >= 0 && rate.compareTo(threshold90) < 0) {
				System.out.println(rate + " 프로젝트 종료일 +2일");
				if (false) {
					subscriptionRepository.updateProjectTwoDay(projectId);
				}
			} else if (rate.compareTo(threshold90) >= 0 && rate.compareTo(threshold100) < 0) {
				System.out.println(rate + " 마리팜 회사가 나머지 충당");
			} else {
				System.out.println(rate + " 그대로 진행");
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
}
