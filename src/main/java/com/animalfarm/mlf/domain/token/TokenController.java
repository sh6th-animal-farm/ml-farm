package com.animalfarm.mlf.domain.token;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.common.security.SecurityUtil;
import com.animalfarm.mlf.domain.token.dto.TokenDTO;
import com.animalfarm.mlf.domain.token.dto.TokenPendingDTO;

@RestController
public class TokenController {
	@Autowired
	TokenService tokenService;

	@GetMapping("/api/token/{projectId}")
	public TokenDTO selectDetail(@PathVariable("projectId")
	Long projectId) {
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
	@GetMapping("/api/token/{tokenId}/pending")
	public List<TokenPendingDTO> selectAllPending(@PathVariable Long tokenId) {
		Long userId = SecurityUtil.getCurrentUserId();
		if (userId != null) {
			return tokenService.selectAllPending(tokenId, userId);
		}
		return null;
	}
}