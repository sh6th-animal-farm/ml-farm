package com.animalfarm.mlf.domain.project;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.animalfarm.mlf.common.http.ApiResponse;
import com.animalfarm.mlf.domain.project.dto.FarmDTO;
import com.animalfarm.mlf.domain.project.dto.ImgEditable;
import com.animalfarm.mlf.domain.project.dto.ProjectDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectDetailDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectInsertDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectListDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectNewTokenDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectPictureDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectSearchReqDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectStarredDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectStatusDTO;
import com.animalfarm.mlf.domain.token.TokenRepository;

@Service
public class ProjectService {
	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	TokenRepository tokenReopsitory;

	// 강황증권 API 서버 주소
	@Value("${api.kh-stock.url}")
	private String khUrl;

	@Autowired
	private RestTemplate restTemplate;

	public List<ProjectDTO> selectAll() {
		return projectRepository.selectAll();
	}

	public ProjectDetailDTO selectDetail(Long projectId) {
		return projectRepository.selectDetail(projectId);
	}

	public List<ProjectListDTO> selectByCondition(ProjectSearchReqDTO searchDTO) {
		return projectRepository.selectByCondition(searchDTO);
	}

	public boolean getStarredStatus(ProjectStarredDTO projectStarredDTO) {
		return projectRepository.getStarredStatus(projectStarredDTO);
	}

