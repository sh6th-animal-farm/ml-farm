package com.animalfarm.mlf.domain.project;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animalfarm.mlf.common.security.SecurityUtil;
import com.animalfarm.mlf.domain.project.dto.FarmDTO;
import com.animalfarm.mlf.domain.accounting.DividendService;
import com.animalfarm.mlf.domain.accounting.dto.DividendSelectDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectDetailDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectInsertDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectListDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectPictureDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectSearchReqDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectStarredDTO;
import com.animalfarm.mlf.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/project")
public class ProjectController {
	
	private final ProjectService projectService;
	private final FarmService farmService;
	@Autowired
	DividendService dividendService;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		// 억지로 객체 만들지 말고, 들어오는 문자열 그대로 처리하라고 스프링한테 명령
		binder.registerCustomEditor(java.time.OffsetDateTime.class, new java.beans.PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				// "2026-01-20T20:35" -> "2026-01-20T20:35:00+09:00" 강제 변환
				setValue(
					java.time.LocalDateTime.parse(text).atZone(java.time.ZoneId.of("Asia/Seoul")).toOffsetDateTime());
			}
		});
	}

	@GetMapping("/{projectId}")
	public ProjectDetailDTO selectDetail(@PathVariable("projectId")
	Long projectId) {
		return projectService.selectDetail(projectId);
	}

	@GetMapping("/all")
	public List<ProjectDTO> selectAll() {
		return projectService.selectAll();
	}

	@GetMapping("/")
	public List<ProjectListDTO> selectByCondition(@RequestBody
	ProjectSearchReqDTO searchDTO) {
		Long userId = null;
		try {
			userId = SecurityUtil.getCurrentUserId();
		} catch(Exception e) {
			userId = -1L;
		}
		searchDTO.setUserId(userId);
		return projectService.selectByCondition(searchDTO);
	}

	//관심 프로젝트인지 조회
	@GetMapping("/starred")
	public boolean getStarredStatus(@RequestBody
	ProjectStarredDTO projectStarredDTO) {
		return projectService.getStarredStatus(projectStarredDTO);
	}

	//관심 프로젝트 신규 등록
	@PostMapping("/starred")
	public Boolean upsertStrarredProject(@RequestBody
	ProjectStarredDTO projectStarredDTO) {
		Boolean curStatus = null;
		if (projectService.upsertStrarredProject(projectStarredDTO)) {
			curStatus = projectService.getStarredStatus(projectStarredDTO);
		}
		return curStatus;
	}

	@PostMapping("/insert")
	public ResponseEntity<String> insertProject(@RequestBody
	ProjectInsertDTO projectInsertDTO) {
		if (projectService.insertProject(projectInsertDTO)) {
			try {
				projectService.postTokenIssue(projectInsertDTO);
				return ResponseEntity.ok("success");
			} catch (Exception e) {
				log.error("증권사 전송 중 오류 발생: {}", e.getMessage());
				return ResponseEntity.ok("api_fail");
			}
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("프로젝트 등록 중 서버 오류가 발생했습니다.");
		}

	}

	@PostMapping("/update")
	public ResponseEntity<String> updateProject(@RequestBody
	ProjectDTO projectDTO) {
		if (projectService.updateProject(projectDTO)) {
			return ResponseEntity.ok("success");
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업데이트 중 서버 오류가 발생했습니다.");
		}
	}

	@GetMapping("/picture/{projectId}/all")
	public List<ProjectPictureDTO> selectPictures(@PathVariable("projectId")
	Long projectId) {
		return projectService.selectPictures(projectId);
	}

	@GetMapping("/checkAccount")
	public boolean checkAccount(Long userId) {
		return projectService.checkAccount();
	}
	
	@GetMapping("/farm/all")
	public List<FarmDTO> selectAllFarm() {
		return farmService.selectAllFarm();
	}

	@PostMapping("/dividend")
	public ResponseEntity<String> processDividend(@RequestBody
	Long projectId) {
		try {
			dividendService.runDividendBatch(projectId);
			return ResponseEntity.ok("성공했습니다. DB를 확인해주세요.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@PostMapping("/dividend-mail")
	public ResponseEntity<String> sendDividendEmail() {
		try {
			dividendService.sendEmail();
			return ResponseEntity.ok("성공했습니다. DB를 확인해주세요.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@PostMapping("/dividend/poll/select")
	public ResponseEntity<String> selectDividendType(@RequestBody
	DividendSelectDTO dividendSelectDTO) {
		Long dividendId = dividendSelectDTO.getDividendId();
		String dividendType = dividendSelectDTO.getDividendType();
		String address = dividendSelectDTO.getAddress();
		try {
			dividendService.processUserSelection(dividendId, dividendType, address);
			// 성공 시 성공 메시지 반환
			return ResponseEntity.ok("수령 방식 선택이 완료되었습니다.");
		} catch (Exception e) {
			// 실패 시 에러 메시지와 함께 400 또는 500 에러 반환
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

}
