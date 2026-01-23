package com.animalfarm.mlf.domain.carbon;

import java.util.ArrayList;
import java.util.List;

// [수정] JUnit 4가 아닌 JUnit 5(Jupiter)용 Test를 임포트해야 합니다.
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import com.animalfarm.mlf.common.security.CustomUser;
import com.animalfarm.mlf.domain.carbon.dto.CarbonDetailDTO;
import com.animalfarm.mlf.domain.carbon.dto.GanghwangBalanceDTO;

@ExtendWith(SpringExtension.class) // JUnit 5용 스프링 연결 다리
@ContextConfiguration(locations = {
	"file:src/main/webapp/WEB-INF/spring/root-context.xml", // 서비스/빈 스캔 설정
	"file:src/main/webapp/WEB-INF/spring/context-datasource.xml" // DB 설정
})
@TestPropertySource(properties = {
	"nts.serviceKey=test_key",
	"nts.baseUrl=http://localhost:8080"
})
@WebAppConfiguration
public class CarbonApiTest {

	@Autowired
	private CarbonService carbonService;

	@Test
	public void verifyCarbonDetail() {
		// 1. 시큐리티 가짜 로그인 설정 (5개의 인자를 정확히 전달)
		// 인자 순서: username, password, authorities, userId, userRole
		CustomUser mockUser = new CustomUser(
			"tester", // username
			"1234", // password
			new ArrayList<>(), // authorities
			8888L, // userId (지갑 번호와 매칭될 ID)
			"ENTERPRISE" // userRole (추가된 인자: "USER" 또는 "ENTERPRISE")
		);

		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(mockUser, null,
			mockUser.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		// 2. 테스트할 상품 ID (마리팜 DB에 존재하는 cpId)
		Long cpId = 1L;

		System.out.println("\n===============================================");
		System.out.println(">>> [CarbonDetailDTO 최종 객체 검증] CP_ID: " + cpId);
		System.out.println("===============================================");

		try {
			// 3. 서비스 호출 (내부에서 강황증권 API 호출 및 혜택 계산이 진행됨)
			CarbonDetailDTO detail = carbonService.selectDetail(cpId);

			if (detail != null && detail.getCarbonInfo() != null) {
				// [A] 마리팜 상품 기본 정보
				System.out.println("[1. 상품 기본 정보]");
				System.out.println(" - 상품 번호: " + detail.getCarbonInfo().getCpId());
				System.out.println(" - 총 발행량: " + detail.getCarbonInfo().getCpAmount());
				System.out.println(" - 기본 단가: " + detail.getCarbonInfo().getCpPrice() + "원");

				// [B] 유저 혜택 정보 (UserBenefitDTO)
				System.out.println("\n[2. 유저 맞춤 혜택 (계산 결과)]");
				System.out.println(" - 내 토큰 잔고: " + detail.getUserBenefit().getMyTokenBalance());
				System.out.println(" - 나의 지분율 기반 할인율: " + detail.getUserBenefit().getDiscountRate() + "%");
				System.out.println(" - 최대 구매 가능 수량: " + detail.getUserBenefit().getUserMaxLimit());
				System.out.println(" - 할인 적용된 최종 단가: " + detail.getUserBenefit().getCurrentPrice() + "원");
			}
		} catch (Exception e) {
			System.err.println(">>> [에러] 상세 조회 실패: " + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("===============================================\n");
	}

	//@Test // 이제 JUnit 5 엔진이 이 메서드를 인식합니다.
	public void verifyApiData() {
		// [Parameter] 지갑 번호
		Long walletId = 8888L;

		System.out.println("\n===============================================");
		System.out.println(">>> [API 호출 테스트 시작] Wallet ID: " + walletId);
		System.out.println("===============================================");

		try {
			// [주의] CarbonService의 fetchAllHoldings가 'public'으로 되어 있어야 호출 가능합니다!
			List<GanghwangBalanceDTO> results = carbonService.fetchAllHoldings(walletId);

			if (results != null && !results.isEmpty()) {
				System.out.println(">>> [Response Body 수신 성공]");

				for (GanghwangBalanceDTO data : results) {
					System.out.println("-----------------------------------------------");
					System.out.println("토큰 번호 (Token ID) : " + data.getTokenId());
					System.out.println("기업 총 보유량 (Enterprise Total) : " + data.getEnterpriseTotal());
					System.out.println("내 보유량 (My Balance) : " + data.getMyBalance());
				}
			} else {
				System.out.println(">>> [결과] 응답 데이터가 비어있습니다. 지갑 번호를 확인하세요.");
			}

		} catch (Exception e) {
			System.err.println(">>> [에러] 통신 중 예외 발생: " + e.getMessage());
			e.printStackTrace(); // 상세 에러 추적을 위해 추가
		}

		System.out.println("===============================================\n");
	}
}