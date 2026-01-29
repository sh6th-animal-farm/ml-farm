package com.animalfarm.mlf.domain.mypage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.animalfarm.mlf.common.security.SecurityUtil;
import com.animalfarm.mlf.domain.mypage.dto.CarbonHistoryDTO;

@Service
public class MypageService {
	@Autowired
	private MypageRepository mypageRepository;

	public List<CarbonHistoryDTO> getCarbonHistory() {
		Long userId = SecurityUtil.getCurrentUserId();
		return mypageRepository.selectCarbonHistoryByUserId(userId);
	}

}
