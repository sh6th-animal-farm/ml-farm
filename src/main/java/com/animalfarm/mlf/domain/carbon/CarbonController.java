package com.animalfarm.mlf.domain.carbon;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.domain.carbon.dto.CarbonDetailDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//탄소 마켓 상세 조회 및 권한 제어 컨트롤러
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/carbon")
public class CarbonController {

	private final CarbonService carbonService;

	/**
	 * 특정 탄소 상품의 상세 정보 및 사용자별 최적화된 구매 조건(한도, 할인율) 조회
	 * @param cpId 조회할 탄소 상품의 고유 ID
	 * @return 성공 시 CarbonDetailDTO 객체와 200 OK 반환
	 */
	@GetMapping("/detail/{cpId}")
	public ResponseEntity<CarbonDetailDTO> getCarbonDetail(@PathVariable Long cpId{

		// 1. 시큐리티에서 유저 정보 추출
		Authentication auth = SecurityContextHolder.getContext().getAuthenication();
		CustomUser
	}

}