	//select하여 있다면 관심 프로젝트 등록/해제 없다면 관심 프로젝트 신규 생성
	public boolean upsertStrarredProject(ProjectStarredDTO projectStarredDTO) {
		try {
			boolean isExist = projectRepository.selectStarredProject(projectStarredDTO);
			if (isExist) {
				projectRepository.updateStarred(projectStarredDTO);
				return true;
			} else {
				projectRepository.insertStrarredProject(projectStarredDTO);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional(rollbackFor = Exception.class) // 모든 예외에 대해 롤백 보장
	public boolean insertProject(ProjectInsertDTO projectInsertDTO) {
		try {
			BigDecimal subscriptionRate = projectInsertDTO.getActualAmount()
				.divide(projectInsertDTO.getTargetAmount(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
			projectInsertDTO.setSubscriptionRate(subscriptionRate);
			projectRepository.insertProject(projectInsertDTO);
			if (projectInsertDTO.getProjectImageNames() != null && !projectInsertDTO.getProjectImageNames().isEmpty()) {
				projectRepository.insertPictureList(extractPictureDTO(projectInsertDTO));
			}
			projectRepository.insertToken(projectInsertDTO);

			//토큰 원장에 넣기

			// 1. 거래 고유 식별 번호 생성 (예: ISS_프로젝트ID_타임스탬프)
			Long tokenId = projectInsertDTO.getTokenId();
			Long projectId = projectInsertDTO.getProjectId();
			BigDecimal totalSupply = projectInsertDTO.getTotalSupply();

			String timePart = String.valueOf(System.currentTimeMillis());
			String shortTime = timePart.substring(timePart.length() - 6);
			String txId = "ISS_" + projectId + "_" + shortTime;

			ProjectNewTokenDTO projectNewTokenDTO = ProjectNewTokenDTO.builder()
				.tokenId(tokenId) // 토큰 번호
				.fromUserId(null) // [요구사항 1-3] 보낸 사용자 null
				.toUserId(1L) // [요구사항 1-2] 시스템 관리자(1)에게 배정
				.transactionId(txId) // 거래 고유 식별 번호
				.externalRefId(txId) // 증권사 참조 ID (일단 동일하게 세팅)
				.orderAmount(totalSupply) // [요구사항 1-1] 전체 토큰 지분
				.contractAmount(totalSupply) // 발행 시 체결 수량은 전체 수량과 동일
				.status("COMPLETED") // 발행 완료 상태
				.fee(BigDecimal.ZERO) // 최초 발행 수수료 0
				.transactionType("ISSUE") // 거래 종류: 발행
				.from_balanceAfter(BigDecimal.ZERO) // 송금 후 잔액 변동 없음 변경 필수
				.to_balanceAfter(totalSupply) // 수금 후 잔액 변동 없음 변경 필수
				.prevHashValue("0") // 이전 해시가 없으므로 "0"
				.hashValue(createHash("0", tokenId, totalSupply)) // 해시 계산
				.build();

			tokenReopsitory.insertTokenLedger(projectNewTokenDTO);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("프로젝트 등록 중 오류 발생: " + e.getMessage(), e);
		}
	}

	// 간단한 해시 계산 예시 메서드
	private String createHash(String prevHash, Long projectId, BigDecimal amount) {
		return org.springframework.util.DigestUtils.md5DigestAsHex(
			(prevHash + projectId + amount.toString()).getBytes());
	}

	public List<FarmDTO> selectAllFarm() {
		return projectRepository.selectAllFarm();
	}

	@Transactional(rollbackFor = Exception.class) // 모든 예외에 대해 롤백 보장
	public boolean updateProject(ProjectDTO projectDTO) {
		try {
			projectRepository.updateProject(projectDTO);
			if (projectDTO.getProjectImageNames() != null && !projectDTO.getProjectImageNames().isEmpty()) {
				projectRepository.insertPictureList(extractPictureDTO(projectDTO));

			}
			if (projectDTO.getDeletedPictureIds() != null && !projectDTO.getDeletedPictureIds().isEmpty()) {
				projectRepository.deletePictureList(projectDTO.getDeletedPictureIds());
			}
			return true; // 성공 시 true 반환
		} catch (DataAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("프로젝트 수정 중 오류 발생: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("프로젝트 수정 중 오류 발생: " + e.getMessage());
		}
	}

	public List<ProjectPictureDTO> selectPictures(Long projectId) {
		return projectRepository.selectPictures(projectId);
	}

	public List<ProjectPictureDTO> extractPictureDTO(ImgEditable imgEditableDTO) {
		List<ProjectPictureDTO> newPictureDTOList = new ArrayList<>();
		for (String projectName : imgEditableDTO.getProjectImageNames()) {
			ProjectPictureDTO newPicture = new ProjectPictureDTO();
			newPicture.setProjectId(imgEditableDTO.getProjectId());
			newPicture.setImageUrl(projectName);
			newPictureDTOList.add(newPicture);
		}
		return newPictureDTOList;
	}

	public List<ProjectStatusDTO> selectStatus() {
		List<ProjectStatusDTO> dto = projectRepository.selectStatus();
		for (ProjectStatusDTO projectStatusDTO : dto) {
			String current = projectStatusDTO.getProjectStatus();
			String next = "";

			switch (current) {
				case "PREPARING": {
					next = "ANNOUNCEMENT";
					projectStatusDTO.setNextStatus(next);
					break;
				}
				case "ANNOUNCEMENT": {
					next = "SUBSCRIPTION";
					projectStatusDTO.setNextStatus(next);
					break;
				}
				case "SUBSCRIPTION": {
					next = "SUBSCRIPTION";
					projectStatusDTO.setNextStatus(next);
					break;
				}
			}
			projectRepository.updateProjectStatus(projectStatusDTO);
		}
		return projectRepository.selectStatus();
	}

	public boolean checkAccount() {
		// 1. 목적지 주소 생성 (외부 IP + 상세 경로)
		String targetUrl = khUrl + "api/my/account/1";
		try {
			// 2. GET 방식으로 데이터 요청 (응답은 String으로 받는 예시)
			ResponseEntity<ApiResponse> responseEntity = restTemplate.getForEntity(targetUrl, ApiResponse.class);
			int status = responseEntity.getStatusCodeValue();
			System.out.println("응답 결과: " + status);
			if (status == 200) {
				ApiResponse response = responseEntity.getBody();
				System.out.println("response: " + response.getMessage());
				if (response.getPayload() != null) {
					return true;
				}
			}
			System.out.println("연동되어 있지 않습니다.");
			return false;
		} catch (Exception e) {
			// 3. 외부 서버 연결 실패 시 예외 처리 (재시도 테이블 insert 등)
			System.err.println("외부 서버 통신 실패: " + e.getMessage());
			return false;
		}
	}

	public Double selectMyWallet() {
		// 1. 목적지 주소 생성 (외부 IP + 상세 경로)
		String targetUrl = khUrl + "api/my/wallet/1";
		try {
			// 2. GET 방식으로 데이터 요청 (응답은 String으로 받는 예시)
			ResponseEntity<ApiResponse> responseEntity = restTemplate.getForEntity(targetUrl, ApiResponse.class);
			int status = responseEntity.getStatusCodeValue();
			System.out.println("응답 결과: " + status);
			if (status == 200) {
				ApiResponse response = responseEntity.getBody();
				System.out.println("response : " + response.getPayload());
				List<Map<String, Object>> list = (List<Map<String, Object>>)response.getPayload();
				System.out.println("list : " + list);
				if (response.getPayload() != null) {
					System.out.println(response.getMessage());
					double result = (double)list.get(0).get("cashBalance");
					System.out.println(list.get(0).get("cashBalance"));
					return result;
				} else {
					System.out.println(response.getMessage());
				}
			}
			return 0.0;
		} catch (Exception e) {
			// 3. 외부 서버 연결 실패 시 예외 처리 (재시도 테이블 insert 등)
			System.err.println("외부 서버 통신 실패: " + e.getMessage());
			return 0.0;
		}
	}
}
