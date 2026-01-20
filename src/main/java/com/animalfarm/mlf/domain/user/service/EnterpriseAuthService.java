package com.animalfarm.mlf.domain.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.animalfarm.mlf.domain.user.BizStatusParsed;
import com.animalfarm.mlf.domain.user.NtsBizApiClient;
import com.animalfarm.mlf.domain.user.dto.EnterpriseVerifyRequestDTO;
import com.animalfarm.mlf.domain.user.dto.EnterpriseVerifyResponseDTO;

@Service
public class EnterpriseAuthService {

	@Autowired
	private NtsBizApiClient ntsBizApiClient;

	public EnterpriseVerifyResponseDTO verify(EnterpriseVerifyRequestDTO req) {

		if (isBlank(req.getBNo())) {
			throw new IllegalArgumentException("bNo(사업자등록번호)는 필수입니다.");
		}

		String bNo = normalizeBizNo(req.getBNo());

		EnterpriseVerifyResponseDTO res = new EnterpriseVerifyResponseDTO();

		// 1) 상태조회만 수행
		String statusRaw = ntsBizApiClient.status(bNo);
		res.setStatusRaw(statusRaw);

		BizStatusParsed statusParsed = ntsBizApiClient.parseStatus(statusRaw);

		if (statusParsed.isActive()) {
			res.setVerified(true);
			res.setStatus("ACTIVE");
			res.setMessage("확인 되었습니다.");
		} else {
			res.setVerified(false);
			// CLOSED / SUSPENDED / UNKNOWN 등
			res.setStatus(statusParsed.getStatus());
			res.setMessage(statusParsed.getMessage());
		}

		// 2) 진위확인(validate) - 가입 단계에서는 비활성화
		// String validateRaw = ntsBizApiClient.validate(bNo, req.getStartDt(), req.getPNm(), req.getBNm());
		// res.setValidateRaw(validateRaw);
		// boolean ok = ntsBizApiClient.parseValidateOk(validateRaw);
		// res.setVerified(ok);
		// res.setStatus(ok ? "ACTIVE" : "ACTIVE");
		// res.setMessage(ok ? "사업자 진위확인 성공" : "사업자 정보가 일치하지 않습니다.");

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
