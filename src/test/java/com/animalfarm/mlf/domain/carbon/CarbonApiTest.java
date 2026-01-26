package com.animalfarm.mlf.domain.carbon;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import com.animalfarm.mlf.common.ApiResponseDTO;
import com.animalfarm.mlf.common.security.CustomUser;
import com.animalfarm.mlf.domain.carbon.dto.CarbonDetailDTO;
import com.animalfarm.mlf.domain.carbon.dto.CarbonDiscountDTO;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {
	"file:src/main/webapp/WEB-INF/spring/root-context.xml",
	"file:src/main/webapp/WEB-INF/spring/context-datasource.xml"
})
@WebAppConfiguration
public class CarbonApiTest {

	@Autowired
	private CarbonService carbonService;

	@Autowired
	private CarbonRepository carbonRepository;

	@BeforeEach
	void setup() {
		// [필수] SecurityContext에 가짜 유저(ID: 1) 심기 (Service의 getLoginUserId 방어)
		CustomUser mockUser = new CustomUser("test@marifarm.com", "1234",
			Collections.singletonList(new SimpleGrantedAuthority("ROLE_ENTERPRISE")), 415L, "ENTERPRISE");

		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(mockUser, null,
			mockUser.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	@Test
	@DisplayName("CarbonDetailDTO 최종 객체 검증 및 리포트 출력")
	void carbonDetailFinalTest() {
		Long cpId = 1L; // 테스트할 상품 번호

		// 1. 데이터 조회 - 리턴 타입이 ApiResponseDTO로 변경됨
		ApiResponseDTO<CarbonDetailDTO> response = carbonService.selectDetail(cpId);

		// 상자에서 실제 내용물(CarbonDetailDTO)을 꺼냅니다.
		CarbonDetailDTO detail = response.getPayload();

		// [검증] 응답이 성공했는지, 데이터가 잘 들어있는지 확인
		assert response != null;
		assert detail != null;

		Long walletId = carbonRepository.getWalletIdByUserId(415L);
		Long tokenId = carbonRepository.getTokenIdByProjectId(detail.getCarbonInfo().getProjectId());
		BigDecimal actualAmount = carbonRepository.getActualAmount(detail.getCarbonInfo().getProjectId());

		// 중간 계산값 도출 (리포트용)
		List<CarbonDiscountDTO> holdings = carbonService.fetchAllHoldings(walletId);
		CarbonDiscountDTO myBal = holdings.stream()
			.filter(h -> h.getTokenId().equals(tokenId)).findFirst().orElse(new CarbonDiscountDTO());

		// 2. 출력 시작
		System.out.println("\n===============================================");
		System.out.println(">>> [CarbonDetailDTO 최종 객체 검증] CP_ID: " + cpId);
		System.out.println("===============================================");

		System.out.println("\n|-------|");
		System.out.println("|ucl_id |");
		System.out.println("|-------|");
		System.out.printf("|%-7d|\n", walletId);
		System.out.println("|-------|");

		System.out.println(
			"\n|------|-----------|----------|--------------------|-----------------|--------|--------------|---------|------------|-------------|--------------------------|--------------|-------------|----------------|---------------|----------------|----------|");
		System.out.println(
			"|cp_id |project_id |create_at |product_certificate |cp_amount        |cp_type |cp_detail     |cp_price |init_amount |vintage_year |cp_title                  |thumbnail_url |address_sido |address_sigungu |address_street |address_details |farm_name |");
		System.out.println(
			"|------|-----------|----------|--------------------|-----------------|--------|--------------|---------|------------|-------------|--------------------------|--------------|-------------|----------------|---------------|----------------|----------|");
		System.out.printf(
			"|%-6d|%-11d|%-10s|%-20s|%-17.6f|%-8s|%-14s|%-9.2f|%-12s|%-13s|%-26s|%-14s|%-13s|%-16s|%-15s|%-16s|%-10s|\n",
			detail.getCarbonInfo().getCpId(), detail.getCarbonInfo().getProjectId(), "[unread]",
			detail.getCarbonInfo().getProductCertificate(), detail.getCarbonInfo().getCpAmount(),
			detail.getCarbonInfo().getCpType(), "인증 데이터", detail.getCarbonInfo().getCpPrice(),
			"[unread]", detail.getCarbonInfo().getVintageYear(), detail.getCarbonInfo().getCpTitle(),
			"[unread]", detail.getAddressSido(), detail.getAddressSigungu(), "null", "null", detail.getFarmName());
		System.out.println(
			"|------|-----------|----------|--------------------|-----------------|--------|--------------|---------|------------|-------------|--------------------------|--------------|-------------|----------------|---------------|----------------|----------|");

		System.out.println("\n|---------|");
		System.out.println("|token_id |");
		System.out.println("|---------|");
		System.out.printf("|%-9d|\n", tokenId);
		System.out.println("|---------|");

		System.out.println("\n|------------------|");
		System.out.println("|discount_rate_pct |");
		System.out.println("|------------------|");
		System.out.printf("|%-18.2f|\n", detail.getUserBenefit().getDiscountRate());
		System.out.println("|------------------|");

		System.out.println("\n[1. 상품 기본 정보]");
		System.out.println(" - 상품 번호: " + detail.getCarbonInfo().getCpId());
		System.out.println(" - 총 발행량: " + detail.getCarbonInfo().getCpAmount());
		System.out.println(" - 기본 단가: " + detail.getCarbonInfo().getCpPrice() + "원");

		// 중간 계산 과정 수식 재현
		BigDecimal totalCorpTokens = (myBal.getEnterpriseTotal() == null) ? BigDecimal.ONE : myBal.getEnterpriseTotal();
		BigDecimal limitShareRatio = myBal.getMyBalance().divide(totalCorpTokens, 10, RoundingMode.HALF_UP);
		BigDecimal discountShareRatio = myBal.getMyBalance().divide(actualAmount, 10, RoundingMode.HALF_UP);

		System.out.println("\n[2. 계산에 필요한 애들]");
		System.out.println(" - actualAmount (프로젝트 총투자금): " + actualAmount);
		System.out.println(" - totalCorpTokens (증권사 총토큰): " + totalCorpTokens);
		System.out.println(" - limitShareRatio (한도용 지분율): " + limitShareRatio);
		System.out.println(" - maxLimit (계산된 수량한도): " + detail.getUserBenefit().getUserMaxLimit());
		System.out.println(" - discountShareRatio (할인용 지분율): " + discountShareRatio);
		System.out
			.println(" - discountSharePercent (할인용 %): " + discountShareRatio.multiply(new BigDecimal("100")) + "%");

		System.out.println("\n[3. 유저 맞춤 혜택 (계산 결과)]");
		System.out.println(" - 내 토큰 잔고: " + detail.getUserBenefit().getMyTokenBalance());
		System.out.println(" - 나의 지분율 기반 할인율: " + detail.getUserBenefit().getDiscountRate() + "%");
		System.out.println(" - 최대 구매 가능 수량: " + detail.getUserBenefit().getUserMaxLimit());
		System.out.println(" - 할인 적용된 최종 단가: " + detail.getUserBenefit().getCurrentPrice() + "원");

		System.out.println("\n===============================================\n");
	}
}