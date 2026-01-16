package com.animalfarm.mlf.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.animalfarm.mlf.domain.user.dto.EnterpriseVerifyRequestDTO;
import com.animalfarm.mlf.domain.user.dto.EnterpriseVerifyResponseDTO;

@Service
public class EnterpriseAuthService {

	@Autowired
	private NtsBizApiClient ntsBizApiClient;

	public EnterpriseVerifyResponseDTO verify(EnterpriseVerifyRequestDTO req) {
		String bNo = normalizeBizNo(req.getBNo());

		if (isBlank(req.getStartDt()) || isBlank(req.getPNm())) {
			throw new IllegalArgumentException("startDt(개업일자)와 pNm(대표자명)은 필수입니다.");
		}

		EnterpriseVerifyResponseDTO res = new EnterpriseVerifyResponseDTO();

		// 1) 상태조회
		String statusRaw = ntsBizApiClient.status(bNo);
		res.setStatusRaw(statusRaw);

		BizStatusParsed statusParsed = ntsBizApiClient.parseStatus(statusRaw);

		if (!statusParsed.isActive()) {
			res.setVerified(false);
			res.setStatus(statusParsed.getStatus()); // CLOSED / SUSPENDED / UNKNOWN
			res.setMessage(statusParsed.getMessage()); // 사유
			return res;
		}

		// 2) 진위확인
		String validateRaw = ntsBizApiClient.validate(bNo, req.getStartDt(), req.getPNm(), req.getBNm());
		res.setValidateRaw(validateRaw);

		boolean ok = ntsBizApiClient.parseValidateOk(validateRaw);

		if (ok) {
			res.setVerified(true);
			res.setStatus("ACTIVE");
			res.setMessage("사업자 진위확인 성공");
		} else {
			res.setVerified(false);
			res.setStatus("ACTIVE"); // 상태는 정상이지만 정보 불일치
			res.setMessage("사업자 정보가 일치하지 않습니다.");
		}

		return res;
	}

	private String normalizeBizNo(String bNo) {
		if (bNo == null) {
			throw new IllegalArgumentException("bNo is null");
		}
		String onlyDigits = bNo.replaceAll("[^0-9]", "");
		if (onlyDigits.length() != 10) {
			throw new IllegalArgumentException("사업자등록번호는 10자리 숫자여야 합니다.");
		}
		return onlyDigits;
	}

	private boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}
}
