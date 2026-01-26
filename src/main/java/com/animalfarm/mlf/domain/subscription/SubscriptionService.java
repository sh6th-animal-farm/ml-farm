package com.animalfarm.mlf.domain.subscription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animalfarm.mlf.common.http.ApiResponse;
import com.animalfarm.mlf.common.http.ExternalApiUtil;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionHistDTO;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionInsertDTO;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionSelectDTO;
import com.animalfarm.mlf.domain.token.dto.TokenIssueDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {
	@Autowired
	SubscriptionRepository subscriptionRepository;

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
	
	public void postApplication(SubscriptionInsertDTO subscriptionInsertDTO) {
		Long tokenId = subscriptionInsertDTO.getTokenId();
		String targetUrl = khUrl + "api/project/application/" + tokenId;
		try {
			SubscriptionInsertDTO result = externalApiUtil.callApi(targetUrl, HttpMethod.POST, subscriptionInsertDTO,
					new ParameterizedTypeReference<ApiResponse<SubscriptionInsertDTO>>() {});
			log.info("증권사 정송 성공 : " + result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("실패!!! : " + e.getMessage());
		}
	}

}
