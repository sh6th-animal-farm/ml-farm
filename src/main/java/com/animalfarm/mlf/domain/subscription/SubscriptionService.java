package com.animalfarm.mlf.domain.subscription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.animalfarm.mlf.common.http.ApiResponse;
import com.animalfarm.mlf.common.http.ExternalApiUtil;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionHistDTO;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionInsertDTO;
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

	public boolean subscriptionApplication(SubscriptionInsertDTO subscriptionInsertDTO) {
		return subscriptionRepository.subscriptionApplication(subscriptionInsertDTO);
	}

	@Transactional
	public void postApplication(SubscriptionInsertDTO subscriptionInsertDTO) {
		Long tokenId = subscriptionInsertDTO.getTokenId();
		// 1. 증권사 명세서 규격에 맞게 맵 구성
		String targetUrl = khUrl + "api/project/application/" + tokenId
			+ "?subscriptionId=" + 3 // 또는 dto.getShId()
			+ "&walletId=" + 2 // 또는 dto.getUclId()
			+ "&amount=" + subscriptionInsertDTO.getSubscriptionAmount();
		try {
			// 2. restTemplate으로 직접 호출 (껍데기 ApiResponse.class를 지정)
			ResponseEntity<ApiResponse> responseEntity = restTemplate.postForEntity(targetUrl, null, ApiResponse.class);

			int status = responseEntity.getStatusCodeValue();
			if (status == 200) {
				ApiResponse response = responseEntity.getBody();

				// 3. 드디어 message와 payload를 둘 다 꺼낼 수 있습니다!
				String msg = response.getMessage(); // "청약 신청이 완료되었습니다."
				Object payload = response.getPayload();

				System.out.println("증권사 메시지: " + msg);
				System.out.println("트랜잭션 ID: " + payload);
				if (payload != null) {
					subscriptionInsertDTO.setPaymentStatus("PAID");
					subscriptionInsertDTO.setExternalRefId((Long)payload);
					subscriptionRepository.subscriptionApplicationResponse(subscriptionInsertDTO);
					subscriptionRepository.updatePlusAmount(subscriptionInsertDTO);
				} else {
					subscriptionInsertDTO.setPaymentStatus("FAILED");
					subscriptionRepository.subscriptionApplicationResponse(subscriptionInsertDTO);
				}
			}
		} catch (Exception e) {
			System.out.println("통신 실패: " + e.getMessage());
		}
	}

}
