package com.animalfarm.mlf.domain.carbon;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.animalfarm.mlf.common.security.CustomUser;
import com.animalfarm.mlf.domain.carbon.dto.CarbonDetailDTO;
import com.animalfarm.mlf.domain.carbon.dto.CarbonListDTO;
import com.animalfarm.mlf.domain.carbon.dto.GanghwangBalanceDTO;
import com.animalfarm.mlf.domain.carbon.dto.UserBenefitDTO;

@Service
public class CarbonService {

	@Autowired
	private CarbonRepository carbonRepository;

	@Autowired
	private RestTemplate restTemplate;

	// 강황증권 API 서버 주소 (예시)
	private final String GANGHWANG_API_URL = "http://192.168.0.156:9090/";

	// ---------------------------------------------------------
	// 1. 공통 유틸리티 메서드 (내부 전용)
	// ---------------------------------------------------------

	/**
	 * 시큐리티 세션에서 현재 로그인한 유저의 ID를 가져옵니다.
	 */
	private Long getLoginUserId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof CustomUser)) {
			throw new RuntimeException("로그인 정보가 만료되었습니다. 다시 로그인해주세요.");
		}
		CustomUser user = (CustomUser)auth.getPrincipal();
		return user.getUserId();
	}

	/**
	 * [API 호출] 강황증권으로부터 유저의 전체 지분 리스트를 가져옵니다.
	 * 엔드포인트: /api/carbon/{walletId}
	 */
	public List<GanghwangBalanceDTO> fetchAllHoldings(Long walletId) {
		try {
			String url = GANGHWANG_API_URL + "/api/carbon/" + walletId;
			GanghwangBalanceDTO[] response = restTemplate.getForObject(url, GanghwangBalanceDTO[].class);
			return (response != null) ? Arrays.asList(response) : new ArrayList<>();
		} catch (Exception e) {
			System.out.println("[ERROR] 강황증권 API 통신 실패: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	/**
	 * [핵심 계산기] 지분율, 구매한도, 할인율을 계산하는 공통 엔진
	 * 수식:
	 * 1. 지분율: $shareRatio = \frac{myBalance}{totalCorpBalance}$
	 * 2. 구매한도: $maxLimit = cpAmount \times shareRatio$
	 * 3. 최종단가: $currentPrice = cpPrice \times (1 - \frac{discountRate}{100})$
	 */
	/**
	 * [최종 수정된 계산기]
	 * 1. maxLimit: 기존 로직 유지 (증권사 토큰 총량 대비 지분율)
	 * 2. discountRate: 신규 로직 적용 (프로젝트 총 투자액 대비 내 투자액)
	 */
	private UserBenefitDTO processCalculation(BigDecimal cpAmount, BigDecimal cpPrice, BigDecimal actualAmount,
		GanghwangBalanceDTO balance) {
		// 기초 데이터 (API에서 온 값)
		BigDecimal myBal = (balance != null) ? balance.getMyBalance() : BigDecimal.ZERO;
		BigDecimal totalCorpTokens = (balance != null) ? balance.getEnterpriseTotal() : BigDecimal.ONE;

		// 0 나누기 방어 로직
		if (totalCorpTokens.compareTo(BigDecimal.ZERO) == 0) {
			totalCorpTokens = BigDecimal.ONE;
		}
		if (actualAmount == null || actualAmount.compareTo(BigDecimal.ZERO) == 0) {
			actualAmount = BigDecimal.ONE;
		}

		// ---------------------------------------------------------
		// 1. 최대 구매 가능 수량 (maxLimit) - 기존 로직 100% 유지
		// 수식: cpAmount * (내 토큰 잔고 / 증권사 총 토큰량)
		// ---------------------------------------------------------
		BigDecimal limitShareRatio = myBal.divide(totalCorpTokens, 10, RoundingMode.HALF_UP);
		BigDecimal maxLimit = cpAmount.multiply(limitShareRatio).setScale(4, RoundingMode.HALF_UP);

		// ---------------------------------------------------------
		// 2. 할인율 계산 (discountRate) - 새로운 기준 적용
		// 수식: (내 토큰 잔고 / 프로젝트 총 투자금액 actual_amount) * 100
		// ---------------------------------------------------------
		BigDecimal discountShareRatio = myBal.divide(actualAmount, 10, RoundingMode.HALF_UP);
		BigDecimal discountSharePercent = discountShareRatio.multiply(new BigDecimal("100"));

		// DB에서 해당 퍼센트 구간의 할인율 조회
		BigDecimal discountRate = carbonRepository.getDiscountRate(discountSharePercent);
		if (discountRate == null) {
			discountRate = BigDecimal.ZERO;
		}

		// ---------------------------------------------------------
		// 3. 최종 가격 계산
		// ---------------------------------------------------------
		BigDecimal curPrice = cpPrice.multiply(
			BigDecimal.ONE.subtract(discountRate.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)));

		return UserBenefitDTO.builder()
			.userMaxLimit(maxLimit)
			.discountRate(discountRate)
			.currentPrice(curPrice.setScale(0, RoundingMode.FLOOR)) // 가격 소수점 절사
			.myTokenBalance(myBal)
			.build();
	}

	// ---------------------------------------------------------
	// 2. 외부 노출용 서비스 메서드 (컨트롤러에서 호출)
	// ---------------------------------------------------------

	// [전체 조회]
	public List<CarbonListDTO> selectAll() {
		return carbonRepository.selectAll();
	}

	// [카테고리 조회]
	public List<CarbonListDTO> selectByCondition(String category) {
		return carbonRepository.selectByCondition(category);
	}

	/**
	 * [상세 조회] 특정 상품 정보와 유저의 실시간 혜택 계산
	 */
	public CarbonDetailDTO selectDetail(Long cpId) {
		Long userId = getLoginUserId();
		Long walletId = carbonRepository.getWalletIdByUserId(userId);

		// 1. 마리팜 상품 정보 조회 (여기엔 projectId가 들어있음)
		CarbonDetailDTO detail = carbonRepository.selectDetail(cpId);
		Long currentProjectId = detail.getCarbonInfo().getProjectId();
		Long projectId = detail.getCarbonInfo().getProjectId();
		BigDecimal actualAmount = carbonRepository.getActualAmount(projectId);

		// 2. [핵심] ID 번역: 프로젝트 ID -> 실제 증권사 토큰 ID
		Long targetTokenId = carbonRepository.getTokenIdByProjectId(currentProjectId);

		// 3. 강황증권 전체 지분 리스트 가져오기
		List<GanghwangBalanceDTO> holdings = fetchAllHoldings(walletId);

		// 4. [수정 완료] 번역된 'targetTokenId'로 API 결과 내 지분 필터링
		GanghwangBalanceDTO myHolding = holdings.stream()
			.filter(h -> h.getTokenId().equals(targetTokenId)) // 이제 정확히 일치함!
			.findFirst()
			.orElse(null);

		// 5. 공통 계산기로 최종 혜택 주입
		detail.setUserBenefit(processCalculation(
			detail.getCarbonInfo().getCpAmount(),
			detail.getCarbonInfo().getCpPrice(),
			actualAmount, myHolding));

		return detail;
	}
}
