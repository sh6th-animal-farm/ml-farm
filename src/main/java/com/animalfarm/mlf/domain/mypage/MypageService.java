package com.animalfarm.mlf.domain.mypage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animalfarm.mlf.common.security.SecurityUtil;
import com.animalfarm.mlf.domain.mypage.dto.CarbonHistoryDTO;
import com.animalfarm.mlf.domain.mypage.dto.PasswordUpdateRequestDTO;
import com.animalfarm.mlf.domain.mypage.dto.ProfileDTO;
import com.animalfarm.mlf.domain.mypage.dto.ProfileUpdateRequestDTO;
import com.animalfarm.mlf.domain.mypage.dto.ProjectDTO;

@Service
public class MypageService {
	@Autowired
	private MypageRepository mypageRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<CarbonHistoryDTO> getCarbonHistory() {
		Long userId = SecurityUtil.getCurrentUserId();
		return mypageRepository.selectCarbonHistoryByUserId(userId);
	}

	public ProfileDTO getProfile() {
		Long userId = SecurityUtil.getCurrentUserId();
		return mypageRepository.selectProfile(userId);
	}

	public void updateProfile(ProfileUpdateRequestDTO req) {
		Long userId = SecurityUtil.getCurrentUserId();
		mypageRepository.updateProfile(userId, req);
	}

	@Transactional
	public void updatePassword(PasswordUpdateRequestDTO dto) {

		Long userId = SecurityUtil.getCurrentUserId();

		if (dto == null
			|| dto.getCurrentPassword() == null
			|| dto.getNewPassword() == null) {
			throw new IllegalArgumentException("비밀번호 입력값이 올바르지 않습니다.");
		}

		// 1. 현재 비밀번호(암호화) 조회
		String encodedPassword = mypageRepository.selectPasswordByUserId(userId);
		if (encodedPassword == null) {
			throw new IllegalStateException("사용자 정보를 찾을 수 없습니다.");
		}

		// 2. 현재 비밀번호 검증
		if (!passwordEncoder.matches(dto.getCurrentPassword(), encodedPassword)) {
			throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
		}

		// 3. 새 비밀번호 암호화
		String newEncodedPassword = passwordEncoder.encode(dto.getNewPassword());

		// 4. 비밀번호 업데이트
		mypageRepository.updatePassword(userId, newEncodedPassword);
	}

	public List<ProjectDTO> getMyProjects() {
		Long userId = SecurityUtil.getCurrentUserId();
		return mypageRepository.selectMyProjects(userId);
	}
}
