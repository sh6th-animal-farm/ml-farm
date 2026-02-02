package com.animalfarm.mlf.domain.subscription;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.animalfarm.mlf.common.MailService;
import com.animalfarm.mlf.common.http.ApiResponse;
import com.animalfarm.mlf.common.http.ExternalApiUtil;
import com.animalfarm.mlf.common.security.SecurityUtil;
import com.animalfarm.mlf.domain.project.ProjectService;
import com.animalfarm.mlf.domain.project.dto.TokenLedgerDTO;
import com.animalfarm.mlf.domain.refund.RefundDTO;
import com.animalfarm.mlf.domain.refund.RefundRepository;
import com.animalfarm.mlf.domain.refund.RefundService;
import com.animalfarm.mlf.domain.retry.ApiRetryQueueDTO;
import com.animalfarm.mlf.domain.retry.ApiRetryService;
import com.animalfarm.mlf.domain.retry.ApiType;
import com.animalfarm.mlf.domain.subscription.dto.AllocationRequestDTO;
import com.animalfarm.mlf.domain.subscription.dto.AllocationResultDTO;
import com.animalfarm.mlf.domain.subscription.dto.AllocationTokenDTO;
import com.animalfarm.mlf.domain.subscription.dto.InvestorDTO;
import com.animalfarm.mlf.domain.subscription.dto.ProjectStartCheckDTO;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionApplicationDTO;
import com.animalfarm.mlf.domain.subscription.dto.SubscriptionHistDTO;
import com.animalfarm.mlf.domain.token.TokenRepository;
import com.animalfarm.mlf.domain.token.TokenService;
import com.animalfarm.mlf.domain.user.dto.WalletDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {
	private final ApplicationContext applicationContext;
	private final SubscriptionRepository subscriptionRepository;
	private final RefundRepository refundRepository;
	private final ExternalApiUtil externalApiUtil;
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	@Autowired
	private SubscriptionService self;

	@Autowired
	private MailService mailService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	TokenRepository tokenReopsitory;

	@Autowired
	RefundService refundService;

	// 강황증권 API 서버 주소
	@Value("${api.kh-stock.url}")
	private String KH_BASE_URL;

	public boolean selectAndCancel(Long projectId) throws Exception {
		Long userId = SecurityUtil.getCurrentUserId();

		// 청약 내역 조회
		SubscriptionHistDTO subscriptionHistDTO = subscriptionRepository.selectPaid(userId, projectId);
		if (subscriptionHistDTO == null) {
			throw new Exception("청약 내역이 존재하지 않습니다.");
		}

		// 멱등성 키 생성
		String idempotencyKey = "SUB-CANCEL-" + subscriptionHistDTO.getShId();

		// url 생성
		String url = KH_BASE_URL + "api/project/cancel/" + subscriptionHistDTO.getExternalRefId();
		RefundDTO refundDTO = null;
		try {
			// 취소 및 환불 요청
			refundDTO = externalApiUtil.callApi(url, HttpMethod.POST, subscriptionHistDTO,
				new ParameterizedTypeReference<ApiResponse<RefundDTO>>() {}, idempotencyKey);

			if (refundDTO == null) {
				throw new Exception("환불 처리에 실패했습니다.");
			}

			log.info("[Service] 증권사 청약 취소 완료");

			afterSubsRefundRequest(subscriptionHistDTO, refundDTO);

		} catch (RuntimeException e) {
			// 유틸리티에서 던진 구체적인 에러 메시지("잔액 부족" 등)가 이곳으로 전달됨
			log.error("[Service] 청약 취소 실패. 재시도 큐에 등록합니다. 사유: {}", e.getMessage());

			Object[] params = new Object[] { subscriptionHistDTO.getExternalRefId() };

			ApiRetryService apiRetryService = applicationContext.getBean(ApiRetryService.class);
			apiRetryService.registerRetry(ApiType.SUB_CANCEL, subscriptionHistDTO, params, idempotencyKey);

			return false;
		}

		return true;
	}

	// 증권사 환불 요청 성공 이후 작업
	private void afterSubsRefundRequest(SubscriptionHistDTO subscriptionHistDTO, RefundDTO refundDTO) throws Exception {
		refundDTO.setShId(subscriptionHistDTO.getShId());
		refundDTO.setProjectId(subscriptionHistDTO.getProjectId());
		refundDTO.setUclId(refundDTO.getWalletId());
		refundDTO.setExternalRefId(refundDTO.getTransactionId());
		refundDTO.setUserId(subscriptionHistDTO.getUserId());
		refundDTO.setRefundType("ALL"); // 환불 완료 상태
		refundDTO.setReasonCode("USER_CANCEL"); // 사유
		refundDTO.setStatus("SUCCESS"); // 처리 상태

		subscriptionHistDTO.setSubscriptionStatus("CANCELED");
		subscriptionHistDTO.setPaymentStatus("REFUNDED");
		subscriptionHistDTO.setCanceledAt(OffsetDateTime.now());

		self.updateRefundAndSubsTable(refundDTO, subscriptionHistDTO);
	}

	// 증권사 환불 요청 "재시도" 성공 이후 작업
	public void afterSubsRefundRetry(ApiRetryQueueDTO retry, RefundDTO refundDTO) throws Exception {
		// 저장된 Payload(JSON)를 다시 DTO로 변환
		SubscriptionHistDTO hist = objectMapper.readValue(retry.getPayload(), SubscriptionHistDTO.class);

		afterSubsRefundRequest(hist, refundDTO);
	}

	// 환불 내역, 청약 내역 DB 수정
	@Transactional(rollbackFor = Exception.class)
	public void updateRefundAndSubsTable(RefundDTO refundDTO, SubscriptionHistDTO subscriptionHistDTO)
		throws Exception {
		if (refundRepository.insertRefund(refundDTO) <= 0) {
			throw new Exception("내부 환불 내역 기록 실패 (DB 오류)");
		}
		if (subscriptionRepository.update(subscriptionHistDTO) <= 0) {
			throw new Exception("청약 상태 변경 실패 (DB 오류)");
		}
	}

	public boolean subscriptionApplication(SubscriptionApplicationDTO subscriptionInsertDTO) {
		Long userId = SecurityUtil.getCurrentUserId();
		subscriptionInsertDTO.setUserId(userId);
		return subscriptionRepository.subscriptionApplication(subscriptionInsertDTO);
	}

	// 1. 외부 API 호출 (트랜잭션 없음)
	public void postApplication(SubscriptionApplicationDTO dto) {
		Long uclId = subscriptionRepository.selectUclId(dto.getUserId());
		dto.setUclId(uclId);
		String targetUrl = KH_BASE_URL + "api/project/application/" + dto.getTokenId() + "?subscriptionId="
			+ dto.getShId() + "&walletId=" + dto.getUclId() + "&amount=" + dto.getSubscriptionAmount();

		try {
			// [API 호출 전] DB 커넥션 안 잡음
			ResponseEntity<ApiResponse> responseEntity = restTemplate.postForEntity(targetUrl, null, ApiResponse.class);

			if (responseEntity.getStatusCodeValue() == 200) {
				Object payload = responseEntity.getBody().getPayload();

				if (payload != null) {
					dto.setPaymentStatus("PAID");
					dto.setExternalRefId(Long.valueOf(String.valueOf(payload)));
					// [성공 시 DB 처리 호출] 이 시점에만 트랜잭션 시작
					updateDatabaseInfo(dto);
				} else {
					dto.setPaymentStatus("FAILED");
					// [실패 시 DB 처리 호출]
					updateDatabaseInfo(dto);
					throw new RuntimeException("empty_payload");
				}
			}
		} catch (Exception e) {
			log.error("통신 실패: {}", e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	// 2. 내 DB 동작 (이 메서드만 트랜잭션 처리)
	@Transactional
	public void updateDatabaseInfo(SubscriptionApplicationDTO dto) {
		// 여기서부터 DB 커넥션을 잡고 처리함
		subscriptionRepository.updateSubscriptionStatus(dto);

		if ("PAID".equals(dto.getPaymentStatus())) {
			subscriptionRepository.updatePlusAmount(dto);
		}
	}

	public void projectStartCheck() {
		List<ProjectStartCheckDTO> projectstartCheckList = subscriptionRepository.selectExpiredSubscriptions();

		for (ProjectStartCheckDTO data : projectstartCheckList) {
			try {
				// 핵심: 각 프로젝트 처리를 개별 트랜잭션으로 묶은 메서드로 넘김
				self.processIndividualProject(data);
			} catch (Exception e) {
				log.error("[프로젝트 {} 처리 중 전면 롤백] 사유: {}", data.getProjectId(), e.getMessage());
			}
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void processIndividualProject(ProjectStartCheckDTO data) throws Exception {
		BigDecimal rate70 = new BigDecimal("70");
		BigDecimal rate90 = new BigDecimal("90");
		BigDecimal rate100 = new BigDecimal("100");
		Long projectId = data.getProjectId();
		BigDecimal rate = data.getSubscriptionRate();
		Long tokenId = data.getTokenId();
		Long subscriberCount = 50L;// 테스트를 위해 50명 설정
		int extensionCount = data.getExtensionCount();
		if (rate.compareTo(rate70) < 0 || subscriberCount < 49) {
			System.out.println(rate + " 프로젝트 폐기");
			projectCanceled(data);
			selectAndAllCancel(projectId);
			tokenClosed(tokenId);
		} else if (rate.compareTo(rate70) >= 0 && rate.compareTo(rate90) < 0) {
			if (extensionCount == 0) {
				System.out.println(rate + " 프로젝트 종료일 +2일");
				subscriptionRepository.updateProjectTwoDay(projectId);
				noticeEmail(projectId); // 여기에 사용자에게 이메일 보내는 것 추가하기
			} else {
				System.out.println("프로젝트 폐기");
				projectCanceled(data);
				selectAndAllCancel(projectId);
				tokenClosed(tokenId);
			}
		} else if (rate.compareTo(rate90) >= 0 && rate.compareTo(rate100) < 0) {
			// 마리팜이 충당할 가격
			BigDecimal leftAmount = data.getTargetAmount().subtract(data.getActualAmount());
			SubscriptionApplicationDTO applicationDTO = new SubscriptionApplicationDTO();
			applicationDTO.setProjectId(data.getProjectId());
			applicationDTO.setSubscriptionAmount(leftAmount);
			applicationDTO.setTokenId(tokenId);
			subscriptionApplication(applicationDTO);
			System.out.println(applicationDTO);
			postApplication(applicationDTO);
			System.out.println(rate + " 마리팜 회사가 나머지 충당");
			subscriptionRepository.updateProjectInProgress(projectId);
		} else {
			subscriptionRepository.updateProjectInProgress(projectId);
			self.selectAllocationInfo(projectId);
			System.out.println(rate + " 그대로 진행");
		}
	}

	public void projectCanceled(ProjectStartCheckDTO projectStartCheckDTO) {
		Long projectId = projectStartCheckDTO.getProjectId();
		Long tokenId = projectStartCheckDTO.getTokenId();

		// DB 업데이트 (프로젝트 상태 변경 및 토큰 삭제)
		subscriptionRepository.updateProjectCanceled(projectId);
		subscriptionRepository.updateTokenDelete(tokenId);
		System.out.println("ID: " + projectId + " 번 프로젝트 및 토큰(" + tokenId + ") 폐기 완료");
	}

	public void noticeEmail(Long projectId) {
		List<String> userEmails = subscriptionRepository.selectUserEmail(projectId);
		for (String userEmail : userEmails) {
			mailService.sendNoticeEmail(userEmail);
		}
	}

	public void selectAndAllCancel(Long projectId) throws Exception {
		List<Long> userIds = subscriptionRepository.selectSubscriberUserIds(projectId);
		for (Long userId : userIds) {
			// 청약 내역 조회
			SubscriptionHistDTO subscriptionHistDTO = subscriptionRepository.selectPaid(userId, projectId);
			if (subscriptionHistDTO == null) {
				throw new Exception("청약 내역이 존재하지 않습니다.");
			}

			// 멱등성 키 생성
			String idempotencyKey = "SUB-REJECTED-" + subscriptionHistDTO.getShId();

			// url 생성
			String url = KH_BASE_URL + "api/project/cancel/" + subscriptionHistDTO.getExternalRefId();
			RefundDTO refundDTO = null;
			try {
				// 취소 및 환불 요청
				refundDTO = externalApiUtil.callApi(url, HttpMethod.POST, subscriptionHistDTO,
					new ParameterizedTypeReference<ApiResponse<RefundDTO>>() {}, idempotencyKey);

				if (refundDTO == null) {
					throw new Exception("환불 처리에 실패했습니다.");
				}

				log.info("[Service] 증권사 청약 취소 완료");

				projectFailRefundRequest(subscriptionHistDTO, refundDTO);

			} catch (RuntimeException e) {
				// 유틸리티에서 던진 구체적인 에러 메시지("잔액 부족" 등)가 이곳으로 전달됨
				log.error("[Service] 청약 취소 실패. 재시도 큐에 등록합니다. 사유: {}", e.getMessage());

				Object[] params = new Object[] { subscriptionHistDTO.getExternalRefId() };

				ApiRetryService apiRetryService = applicationContext.getBean(ApiRetryService.class);
				apiRetryService.registerRetry(ApiType.SUB_CANCEL, subscriptionHistDTO, params, idempotencyKey);
			}
		}
	}

	// 일단 사용하지 말고 두기
	private void tokenClosed(Long tokenId) {
		String url = KH_BASE_URL + "api/project/close/" + tokenId;
		try {
			externalApiUtil.callApi(url, HttpMethod.POST, null,
				new ParameterizedTypeReference<ApiResponse<Object>>() {});

			log.info("강황 증권에 토큰 소각 완료");
		} catch (Exception e) {
			log.error("정산 실패 메시지: {}", e.getMessage());
		}
	}

	// 증권사 환불 요청 성공 이후 작업
	private void projectFailRefundRequest(SubscriptionHistDTO subscriptionHistDTO, RefundDTO refundDTO)
		throws Exception {
		refundDTO.setShId(subscriptionHistDTO.getShId());
		refundDTO.setProjectId(subscriptionHistDTO.getProjectId());
		refundDTO.setUclId(refundDTO.getWalletId());
		refundDTO.setExternalRefId(refundDTO.getTransactionId());
		refundDTO.setUserId(subscriptionHistDTO.getUserId());
		refundDTO.setRefundType("ALL"); // 환불 완료 상태
		refundDTO.setReasonCode("FAIL_UNDER_70"); // 사유
		refundDTO.setStatus("SUCCESS"); // 처리 상태

		subscriptionHistDTO.setSubscriptionStatus("CANCELED");
		subscriptionHistDTO.setPaymentStatus("REFUNDED");
		subscriptionHistDTO.setCanceledAt(OffsetDateTime.now());

		self.updateRefundAndSubsTable(refundDTO, subscriptionHistDTO);
	}

	public void selectAllocationInfo(Long inprogressProjectId) {
		System.out.println("배정 로직 진입 확인");
		AllocationTokenDTO dto = subscriptionRepository.selectAllocationInfo(inprogressProjectId);
		List<InvestorDTO> investors = dto.getInvestors();
		List<AllocationRequestDTO> requestList = new ArrayList<AllocationRequestDTO>();
		List<TokenLedgerDTO> newTokenList = new ArrayList<TokenLedgerDTO>();
		List<Long> shIdList = new ArrayList<Long>();
		Long tokenId = dto.getTokenId();
		Long projectId = dto.getProjectId();
		String lastPrevHash = tokenReopsitory.selectLastHash();
		WalletDTO adminWalletInfo = projectService.selectMyWalletInfo(1L);
		BigDecimal adminTotalBalance = adminWalletInfo.getTotalBalance();
		// [설정] KRW(원화)는 소수점 0자리, 토큰은 소수점 4자리
		final int MONEY_SCALE = 0;
		final int TOKEN_SCALE = 4;
		BigDecimal standardAmount = ((dto.getTargetAmount().divide(dto.getSubscriberCount(), MONEY_SCALE,
			RoundingMode.FLOOR)).max(dto.getMinAmountPerInvestor()));
		BigDecimal maxAmount = investors.stream().map(investor -> investor.getSubscriptionAmount())
			.max((a, b) -> a.compareTo(b)).orElse(BigDecimal.ZERO);

		BigDecimal minAmount = investors.stream().map(investor -> investor.getSubscriptionAmount())
			.min((a, b) -> a.compareTo(b)).orElse(BigDecimal.ZERO);
		System.out.println("standardAmount : " + standardAmount);
		if (minAmount.compareTo(standardAmount) >= 0) {
			// [Case 01] 모든 참여자가 '기준 금액' 이상 신청
			// 모든 참여자에게 **[기준 금액]**만큼만 동일하게 배분합니다
			System.out.println("projectId : " + projectId);
			for (InvestorDTO sendData : investors) {
				Long shId = sendData.getShId();
				Long userId = sendData.getUserId();
				BigDecimal subscriptionAmount = sendData.getSubscriptionAmount();
				BigDecimal pricePerToken = dto.getTargetAmount().divide(dto.getTotalSupply(), MONEY_SCALE,
					RoundingMode.FLOOR);
				Long uclId = subscriptionRepository.selectUclId(userId);
				BigDecimal resultTokenCount = standardAmount.divide(pricePerToken, TOKEN_SCALE, RoundingMode.FLOOR);
				BigDecimal subscriptionTokenCount = subscriptionAmount.divide(pricePerToken, TOKEN_SCALE,
					RoundingMode.FLOOR);
				// 강황 증권 api로 현재 나의 지갑 조회
				WalletDTO walletInfo = projectService.selectMyWalletInfo(uclId);
				BigDecimal myTotalBalance = walletInfo.getTotalBalance();
				System.out.println("userId : " + userId);

				AllocationRequestDTO allocationRequestDTO = AllocationRequestDTO.builder().subscriptionId(shId)
					.walletId(uclId).passPrice(pricePerToken).passVolume(resultTokenCount).build();

				requestList.add(allocationRequestDTO);

				// 환불 금액
				BigDecimal refundAmount = subscriptionAmount.subtract(standardAmount);
				System.out.println("--------------------------------------------------");
				System.out.println("신청ID: " + allocationRequestDTO.getSubscriptionId());
				System.out.println("지갑ID: " + allocationRequestDTO.getWalletId());
				System.out.println("토큰1개당 가격: " + allocationRequestDTO.getPassPrice().toPlainString());
				System.out.println("배정수량: " + allocationRequestDTO.getPassVolume().toPlainString());
				System.out.println("--------------------------------------------------");

				String timePart = String.valueOf(System.currentTimeMillis());
				String shortTime = timePart.substring(timePart.length() - 6);
				String txId = "SUB_" + projectId + "_" + shortTime;
				String newHash = projectService.createHash(lastPrevHash, tokenId, resultTokenCount);
				TokenLedgerDTO projectNewTokenDTO = TokenLedgerDTO.builder().tokenId(tokenId) // 토큰 번호
					.fromUserId(1L) // 보낸 사용자 시스템사용자
					.toUserId(sendData.getUserId()) // 사용자ID
					.transactionId(txId) // 거래 고유 식별 번호
					.externalRefId(990803L) // 증권사 참조 ID (일단 동일하게 세팅)
					.orderAmount(subscriptionTokenCount) // 주문 수량
					.contractAmount(resultTokenCount) // 체결 수량
					.status("COMPLETED") // 발행 완료 상태
					.fee(BigDecimal.ZERO) // 최초 발행 수수료 0
					.transactionType("SUBSCRIPTION") // 거래 종류: 발행
					.from_balanceAfter(adminTotalBalance) // 송금 후 잔액 시스템 관리자 잔액
					.to_balanceAfter(myTotalBalance.add(standardAmount)) // 수금 후 잔액 현재 잔액 + 기준 금액
					.prevHashValue(lastPrevHash) // 이전 해시가 없으므로 "0"
					.hashValue(newHash) // 해시 계산
					.build();
				newTokenList.add(projectNewTokenDTO);
				lastPrevHash = newHash;
			}
		} else if (minAmount.compareTo(standardAmount) < 0 && maxAmount.compareTo(standardAmount) >= 0) {

			// [Case 02] 일부만 '기준 금액' 이상 신청 (핵심 로직)
			/*
			 * 배정 로직 (2단계 배정): 1. 1차 배정: 기준 금액 미만 신청자에게는 신청액 전액을 먼저 배정합니다. 2. 물량 확보: 1차 배정 후
			 * 남은 잔여 토큰 물량을 계산합니다. 3. 2차 배정: 기준 금액 이상 신청자들에게 **[남은 물량 × 초과 신청 비율]**로 비례
			 * 배분합니다.
			 */

			System.out.println("projectId : " + projectId);
			BigDecimal targetAmount = dto.getTargetAmount(); // 프로젝트 목표 금액
			BigDecimal tokenTotalSupply = dto.getTotalSupply(); // 토큰 총 발행량
			BigDecimal pricePerToken = dto.getTargetAmount().divide(dto.getTotalSupply(), MONEY_SCALE,
				RoundingMode.FLOOR); // 토큰 1개당 가격
			BigDecimal totalExcessAmount = BigDecimal.ZERO; // 총 초과 금액
			BigDecimal remainingTokens = tokenTotalSupply; // 나머지 토큰 수
			List<InvestorDTO> highValueInvestors = new ArrayList<InvestorDTO>();
			// 1차 배정
			for (InvestorDTO sendData : investors) {
				if (sendData.getSubscriptionAmount().compareTo(standardAmount) < 0) {
					Long shId = sendData.getShId();
					Long userId = sendData.getUserId();
					BigDecimal subscriptionAmount = sendData.getSubscriptionAmount();
					Long uclId = subscriptionRepository.selectUclId(userId);
					BigDecimal resultTokenCount = subscriptionAmount.divide(pricePerToken, TOKEN_SCALE,
						RoundingMode.FLOOR);
					// 강황 증권 api로 현재 나의 지갑 조회
					WalletDTO walletInfo = projectService.selectMyWalletInfo(uclId);
					BigDecimal myTotalBalance = walletInfo.getTotalBalance();
					System.out.println("userId : " + userId);
					// 물량 확보
					targetAmount = targetAmount.subtract(subscriptionAmount); // 목표 금액 - 기준 금액 미만 신청자 금액
					remainingTokens = remainingTokens.subtract(resultTokenCount); // 토큰 총 발행량 - 기준 금액 미만 신청자 토큰
					AllocationRequestDTO allocationRequestDTO = AllocationRequestDTO.builder().subscriptionId(shId)
						.walletId(uclId).passPrice(pricePerToken).passVolume(resultTokenCount).build();

					requestList.add(allocationRequestDTO);

					System.out.println("--------------------------------------------------");
					System.out.println("신청ID: " + allocationRequestDTO.getSubscriptionId());
					System.out.println("지갑ID: " + allocationRequestDTO.getWalletId());
					System.out.println("토큰1개당 가격: " + allocationRequestDTO.getPassPrice().toPlainString());
					System.out.println("배정수량: " + allocationRequestDTO.getPassVolume().toPlainString());
					System.out.println("--------------------------------------------------");

					String timePart = String.valueOf(System.currentTimeMillis());
					String shortTime = timePart.substring(timePart.length() - 6);
					String txId = "SUB_" + projectId + "_" + shortTime;
					String newHash = projectService.createHash(lastPrevHash, tokenId, resultTokenCount);
					TokenLedgerDTO projectNewTokenDTO = TokenLedgerDTO.builder().tokenId(tokenId) // 토큰 번호
						.fromUserId(1L) // 보낸 사용자 시스템사용자
						.toUserId(sendData.getUserId()) // 사용자ID
						.transactionId(txId) // 거래 고유 식별 번호
						.externalRefId(990803L) // 증권사 참조 ID (일단 동일하게 세팅)
						.orderAmount(resultTokenCount) // 주문 수량
						.contractAmount(resultTokenCount) // 체결 수량
						.status("COMPLETED") // 발행 완료 상태
						.fee(BigDecimal.ZERO) // 최초 발행 수수료 0
						.transactionType("SUBSCRIPTION") // 거래 종류: 발행
						.from_balanceAfter(adminTotalBalance) // 송금 후 잔액 시스템 관리자 잔액
						.to_balanceAfter(myTotalBalance.add(subscriptionAmount)) // 수금 후 잔액 증권사에서 보내줌
						.prevHashValue(lastPrevHash) // 이전 해시값
						.hashValue(newHash) // 해시 계산
						.build();
					newTokenList.add(projectNewTokenDTO);
					lastPrevHash = newHash;
				} else {
					BigDecimal subscriptionAmount = sendData.getSubscriptionAmount();
					// 총 초과 금액 += 개별 신청액 - 기준 금액
					totalExcessAmount = totalExcessAmount.add(subscriptionAmount.subtract(standardAmount));
					// 남은 토큰 발행량
					/*
					 * remainingTokens = remainingTokens
					 * .subtract(standardAmount.divide(pricePerToken, TOKEN_SCALE,
					 * RoundingMode.FLOOR));
					 */
					highValueInvestors.add(sendData);
				}
			}
			// 가격 = 기준 금액 + ((남은 토큰 발행량 * (개별 초과 금액 / 총 초과 금액))) * 토큰 1개당 가격
			// 토큰 = (기준 금액 / 토큰 1개당 가격) + (남은 토큰 발행량 * (개별 초과 금액 / 총 초과 금액))

			BigDecimal totalHighBasicAmount = standardAmount.multiply(new BigDecimal(highValueInvestors.size()));
			targetAmount = targetAmount.subtract(totalHighBasicAmount); // 이제 targetAmount는 진짜 '보너스 돈 파이'가 됨
			for (InvestorDTO sendData : highValueInvestors) {
				if (sendData.getSubscriptionAmount().compareTo(standardAmount) >= 0) {
					Long shId = sendData.getShId();
					Long userId = sendData.getUserId();
					BigDecimal subscriptionAmount = sendData.getSubscriptionAmount();
					Long uclId = subscriptionRepository.selectUclId(userId);
					BigDecimal userExcessAmount = subscriptionAmount.subtract(standardAmount);
					BigDecimal subscriptionTokenCount = subscriptionAmount.divide(pricePerToken, TOKEN_SCALE,
						RoundingMode.FLOOR);
					// 강황 증권 api로 현재 나의 지갑 조회
					WalletDTO walletInfo = projectService.selectMyWalletInfo(uclId);
					BigDecimal myTotalBalance = walletInfo.getTotalBalance();
					System.out.println("userId : " + userId);
					// 추가 토큰 계산 (0으로 나누기 방어 로직 포함)
					BigDecimal extraAmount = BigDecimal.ZERO;
					// 추가 금액 계산 (보너스 파이 * 내 초과율)
					if (totalExcessAmount.compareTo(BigDecimal.ZERO) > 0) {
						extraAmount = targetAmount.multiply(userExcessAmount).divide(totalExcessAmount, MONEY_SCALE,
							RoundingMode.FLOOR);
					}

					// 최종 금액 = 기준 금액 + 추가 금액
					BigDecimal finalUserAmount = standardAmount.add(extraAmount);
					BigDecimal resultTokenCount = (finalUserAmount.divide(pricePerToken, TOKEN_SCALE,
						RoundingMode.FLOOR));
					AllocationRequestDTO allocationRequestDTO = AllocationRequestDTO.builder().subscriptionId(shId)
						.walletId(uclId).passPrice(pricePerToken).passVolume(resultTokenCount).build();

					requestList.add(allocationRequestDTO);

					System.out.println("--------------------------------------------------");
					System.out.println("신청ID: " + allocationRequestDTO.getSubscriptionId());
					System.out.println("지갑ID: " + allocationRequestDTO.getWalletId());
					System.out.println("토큰1개당 가격: " + allocationRequestDTO.getPassPrice().toPlainString());
					System.out.println("배정수량: " + allocationRequestDTO.getPassVolume().toPlainString());
					System.out.println("기준배정: " + standardAmount.divide(pricePerToken));
					System.out.println("추가배정: " + resultTokenCount);
					System.out.println("--------------------------------------------------");

					String timePart = String.valueOf(System.currentTimeMillis());
					String shortTime = timePart.substring(timePart.length() - 6);
					String txId = "SUB_" + projectId + "_" + shortTime;
					String newHash = projectService.createHash(lastPrevHash, tokenId, resultTokenCount);
					TokenLedgerDTO projectNewTokenDTO = TokenLedgerDTO.builder().tokenId(tokenId) // 토큰 번호
						.fromUserId(1L) // [요구사항 1-3] 보낸 사용자 null
						.toUserId(sendData.getUserId()) // [요구사항 1-2] 시스템 관리자(1)에게 배정
						.transactionId(txId) // 거래 고유 식별 번호
						.externalRefId(990803L) // 증권사 참조 ID (일단 동일하게 세팅)
						.orderAmount(subscriptionTokenCount) // 주문 수량
						.contractAmount(resultTokenCount) // 체결 수량
						.status("COMPLETED") // 발행 완료 상태
						.fee(BigDecimal.ZERO) // 최초 발행 수수료 0
						.transactionType("SUBSCRIPTION") // 거래 종류: 발행
						.from_balanceAfter(adminTotalBalance) // 송금 후 잔액 시스템 관리자 잔액
						.to_balanceAfter(myTotalBalance.add(finalUserAmount)) // 수금 후 잔액 증권사에서 보내줌
						.prevHashValue(lastPrevHash) // 이전 해시가 없으므로 "0"
						.hashValue(newHash) // 해시 계산
						.build();
					newTokenList.add(projectNewTokenDTO);
					lastPrevHash = newHash;
				}
			}
		} else {
			// [Case 03] 모든 참여자가 '기준 금액' 미만 신청
			// 모든 참여자에게 **신청액 전액(100%)**을 배정합니다.
			System.out.println("projectId : " + projectId);
			for (InvestorDTO sendData : investors) {
				Long shId = sendData.getShId();
				Long userId = sendData.getUserId();
				BigDecimal subscriptionAmount = sendData.getSubscriptionAmount();
				BigDecimal pricePerToken = dto.getTargetAmount().divide(dto.getTotalSupply(), MONEY_SCALE,
					RoundingMode.FLOOR);
				Long uclId = subscriptionRepository.selectUclId(userId);
				BigDecimal resultTokenCount = subscriptionAmount.divide(pricePerToken, TOKEN_SCALE, RoundingMode.FLOOR);
				// 강황 증권 api로 현재 나의 지갑 조회
				WalletDTO walletInfo = projectService.selectMyWalletInfo(uclId);
				BigDecimal myTotalBalance = walletInfo.getTotalBalance();
				System.out.println("userId : " + userId);

				AllocationRequestDTO allocationRequestDTO = AllocationRequestDTO.builder().subscriptionId(shId)
					.walletId(uclId).passPrice(pricePerToken).passVolume(resultTokenCount).build();

				requestList.add(allocationRequestDTO);

				System.out.println("--------------------------------------------------");
				System.out.println("신청ID: " + allocationRequestDTO.getSubscriptionId());
				System.out.println("지갑ID: " + allocationRequestDTO.getWalletId());
				System.out.println("토큰1개당 가격: " + allocationRequestDTO.getPassPrice().toPlainString());
				System.out.println("배정수량: " + allocationRequestDTO.getPassVolume().toPlainString());
				System.out.println("--------------------------------------------------");

				String timePart = String.valueOf(System.currentTimeMillis());
				String shortTime = timePart.substring(timePart.length() - 6);
				String txId = "SUB_" + projectId + "_" + shortTime;
				String newHash = projectService.createHash(lastPrevHash, tokenId, resultTokenCount);
				TokenLedgerDTO projectNewTokenDTO = TokenLedgerDTO.builder().tokenId(tokenId) // 토큰 번호
					.fromUserId(1L) // [요구사항 1-3] 보낸 사용자 null
					.toUserId(sendData.getUserId()) // [요구사항 1-2] 시스템 관리자(1)에게 배정
					.transactionId(txId) // 거래 고유 식별 번호
					.externalRefId(990803L) // 증권사 참조 ID (일단 동일하게 세팅)
					.orderAmount(resultTokenCount) // 주문 수량
					.contractAmount(resultTokenCount) // 체결 수량
					.status("COMPLETED") // 발행 완료 상태
					.fee(BigDecimal.ZERO) // 최초 발행 수수료 0
					.transactionType("SUBSCRIPTION") // 거래 종류: 발행
					.from_balanceAfter(adminTotalBalance) // 송금 후 잔액 시스템 관리자 잔액
					.to_balanceAfter(myTotalBalance.add(subscriptionAmount)) // 수금 후 잔액 증권사에서 보내줌
					.prevHashValue(lastPrevHash) // 이전 해시가 없으므로 "0"
					.hashValue(newHash) // 해시 계산
					.build();
				newTokenList.add(projectNewTokenDTO);
				lastPrevHash = newHash;
			}
		}
		log.info("증권사 전송 데이터 확인: {}", requestList);
		List<AllocationResultDTO> apiResults = resultAllocation(tokenId, requestList);
		tokenService.insertTokenLedger(newTokenList);

		// 2. 1대1 매칭을 위한 Map 생성 (Key: uclId / Value: InvestorDTO)
		// 루프 밖에서 한 번만 생성해서 속도를 높입니다.
		Map<Long, InvestorDTO> investorMap = investors.stream()
			.collect(Collectors.toMap(inv -> subscriptionRepository.selectUclId(inv.getUserId()), // 우리 쪽 지갑 ID
				inv -> inv, (existing, replacement) -> existing // 혹시 모를 중복 방어
			));

		List<RefundDTO> refundList = new ArrayList<>();

		// 3. API 응답(payload)을 기준으로 1대1 매칭 시작
		for (AllocationResultDTO res : apiResults) {
			// API가 준 walletId로 우리 쪽 투자자 정보를 찾음
			InvestorDTO investor = investorMap.get(res.getWalletId());

			if (investor != null) {
				// [환불 계산] 신청 금액 - 증권사가 확정한 실제 배정 금액(passAmount)
				BigDecimal refundAmount = investor.getSubscriptionAmount().subtract(res.getPassAmount());

				if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
					RefundDTO refund = RefundDTO.builder().userId(investor.getUserId()).projectId(projectId) // 상단에서 선언한
						.shId(investor.getShId()).uclId(res.getWalletId()) // 증권사 지갑 ID
						.amount(refundAmount).refundType("PARTIAL") // 일부 배정 후 남은 금액이므로 PARTIAL
						.reasonCode("PRO_RATA_RESIDUE") // 비례 배분 후 잔여금 사유
						.status("SUCCESS") // 이제 막 생성했으니 대기 상태
						.externalRefId(res.getPassTxId()) // 증권사가 준 트랜잭션 ID 기록
						.build();
					refundList.add(refund);
				}

				// 4. 청약 상태 업데이트 (APPROVED)
				subscriptionRepository.approveSubscription(investor.getShId());
			} else {
				log.warn("API 응답의 walletId {} 에 해당하는 투자자를 찾을 수 없습니다.", res.getWalletId());
			}
		}
		if (!refundList.isEmpty()) {
			refundService.insertRefunds(refundList);
			log.info("환불 데이터 생성 완료: {}건", refundList.size());
		}
	}

	// 강황증권에 토큰 배정 보내기
	public List<AllocationResultDTO> resultAllocation(Long tokenId, List<AllocationRequestDTO> allocationTokenDTO) {
		String url = KH_BASE_URL + "api/project/result/" + tokenId;
		try {
			List<AllocationResultDTO> response = externalApiUtil.callApi(url, HttpMethod.POST, allocationTokenDTO,
				new ParameterizedTypeReference<ApiResponse<List<AllocationResultDTO>>>() {});
			log.info("배정 완료 데이터 전송 성공: tokenId={}", tokenId);
			return response;
		} catch (Exception e) {
			log.error("배정 데이터 전송 중 오류 발생: {}", e.getMessage());
			throw e;
		}
	}
}
