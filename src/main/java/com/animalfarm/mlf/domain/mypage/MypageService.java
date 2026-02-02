package com.animalfarm.mlf.domain.mypage;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.animalfarm.mlf.common.ApiResponseDTO;
import com.animalfarm.mlf.common.PagedResponseDTO;
import com.animalfarm.mlf.common.security.SecurityUtil;
import com.animalfarm.mlf.domain.mypage.dto.CarbonHistoryDTO;
import com.animalfarm.mlf.domain.mypage.dto.HoldingDTO;
import com.animalfarm.mlf.domain.mypage.dto.MyTransactionHistDTO;
import com.animalfarm.mlf.domain.mypage.dto.PasswordUpdateRequestDTO;
import com.animalfarm.mlf.domain.mypage.dto.ProfileDTO;
import com.animalfarm.mlf.domain.mypage.dto.ProfileUpdateRequestDTO;
import com.animalfarm.mlf.domain.mypage.dto.ProjectDTO;
import com.animalfarm.mlf.domain.mypage.dto.ProjectTabsDTO;
import com.animalfarm.mlf.domain.mypage.dto.WalletDTO;

@Service
public class MypageService {
	@Autowired
	private MypageRepository mypageRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// 강황증권 API 서버 주소

	private final String GANGHWANG_API_URL = "http://kh-holdings.cloud/";

	// ---------------------------------------------------------
	// 거래 내역 조회
	// ---------------------------------------------------------
	public List<MyTransactionHistDTO> getTransactionHistory(int page, int period, String category) {
		Long walletId = validateAndGetWalletId();
		if (walletId == null) {
			return new ArrayList<>();
		}

		// 별도의 로직 없이 바로 외부 API 호출
		return fetchList(walletId, page, period, category);
	}

