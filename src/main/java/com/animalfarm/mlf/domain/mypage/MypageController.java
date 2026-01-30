package com.animalfarm.mlf.domain.mypage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.domain.mypage.dto.CarbonHistoryDTO;
import com.animalfarm.mlf.domain.mypage.dto.PasswordUpdateRequestDTO;
import com.animalfarm.mlf.domain.mypage.dto.ProfileDTO;
import com.animalfarm.mlf.domain.mypage.dto.ProfileUpdateRequestDTO;
import com.animalfarm.mlf.domain.mypage.dto.ProjectDTO;

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

	@GetMapping("/profile")
	public ResponseEntity<ProfileDTO> getProfile() {
		return ResponseEntity.ok(mypageService.getProfile());
	}

	@PatchMapping("/profile")
	public ResponseEntity<Void> updateProfile(@RequestBody
	ProfileUpdateRequestDTO req) {
		mypageService.updateProfile(req);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/password")
	public ResponseEntity<Void> updatePassword(@RequestBody
	PasswordUpdateRequestDTO dto) {
		mypageService.updatePassword(dto);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/projects")
	public ResponseEntity<List<ProjectDTO>> getMyProjects() {
		return ResponseEntity.ok(mypageService.getMyProjects());
	}
}
