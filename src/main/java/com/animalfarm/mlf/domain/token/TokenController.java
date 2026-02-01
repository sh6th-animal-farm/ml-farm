package com.animalfarm.mlf.domain.token;

import java.math.BigDecimal;
import java.util.List;

import com.animalfarm.mlf.domain.token.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.common.security.SecurityUtil;

@RestController
public class TokenController {
	@Autowired
	TokenService tokenService;

	@GetMapping("/api/token/{projectId}")
	public TokenDTO selectDetail(@PathVariable("projectId") Long projectId) {
		return tokenService.selectByProjectId(projectId);
	}

	// 주문 가능 금액 조회
	@GetMapping("/api/account/balance")
	public BigDecimal selectCashBalance() {
		Long userId = SecurityUtil.getCurrentUserId();
		if (userId != null) {
			return tokenService.selectCashBalance(userId);
		}
		return null;
	}

	// 보유 토큰 수량 조회
	@GetMapping("/api/account/balance/{tokenId}")
	public BigDecimal selectTokenBalance(@PathVariable Long tokenId) {
		Long userId = SecurityUtil.getCurrentUserId();
		if (userId != null) {
			return tokenService.selectTokenBalance(tokenId, userId);
		}
		return null;
	}

	// 미체결 내역 조회
	@GetMapping("/api/token/pending/{tokenId}")
	public List<TokenPendingDTO> selectAllPending(@PathVariable Long tokenId) {
		Long userId = SecurityUtil.getCurrentUserId();
		if (userId != null) {
			return tokenService.selectAllPending(tokenId, userId);
		}
		return null;
	}

	// 주문 (매수, 매도)
	@PostMapping("/api/token/order/{tokenId}")
	public boolean createOrder(@PathVariable Long tokenId, @RequestBody OrderDTO orderDTO) {
		Long userId = SecurityUtil.getCurrentUserId();
		if (userId != null) {
			return tokenService.createOrder(userId, tokenId, orderDTO);
		}
		return false;
	}

	// 주문 취소
	@PostMapping("/api/token/order-cancel/{tokenId}/{orderId}")
	public boolean cancelOrder(@PathVariable Long tokenId, @PathVariable Long orderId) {
		return tokenService.cancelOrder(tokenId, orderId);
	}

	// 캔들 조회
	@GetMapping("/api/market/candles/{tokenId}")
	public List<CandleDTO> selectCandles(
			@PathVariable Long tokenId,
			@RequestParam(defaultValue = "1") int unit,
			@RequestParam(required = false) Long start,
			@RequestParam(required = false) Long end) {

		return tokenService.selectCandles(tokenId, unit,
				start != null ? start : 0L,
				end != null ? end : System.currentTimeMillis());
	}

	// OHLCV 조회
	@GetMapping("/api/token/ohlcv/{tokenId}")
	public TokenListDTO selectTokenOhlcv(@PathVariable Long tokenId) {
		return tokenService.selectTokenOhlcv(tokenId);
	}
}
