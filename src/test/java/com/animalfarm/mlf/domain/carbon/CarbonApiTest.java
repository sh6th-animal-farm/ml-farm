package com.animalfarm.mlf.domain.carbon;

import java.util.List;

// [수정] JUnit 4가 아닌 JUnit 5(Jupiter)용 Test를 임포트해야 합니다.
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

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

	@Test // 이제 JUnit 5 엔진이 이 메서드를 인식합니다.
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