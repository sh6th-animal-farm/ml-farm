package com.animalfarm.mlf.domain.token;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.animalfarm.mlf.common.http.ApiResponse;
import com.animalfarm.mlf.common.http.ExternalApiUtil;
import com.animalfarm.mlf.domain.token.dto.TokenDTO;
import com.animalfarm.mlf.domain.token.dto.TokenDetailDTO;

@Service
public class TokenService {

	@Autowired
	TokenRepository tokenRepository;

	@Autowired
	ExternalApiUtil externalApiUtil;

	// @Value("${api.kh-stock.url}") // 강황증권 API 서버 주소 (배포)
	@Value("http://localhost:9090/") // 강황증권 API 서버 주소 (테스트)
	private String khUrl;

	public TokenDTO selectByProjectId(Long projectId) {
		return tokenRepository.selectByProjectId(projectId);
	}

	// 토큰 상세 내역 조회
	public TokenDetailDTO selectByTokenId(Long tokenId) {
		String targetUrl = khUrl + "api/my/market/" + tokenId; 	// [TODO] 강황증권 API 필요
		return null;
	}

	// 증권사 연동 번호 조회 (지갑 번호)
	public Long selectWalletId(Long userId) {
		return tokenRepository.selectWalletId(userId);
	}

	// 주문 가능 금액 조회
	public BigDecimal selectMyCash(Long userId) {
		Long walletId = selectWalletId(userId);
		if (walletId != null) {
			String targetUrl = khUrl + "api/order/balance/" + walletId;
			ParameterizedTypeReference<ApiResponse<BigDecimal>> responseType =
				new ParameterizedTypeReference<ApiResponse<BigDecimal>>() {};

			return externalApiUtil.callApi(targetUrl, HttpMethod.GET, null, responseType);
		}
		return null;
	}

}
