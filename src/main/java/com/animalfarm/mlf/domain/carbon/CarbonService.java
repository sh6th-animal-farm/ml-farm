package com.animalfarm.mlf.domain.carbon;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.animalfarm.mlf.common.security.CustomUser;
import com.animalfarm.mlf.domain.carbon.dto.CarbonDTO;
import com.animalfarm.mlf.domain.carbon.dto.CarbonDetailDTO;
import com.animalfarm.mlf.domain.carbon.dto.CarbonListDTO;
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
	 * [핵심 분리 로직] 유저의 지분(한도) 및 할인율을 계산하는 순수 비즈니스 메서드 - API 호출 방식
	 * - 전체 탄소상품 리스트 조회, 구매 검증 로직 등에서 공통으로 호출됩니다.
	 * - 어떤 DTO에도 종속되지 않고 '유저 혜택'만 계산합니다.
	 * - 전체 리스트, 상세 페이지, 구매 검증 로직에서 모두 이 메서드를 호출합니다.
	 */
	private UserBenefitDTO calculateBenefit(Long projectId, BigDecimal cpAmount, BigDecimal cpPrice, Long userId) {

		try {
			// A. 내 지분 정보 가져오기 (강황증권 API 쏘기)
			// GET http://192.168.0.156:9090/holdings?projectId=1&userId=10
			String myBalanceUrl = GANGHWANG_API_URL + "/holdings?projectId=" + projectId + "&userId=" + userId;
			BigDecimal myBalance = restTemplate.getForObject(myBalanceUrl, BigDecimal.class);
			if (myBalance == null) {
				myBalance = BigDecimal.ZERO;
			}

			// B. 기업 회원 전체 지분 합계 가져오기 (강황증권 API 쏘기)
			// GET http://api.ganghwang-securities.com/holdings/total-enterprise?projectId=1
			String totalBalanceUrl = GANGHWANG_API_URL + "/holdings/total-enterprise?projectId=" + projectId;
			BigDecimal totalCorpBalance = restTemplate.getForObject(totalBalanceUrl, BigDecimal.class);

			// C. 0으로 나누기 방지 및 한도 계산
			if (totalCorpBalance == null || totalCorpBalance.compareTo(BigDecimal.ZERO) == 0) {
				totalCorpBalance = BigDecimal.ONE;
			}

			// 기업 회원들의 지분 대비 유저의 지분율
			BigDecimal shareRatio = myBalance.divide(totalCorpBalance, 10, RoundingMode.HALF_UP);
			// 최대 구매 가능량
			BigDecimal maxLimit = cpAmount.multiply(shareRatio).setScale(4, RoundingMode.HALF_UP);

			// D. 할인 정책 조회 (이건 마리팜 DB니까 기존처럼 Repository 사용)
			BigDecimal discountRate = carbonRepository.getDiscountRate(shareRatio.multiply(new BigDecimal("100")));
			if (discountRate == null) {
				discountRate = BigDecimal.ZERO;
			}

			BigDecimal currentPrice = cpPrice
				.multiply(BigDecimal.ONE.subtract(discountRate.divide(new BigDecimal("100"))));

			return UserBenefitDTO.builder()
				.userMaxLimit(maxLimit)
				.discountRate(discountRate)
				.currentPrice(currentPrice)
				.myTokenBalance(myBalance)
				.build();

		} catch (Exception e) {
			// API 장애 시 기본값(할인 없음, 한도 0 등)을 리턴하거나 예외를 던짐
			System.out.println("========================================");
			System.out.println("[ERROR] 강황증권 API 호출 중 오류 발생!!!");
			System.out.println("메시지: " + e.getMessage());
			System.out.println("========================================");
			throw new RuntimeException("지분 정보를 가져올 수 없어 계산이 불가능합니다.");
		}

	}

	// ---------------------------------------------------------
	// 2. 외부 노출용 서비스 메서드 (컨트롤러에서 호출)
	// ---------------------------------------------------------

	// [전체 조회]
	public List<CarbonListDTO> selectAll() {
		return carbonRepository.selectAll();
	}

	// [카테고리 조회]
	public List<CarbonListDTO> selectByCategory(String category) {
		return carbonRepository.selectByCategory(category);
	}

	// [상세 조회]
	public CarbonDetailDTO selectDetail(Long cpId) {
		Long userId = getLoginUserId();

		// 1. 매퍼에서 상품 정보 + 농장 주소 정보가 담긴 DTO를 통째로 가져옴
		CarbonDetailDTO detail = carbonRepository.selectDetail(cpId);

		// 2. 계산기에 필요한 데이터는 detail 내부의 carbonInfo에서 꺼냄
		CarbonDTO info = detail.getCarbonInfo();

		// 3. API 기반 계산기 실행
		UserBenefitDTO benefit = calculateBenefit(
			info.getProjectId(),
			info.getCpAmount(),
			info.getCpPrice(),
			userId);

		// 4. 계산된 혜택 정보를 detail 객체에 세팅
		detail.setUserBenefit(benefit);

		return detail;
	}
}
