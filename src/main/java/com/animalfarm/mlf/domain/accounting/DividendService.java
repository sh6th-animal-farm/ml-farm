package com.animalfarm.mlf.domain.accounting;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.animalfarm.mlf.common.http.ApiResponse;
import com.animalfarm.mlf.common.http.ExternalApiUtil;
import com.animalfarm.mlf.common.security.SecurityUtil;
import com.animalfarm.mlf.domain.accounting.dto.DividendDTO;
import com.animalfarm.mlf.domain.accounting.dto.DividendRequestDTO;
import com.animalfarm.mlf.domain.accounting.dto.DividendResultDTO;
import com.animalfarm.mlf.domain.token.TokenRepository;
import com.animalfarm.mlf.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DividendService {
	private final DividendRepository dividendRepository;
	private final UserRepository userRepository;
	private final ExternalApiUtil externalApiUtil;
	private final TokenRepository tokenRepository;

	// 강황증권 API 서버 주소
	@Value("${api.kh-stock.url}")
	private String KH_BASE_URL;

	public DividendDTO getDividendByID(Long dividendId) {
		return dividendRepository.selectById(dividendId);
	}

	@Transactional
	public void processUserSelection(Long dividendId, String dividendType, String address) throws Exception {
		Long curUserId = SecurityUtil.getCurrentUserId();
		DividendDTO dividend = getDividendByID(dividendId);
		if (!curUserId.equals(dividend.getUserId())) {
			throw new Exception("투자자 본인만 결정할 수 있습니다.");
		}
		if ("CROP".equals(dividendType) && address != null && !address.trim().isEmpty()) {
			userRepository.updateAddress(address, curUserId);
		}
		dividendRepository.updateUserSelection(dividendId, dividendType);
	}

	public void sendDividendData(Long tokenId, List<? extends DividendRequestDTO> divReqDTOList) throws Exception {
		final String finalUrl = KH_BASE_URL + "/api/project/dividend/after/" + tokenId;
		List<DividendResultDTO> result = externalApiUtil.callApi(finalUrl, HttpMethod.POST,
			divReqDTOList,
			new ParameterizedTypeReference<ApiResponse<List<DividendResultDTO>>>() {});
		updateFinalDividendData(divReqDTOList, result, tokenId);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateFinalDividendData(List<? extends DividendRequestDTO> reqList, List<DividendResultDTO> resList,
		Long tokenId) {

		dividendRepository.updatePaidAt(reqList);

	}

}
