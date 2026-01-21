package com.animalfarm.mlf.domain.carbon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animalfarm.mlf.domain.carbon.dto.CarbonDetailDTO;

/**
 * [탄소 마켓 핵심 서비스]
 * - 금산분리 원칙에 따라 강황증권 DB와 마리팜 DB를 연동합니다.
 * - 기업 회원의 지분(토큰 보유량)을 분석하여 구매 한도와 할인율을 계산합니다.
 */
@Service
public class CarbonService {

	@Autowired
	private CarbonRepository carbonRepository;

	/**
	 * [핵심 로직] 유저별 탄소 배출권 구매 한도 및 할인율 계산
	 * @param cpId   탄소 프로젝트 ID
	 * @param userId 현재 로그인한 유저의 PK (CustomUser에서 추출됨)
	 */
	@Transactional(readOnly = true) // 단순 조회이므로 readOnly로 성능 최적화
	public CarbonDetailDTO calculateCarbonDetail(Long cpId, Long userId) {

		return null;

	}

	public List<CarbonListDTO> selectAll() {
		return carbonRepository.selectAll();
	}

	public List<CarbonListDTO> selectByCategory(String category) {
		return carbonRepository.selectByCategory(category);
	}
}
