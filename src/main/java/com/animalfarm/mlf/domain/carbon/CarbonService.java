package com.animalfarm.mlf.domain.carbon;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.animalfarm.mlf.common.ApiResponseDTO;
import com.animalfarm.mlf.common.security.SecurityUtil;
import com.animalfarm.mlf.domain.carbon.dto.CarbonDetailDTO;
import com.animalfarm.mlf.domain.carbon.dto.CarbonDiscountDTO;
import com.animalfarm.mlf.domain.carbon.dto.CarbonListDTO;
import com.animalfarm.mlf.domain.carbon.dto.CarbonOrderCompleteDTO;
import com.animalfarm.mlf.domain.carbon.dto.CarbonOrderResponseDTO;
import com.animalfarm.mlf.domain.carbon.dto.UserBenefitDTO;

@Service
public class CarbonService {

	@Autowired
	private CarbonRepository carbonRepository;

	@Autowired
	private RestTemplate restTemplate;

	// 강황증권 API 서버 주소
	private final String GANGHWANG_API_URL = "http://54.167.85.125:9090/";

	// ---------------------------------------------------------
	// 1. 공통 유틸리티 메서드 (내부 전용)
	// ---------------------------------------------------------

	/**
	 * [API 호출] 강황증권으로부터 유저의 전체 지분 리스트를 가져옵니다.
	 * 엔드포인트: /api/carbon/{walletId}
	 */
	public List<CarbonDiscountDTO> fetchAllHoldings(Long walletId) {
		try {
			String url = GANGHWANG_API_URL + "/api/carbon/" + walletId;

			// ParameterizedTypeReference를 써야 제네릭(<T>)이 포함된 응답을 정확히 읽어옵니다.
			ResponseEntity<ApiResponseDTO<List<CarbonDiscountDTO>>> responseEntity = restTemplate.exchange(
				url,
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<ApiResponseDTO<List<CarbonDiscountDTO>>>() {});

			ApiResponseDTO<List<CarbonDiscountDTO>> response = responseEntity.getBody();

			// 상자(ApiResponseDTO)를 열어 실제 내용물(payload)인 리스트를 꺼냅니다.
			if (response != null && response.getPayload() != null) {
				return response.getPayload();
			}
			return new ArrayList<>();
		} catch (Exception e) {
			System.out.println("[ERROR] 강황증권 API 통신 실패: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	/**
	 * [API 호출] 강황증권으로부터 유저의 주문 가능 금액(available_balance)을 가져옵니다.
	 * 엔드포인트: GET /api/order/balance?user_id=...
	 */
	public BigDecimal fetchAvailableBalance(Long walletId) {
		try {
			String url = GANGHWANG_API_URL + "api/order/balance/" + walletId;

			ResponseEntity<String> responseEntity = restTemplate.exchange(
				url,
				HttpMethod.GET,
				null,
				String.class);

			String body = responseEntity.getBody();
			if (body == null || body.trim().isEmpty()) {
				throw new RuntimeException("강황증권 주문 가능 금액 응답이 비어있습니다.");
			}

			return new BigDecimal(body.trim());

		} catch (Exception e) {
			throw new RuntimeException(
				"강황증권 주문 가능 금액 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
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
		CarbonDiscountDTO balance) {
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

	// 보유 잔고가 있는 토큰 ID만 추출
	private List<Long> extractMyTokenIds(List<CarbonDiscountDTO> holdings) {
		return holdings.stream()
			.filter(h -> h.getMyBalance() != null && h.getMyBalance().compareTo(BigDecimal.ZERO) > 0)
			.map(CarbonDiscountDTO::getTokenId)
			.collect(Collectors.toList());
	}

	// 리스트의 각 상품에 UserBenefitDTO(할인율, 최종가 등) 주입
	private List<CarbonListDTO> applyBenefitsToList(List<CarbonListDTO> list, List<CarbonDiscountDTO> holdings) {
		for (CarbonListDTO item : list) {
			BigDecimal actualAmount = carbonRepository.getActualAmount(item.getProjectId()); // [cite: 2]
			Long targetTokenId = carbonRepository.getTokenIdByProjectId(item.getProjectId()); // [cite: 2]

			CarbonDiscountDTO myHolding = holdings.stream()
				.filter(h -> h.getTokenId().equals(targetTokenId))
				.findFirst().orElse(null);

			// 상세페이지에서 완성한 계산기(processCalculation) 그대로 사용
			item.setUserBenefit(processCalculation(
				item.getCpAmount(),
				item.getCpPrice(),
				actualAmount,
				myHolding));
		}
		return list;
	}

	// ---------------------------------------------------------
	// 2. 외부 노출용 서비스 메서드 (컨트롤러에서 호출)
	// ---------------------------------------------------------

	// [전체 조회] 로그인 유저의 토큰과 관련된 모든 상품 조회 및 혜택 계산
	public List<CarbonListDTO> selectAll() {
		Long userId = SecurityUtil.getCurrentUserId();
		Long walletId = carbonRepository.getWalletIdByUserId(userId);
		List<CarbonDiscountDTO> holdings = fetchAllHoldings(walletId); // 강황증권 API 호출

		List<Long> myTokenIds = extractMyTokenIds(holdings);
		if (myTokenIds.isEmpty()) {
			return new ArrayList<>();
		}

		List<CarbonListDTO> list = carbonRepository.selectAll(myTokenIds);
		return applyBenefitsToList(list, holdings);
	}

	// [카테고리 조회] 카테고리 조건 + 유저 토큰 필터링 및 혜택 계산
	public List<CarbonListDTO> selectByCondition(String category) {
		if (category == null || "ALL".equalsIgnoreCase(category)) {
			return selectAll();
		}

		Long userId = SecurityUtil.getCurrentUserId();
		Long walletId = carbonRepository.getWalletIdByUserId(userId);
		List<CarbonDiscountDTO> holdings = fetchAllHoldings(walletId);

		List<Long> myTokenIds = extractMyTokenIds(holdings);
		if (myTokenIds.isEmpty()) {
			return new ArrayList<>();
		}

		// 카테고리와 토큰 ID 리스트를 모두 만족하는 상품 조회
		List<CarbonListDTO> list = carbonRepository.selectByCondition(category, myTokenIds);
		return applyBenefitsToList(list, holdings);
	}

	/**
	 * [상세 조회] 특정 상품 정보와 유저의 실시간 혜택 계산
	 */
	public ApiResponseDTO<CarbonDetailDTO> selectDetail(Long cpId) {
		Long userId = SecurityUtil.getCurrentUserId();
		Long walletId = carbonRepository.getWalletIdByUserId(userId);

		// 1. 마리팜 상품 정보 조회 (여기엔 projectId가 들어있음)
		CarbonDetailDTO detail = carbonRepository.selectDetail(cpId);

		// [테스트 방어 코드 1] DB에 상품이 없으면 바로 에러를 내지 말고 리턴하거나 예외 처리
		if (detail == null || detail.getCarbonInfo() == null) {
			throw new RuntimeException("해당 상품 정보를 찾을 수 없습니다. (ID: " + cpId + ")");
		}

		Long currentProjectId = detail.getCarbonInfo().getProjectId();
		Long projectId = detail.getCarbonInfo().getProjectId();
		BigDecimal actualAmount = carbonRepository.getActualAmount(projectId);

		// 2. [핵심] ID 번역: 프로젝트 ID -> 실제 증권사 토큰 ID
		Long targetTokenId = carbonRepository.getTokenIdByProjectId(currentProjectId);

		// 3. 강황증권 전체 지분 리스트 가져오기
		List<CarbonDiscountDTO> holdings = fetchAllHoldings(walletId);

		// 4.  번역된 'targetTokenId'로 API 결과 내 지분 필터링
		CarbonDiscountDTO myHolding = holdings.stream()
			.filter(h -> h.getTokenId().equals(targetTokenId)) // 이제 정확히 일치함!
			.findFirst()
			.orElse(null);

		// 5. 공통 계산기로 최종 혜택 주입
		detail.setUserBenefit(processCalculation(
			detail.getCarbonInfo().getCpAmount(),
			detail.getCarbonInfo().getCpPrice(),
			actualAmount, myHolding));

		return new ApiResponseDTO<CarbonDetailDTO>("상품 상세 정보 조회에 성공했습니다.", detail);
	}

	/**
	 * [모달용] 주문 견적
	 */
	public ApiResponseDTO<CarbonOrderResponseDTO> quoteOrder(Long cpId, BigDecimal amount) {

		if (cpId == null) {
			throw new IllegalArgumentException("cpId가 필요합니다.");
		}
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("amount는 0보다 커야 합니다.");
		}

		Long userId = getLoginUserId();
		Long walletId = carbonRepository.getWalletIdByUserId(userId);

		// 1) 강황증권 주문 가능 금액
		BigDecimal availableBalance = fetchAvailableBalance(walletId);

		// 2) 상품/잔여 수량
		CarbonDetailDTO detail = carbonRepository.selectDetail(cpId);
		if (detail == null || detail.getCarbonInfo() == null) {
			throw new RuntimeException("해당 상품 정보를 찾을 수 없습니다. (ID: " + cpId + ")");
		}

		BigDecimal remainAmount = carbonRepository.selectCpAmount(cpId);
		if (remainAmount == null) {
			remainAmount = BigDecimal.ZERO;
		}

		// 3) 혜택 계산(기존 로직 재활용)
		Long projectId = detail.getCarbonInfo().getProjectId();
		BigDecimal actualAmount = carbonRepository.getActualAmount(projectId);
		Long tokenId = carbonRepository.getTokenIdByProjectId(projectId);

		List<CarbonDiscountDTO> holdings = fetchAllHoldings(walletId);
		CarbonDiscountDTO myHolding = holdings.stream()
			.filter(h -> h.getTokenId() != null && h.getTokenId().equals(tokenId))
			.findFirst()
			.orElse(null);

		UserBenefitDTO benefit = processCalculation(
			detail.getCarbonInfo().getCpAmount(),
			detail.getCarbonInfo().getCpPrice(),
			actualAmount,
			myHolding);

		BigDecimal unitPrice = (benefit != null && benefit.getCurrentPrice() != null)
			? benefit.getCurrentPrice()
			: detail.getCarbonInfo().getCpPrice();

		BigDecimal total = unitPrice.multiply(amount).setScale(0, RoundingMode.HALF_UP);
		BigDecimal supply = total.divide(new BigDecimal("1.1"), 0, RoundingMode.FLOOR);
		BigDecimal vat = total.subtract(supply);

		String cpTitle = carbonRepository.selectCpTitle(cpId);

		CarbonOrderResponseDTO resp = CarbonOrderResponseDTO.builder()
			.cpId(cpId)
			.cpTitle(cpTitle)
			.orderAmount(amount)
			.unitPrice(unitPrice)
			.supplyAmount(supply)
			.vatAmount(vat)
			.totalAmount(total)
			.userMaxLimit(benefit != null ? benefit.getUserMaxLimit() : null)
			.remainAmount(remainAmount)
			.availableBalance(availableBalance)
			.build();

		return new ApiResponseDTO<>("주문 견적 조회에 성공했습니다.", resp);

	}

	@Transactional
	public void completeOrder(CarbonOrderCompleteDTO req) {
		if (req == null) {
			throw new IllegalArgumentException("요청값이 비었습니다.");
		}
		if (req.getCpId() == null) {
			throw new IllegalArgumentException("cpId가 필요합니다.");
		}
		if (req.getImpUid() == null || req.getImpUid().isBlank()) {
			throw new IllegalArgumentException("impUid가 필요합니다.");
		}
		if (req.getMerchantUid() == null || req.getMerchantUid().isBlank()) {
			throw new IllegalArgumentException("merchantUid가 필요합니다.");
		}
		if (req.getAmount() == null) {
			throw new IllegalArgumentException("amount가 필요합니다.");
		}

	}
}