	private List<MyTransactionHistDTO> fetchList(Long walletId, int page, int period, String apiCategory) {
		try {
			// 변경된 명세: /api/my/transaction/{walletId}?page=..&period=..&category=..
			StringBuilder urlBuilder = new StringBuilder(GANGHWANG_API_URL)
				.append("api/my/transaction/").append(walletId)
				.append("?page=").append(page)
				.append("&period=").append(period);

			// category가 있을 때만 붙임 (필수가 아니라면 null 체크 유지)
			// 명세상 TOKEN, PROJECT 등이 default값 역할을 하므로 무조건 보내는 게 안전함
			if (apiCategory != null && !apiCategory.isEmpty()) {
				urlBuilder.append("&category=").append(apiCategory);
			}

			ResponseEntity<ApiResponseDTO<List<MyTransactionHistDTO>>> response = restTemplate.exchange(
				urlBuilder.toString(), HttpMethod.GET, null,
				new ParameterizedTypeReference<ApiResponseDTO<List<MyTransactionHistDTO>>>() {});

			return (response.getBody() != null) ? response.getBody().getPayload() : new ArrayList<>();

		} catch (Exception e) {
			System.err.println("[ERROR] API 호출 실패: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	// ---------------------------------------------------------
	// 탄소 구매 내역 조회
	// ---------------------------------------------------------

	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy. MM. dd");

	public List<CarbonHistoryDTO> getCarbonHistory() {
		Long userId = SecurityUtil.getCurrentUserId();
		return mypageRepository.selectCarbonHistoryByUserId(userId);
	}

	// ---------------------------------------------------------
	// 나의 지갑
	// ---------------------------------------------------------

	// 공통 유틸리티: 연동된 지갑 ID 확인
	private Long validateAndGetWalletId() {
		Long userId = SecurityUtil.getCurrentUserId();
		if (userId == null) {
			return null;
		}
		return mypageRepository.getWalletIdByUserId(userId);
	}

	// 지갑 요약 정보 조회
	public WalletDTO getWalletInfo() {
		Long walletId = validateAndGetWalletId();
		if (walletId == null) {
			return null; // 미연동 사용자 처리
		}

		try {
			String url = GANGHWANG_API_URL + "api/my/wallet/" + walletId;
			ResponseEntity<ApiResponseDTO<WalletDTO>> response = restTemplate.exchange(
				url, HttpMethod.GET, null,
				new ParameterizedTypeReference<ApiResponseDTO<WalletDTO>>() {});

			return (response.getBody() != null) ? response.getBody().getPayload() : null;
		} catch (Exception e) {
			System.err.println("[ERROR] 지갑 API 호출 실패: " + e.getMessage());
			return null;
		}
	}

	// 보유 토큰 목록 조회 (페이징)
	public List<HoldingDTO> getHoldings(int page) {
		Long walletId = validateAndGetWalletId();
		if (walletId == null) {
			return new ArrayList<>();
		}

		try {
			String url = GANGHWANG_API_URL + "api/my/token/" + walletId + "?page=" + page;
			ResponseEntity<ApiResponseDTO<List<HoldingDTO>>> response = restTemplate.exchange(
				url, HttpMethod.GET, null,
				new ParameterizedTypeReference<ApiResponseDTO<List<HoldingDTO>>>() {});

			return (response.getBody() != null) ? response.getBody().getPayload() : new ArrayList<>();
		} catch (Exception e) {
			System.err.println("[ERROR] 토큰 API 호출 실패: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	// 계좌 연동하기
	@Transactional
	public Long linkGangHwangAccount() {
		Long userId = SecurityUtil.getCurrentUserId();
		if (userId == null) {
			return null;
		}

		// 이미 연동된 회원인지 먼저 확인
		Long existingWalletId = mypageRepository.getWalletIdByUserId(userId);
		if (existingWalletId != null) {
			// 이미 연동된 경우, 특수한 값(예: -1)을 반환하거나 예외를 던져 알림 처리
			return -1L;
		}

		try {
			// 1. 강황증권 API로 {userId}에 해당하는 지갑 정보 조회 (GET 방식)
			String url = GANGHWANG_API_URL + "api/my/account/" + userId;

			ResponseEntity<ApiResponseDTO<Long>> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<ApiResponseDTO<Long>>() {});

			Long walletId = (response.getBody() != null) ? response.getBody().getPayload() : null;

			// 2. 결과가 있으면 우리 DB(user_certificate_links)에 저장
			if (walletId != null) {
				// 랜덤 토큰 및 만료 시간 생성 (테이블 NOT NULL 제약 조건 대응)
				String randomAccessToken = UUID.randomUUID().toString();
				String randomRefreshToken = UUID.randomUUID().toString();

				// 3. DB 저장 (certificates_id는 1로 고정)
				mypageRepository.upsertUserWalletLink(
					userId,
					walletId,
					randomAccessToken,
					randomRefreshToken);
				return walletId;
			}
		} catch (Exception e) {
			System.err.println("[ERROR] 계좌 연동 실패 (계좌 없음 또는 통신 오류): " + e.getMessage());
		}
		return null; // 계좌가 없으면 null 반환
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

	private String mapProjectStatus(String s) {
		if (s == null) {
			return "-";
		}
		switch (s) {
			case "SUBSCRIPTION":
				return "청약중";
			case "ANNOUNCEMENT":
				return "공고중";
			case "INPROGRESS":
				return "진행중";
			case "ENDED":
				return "종료";
			case "PREPARING":
				return "준비중";
			default:
				return s;
		}
	}

	private String mapSubscriptionStatus(String s) {
		if (s == null) {
			return null;
		}
		switch (s) {
			case "PENDING":
				return "청약중";
			case "APPROVED":
				return "당첨";
			case "REJECTED":
				return "낙첨";
			case "CANCELED":
				return "취소";
			default:
				return s;
		}
	}

	private String fmt(OffsetDateTime dt) {
		return dt == null ? "-" : dt.format(DF);
	}

	private void decorate(ProjectDTO p, boolean joinedTab) {
		p.setPeriodText(fmt(p.getProjectStartDate()) + " - " + fmt(p.getProjectEndDate()));
		p.setStatusText1(mapProjectStatus(p.getProjectStatus()));
		p.setStatusText2(joinedTab ? mapSubscriptionStatus(p.getSubscriptionStatus()) : null);
	}

	public ProjectTabsDTO getProjectTabs() {
		Long userId = SecurityUtil.getCurrentUserId();
		// 탭 카운트는 전체 기준(필터 ALL)
		int joined = (int)mypageRepository.countJoinedProjectCards(userId, "ALL");
		int starred = (int)mypageRepository.countStarredProjectCards(userId, "ALL");
		return new ProjectTabsDTO(joined, starred);
	}

	@Transactional(readOnly = true)
	public PagedResponseDTO<ProjectDTO> getProjectCards(String type, String status, int page, int size) {
		Long userId = SecurityUtil.getCurrentUserId();
		int limit = Math.max(1, Math.min(size, 30));
		int offset = Math.max(0, (page - 1) * limit);

		boolean joinedTab = "JOIN".equalsIgnoreCase(type);
		List<ProjectDTO> list;
		long total;

		if (joinedTab) {
			list = mypageRepository.selectJoinedProjectCards(userId, status, limit, offset);
			total = mypageRepository.countJoinedProjectCards(userId, status);
		} else {
			list = mypageRepository.selectStarredProjectCards(userId, status, limit, offset);
			total = mypageRepository.countStarredProjectCards(userId, status);
		}

		for (ProjectDTO p : list) {
			decorate(p, joinedTab);
		}

		boolean hasNext = (offset + list.size()) < total;
		return new PagedResponseDTO<>(list, page, limit, total, hasNext);
	}

	@Transactional
	public void setStarred(Long projectId, boolean starred) {
		Long userId = SecurityUtil.getCurrentUserId();
		mypageRepository.upsertStarredProject(userId, projectId, starred);
	}

}
