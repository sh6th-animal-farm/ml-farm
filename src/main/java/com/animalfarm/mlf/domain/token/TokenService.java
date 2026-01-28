package com.animalfarm.mlf.domain.token;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import com.animalfarm.mlf.domain.token.dto.OrderPriceDTO;
import com.animalfarm.mlf.domain.token.dto.TokenListDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.animalfarm.mlf.common.http.ApiResponse;
import com.animalfarm.mlf.common.http.ExternalApiUtil;
import com.animalfarm.mlf.domain.token.dto.TokenDTO;
import com.animalfarm.mlf.domain.token.dto.TokenDetailDTO;
import com.animalfarm.mlf.domain.token.dto.TradePriceDTO;

@Slf4j
@Service
public class TokenService {

	@Autowired
	TokenRepository tokenRepository;

	@Autowired
	ExternalApiUtil externalApiUtil;

	// @Value("${api.kh-stock.url}") // 강황증권 API 서버 주소 (배포)
	@Value("http://localhost:9090/api") // 강황증권 API 서버 주소 (테스트)
	private String khUrl;

	// 전체 토큰 시세 조회
	public List<TokenListDTO> selectAll() {
		try {
			List<TokenListDTO> list = externalApiUtil.callApi(
					khUrl + "/market",
					HttpMethod.GET,
					null,
					new ParameterizedTypeReference<ApiResponse<List<TokenListDTO>>>() {}
			);

			list.forEach(dto -> {
				if (dto.getMarketPrice() == null) dto.setMarketPrice(BigDecimal.ZERO);
				if (dto.getDailyTradeVolume() == null) dto.setDailyTradeVolume(BigDecimal.ZERO);
				// 등락률 계산 예정
				 if (dto.getChangeRate() == null) dto.setChangeRate(BigDecimal.ZERO);
			});

			return list;
		} catch (RuntimeException e) {
			log.error("[Service Error] 토큰 목록 조회 실패: {}",e.getMessage());
			return Collections.emptyList();
		}
	}

	// 토큰 상세 내역 조회
	public TokenDetailDTO selectByTokenId(Long tokenId) {
		String targetUrl = khUrl + "/my/market/" + tokenId; 	// [TODO] 강황증권 API 필요
		return null;
	}

	// 토큰 현재가 조회
	public BigDecimal selectCurrentPrice(Long tokenId) {
		BigDecimal curPrice = externalApiUtil.callApi(
			khUrl + "/market/current/" + tokenId,
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<ApiResponse<BigDecimal>>() {}
		);

		return curPrice;
	}

	// 매수 호가 조회
	public List<OrderPriceDTO> selectAllOrderBuyPrice(Long tokenId) {
		List<OrderPriceDTO> orderBuyPriceList = externalApiUtil.callApi(
			khUrl + "/market/order/buy/" + tokenId,
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<ApiResponse<List<OrderPriceDTO>>>() {}
		);

		return orderBuyPriceList;
	}

	// 매도 호가 조회
	public List<OrderPriceDTO> selectAllOrderSellPrice(Long tokenId) {
		List<OrderPriceDTO> orderSellPriceList = externalApiUtil.callApi(
			khUrl + "/market/order/sell/" + tokenId,
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<ApiResponse<List<OrderPriceDTO>>>() {}
		);

		return orderSellPriceList;
	}

	// 체결가 조회
	public List<TradePriceDTO> selectAllTradePrice(Long tokenId) {
		List<TradePriceDTO> tradePriceList = externalApiUtil.callApi(
			khUrl + "/market/trade/" + tokenId,
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<ApiResponse<List<TradePriceDTO>>>() {}
		);

		return tradePriceList;
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

	public TokenDTO selectByProjectId(Long projectId) {
		return tokenRepository.selectByProjectId(projectId);
	}

	// 보유 토큰 수량 조회

}
