package com.animalfarm.mlf.domain.mypage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.domain.mypage.dto.CarbonHistoryDTO;

@RestController
@RequestMapping("/api/mypage")
public class MypageController {

	@Autowired
	private MypageService mypageService;

	@GetMapping("/carbon-history")
	public ResponseEntity<List<CarbonHistoryDTO>> getCarbonHistory() {
		// 서비스 내부에서 유저 ID를 조회하도록 설계된 메서드를 호출합니다.
		return ResponseEntity.ok(mypageService.getCarbonHistory());
	}
}
