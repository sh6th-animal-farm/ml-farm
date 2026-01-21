package com.animalfarm.mlf.domain.carbon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.common.security.CustomUser;
import com.animalfarm.mlf.domain.carbon.dto.CarbonDetailDTO;

import lombok.extern.slf4j.Slf4j;

//탄소 마켓 상세 조회 및 권한 제어 컨트롤러

@Slf4j
@RestController
@RequestMapping("/api/carbon")
public class CarbonController {

	@Autowired
	private CarbonService carbonService;

	/**
	 * 특정 탄소 상품의 상세 정보 및 사용자별 최적화된 구매 조건(한도, 할인율) 조회
	 * @param cpId 조회할 탄소 상품의 고유 ID
	 * @return 성공 시 CarbonDetailDTO 객체와 200 OK 반환
	 */
	@GetMapping("/detail/{cpId}")
	public ResponseEntity<?> getCarbonDetail(@PathVariable
	Long cpId) {

		try {
			// 1. 시큐리티 보관함에서 우리가 커스텀한 CustomUser 정보를 꺼냅니다.
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			CustomUser user = (CustomUser)auth.getPrincipal();

			CarbonDetailDTO detailDTO = carbonService.calculateCarbonDetail(cpId, user.getUserId());

			// 성공 시: ResponseEntity<CarbonDetailDTO>가 반환됨
			return ResponseEntity.ok(detailDTO);

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("데이터 조회 중 오류가 발생했습니다: " + e.getMessage());
		}
	}

}
