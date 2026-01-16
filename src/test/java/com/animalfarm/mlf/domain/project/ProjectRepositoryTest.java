package com.animalfarm.mlf.domain.project;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.animalfarm.mlf.domain.project.dto.ProjectDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectDetailDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectInsertDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectListDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectSearchReqDTO;
import static com.animalfarm.mlf.common.ProjectTestFixture.createBaseProjectDTO;

@ExtendWith(SpringExtension.class) // Spring 컨테이너를 띄워 Bean 주입을 받음
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/context-datasource.xml"}) // DB 설정 파일 경로
@Transactional // 테스트 후 DB를 자동으로 롤백하여 데이터 오염 방지
class ProjectRepositoryTest {

	@Autowired
	private ProjectRepository projectRepository;

	@Test
	@DisplayName("전체 프로젝트 목록 조회 및 찜 여부 확인 테스트")
	void selectByCondition_WithStarredTest() {
		// Given: 검색 조건 설정
		ProjectSearchReqDTO searchDTO = new ProjectSearchReqDTO();
		searchDTO.setUserId(2L); // 테스트용 DB에 존재하는 유저 ID 가정
		searchDTO.setKeyword(""); // 전체 조회

		// When: 레포지토리 메서드 호출
		List<ProjectListDTO> result = projectRepository.selectByCondition(searchDTO);

		// Then: 결과 검증
		assertNotNull(result, "결과 리스트는 null이 아니어야 합니다.");

		// 데이터가 있다는 가정하에 로그 출력 및 검증
		if (!result.isEmpty()) {
			ProjectListDTO firstProject = result.get(0);

			// 필수 값들이 잘 매핑되었는지 확인
			assertNotNull(firstProject.getProjectName());
			assertNotNull(firstProject.getThumbnailUrl());
		}
	}

	@Test
	@DisplayName("키워드 검색 시 필터링이 정상적으로 작동하는지 테스트")
	void selectByCondition_KeywordSearchTest() {
		// Given
		ProjectSearchReqDTO searchDTO = new ProjectSearchReqDTO();
		String keyword = "스마트";
		searchDTO.setKeyword(keyword);

		// When
		List<ProjectListDTO> result = projectRepository.selectByCondition(searchDTO);

		// Then
		for (ProjectListDTO project : result) {
			assertTrue(project.getProjectName().contains(keyword),
				"모든 결과는 '" + keyword + "' 키워드를 포함해야 합니다.");
		}
	}

	@Test
	@DisplayName("프로젝트 상세 조회 테스트")
	public void selectDetail_ProjectId() {
		// 1. Given
		Long projectId = 1L;

		// 2. 주입 확인 (여기서 실패하면 스프링 설정 문제)
		assertNotNull(projectRepository, "Repository가 주입되지 않았습니다.");

		// 3. When
		ProjectDetailDTO result = projectRepository.selectDetail(projectId);

		// 4. Then
		assertNotNull(result, "조회 결과가 null입니다.");
	}
	
	@Test
	@DisplayName("프로젝트 및 토큰 정보 등록 테스트")
	void insertProject_SuccessTest() {
	    // Given
	    ProjectInsertDTO insertDTO = createBaseProjectDTO();
	    // When
	    projectRepository.insertProject(insertDTO);
	    
	    // Then
	    assertNotNull(insertDTO.getProjectId(), "등록 후 projectId가 생성되어야 합니다.");
	    ProjectDetailDTO savedProject = projectRepository.selectDetail(insertDTO.getProjectId());
	    // When & Then: 예외 없이 실행되는지 확인
	    assertDoesNotThrow(() -> {
	        projectRepository.insertToken(insertDTO);
	    }, "프로젝트와 토큰 등록 시 예외가 발생하지 않아야 합니다.");
	    
	    assertAll(
    	    () -> assertNotNull(savedProject, "저장된 프로젝트를 불러올 수 있어야 합니다."),
    	    () -> assertEquals("다금바리 멜론 프로젝트", savedProject.getProjectName()),
    	    () -> assertEquals(new BigDecimal("10.00"), savedProject.getExpectedReturn()), // 소수점 자리수 주의
    	    () -> assertEquals(3, savedProject.getManagerCount()),
    	    () -> assertNotNull(savedProject.getAnnouncementStartDate(), "공고 시작일이 저장되어야 합니다.")
    	);
	}
	
	@Test
	@DisplayName("필수값이 누락된 프로젝트 등록 시 실패 테스트")
	void insertProject_Fail_RequiredFieldMissing() {
	    // 1. Given (준비): 필수값인 프로젝트 이름(projectName)을 null로 설정
	    ProjectInsertDTO failDTO = createBaseProjectDTO();
	    failDTO.setProjectName(null); 

	    // 2. When & Then (실행 및 검증): 예외가 발생하는지 확인
	    // assertThrows는 특정 예외가 발생해야 성공하는 테스트입니다.
	    assertThrows(Exception.class, () -> {
	        projectRepository.insertProject(failDTO);
	    }, "프로젝트 이름이 null이면 DB 제약 조건 위반으로 예외가 발생해야 합니다.");
	}

	@Test
	@DisplayName("프로젝트 기본 정보 수정 테스트")
	void updateProject_SuccessTest() {
	    // Given: 기존 1번 프로젝트가 있다고 가정
	    ProjectDTO updateDTO = new ProjectDTO();
	    updateDTO.setProjectId(1L);
	    updateDTO.setProjectName("수정된 프로젝트 이름");
	    updateDTO.setTargetAmount(new BigDecimal("20000000"));

	    // When
	    projectRepository.updateProject(updateDTO);
	    ProjectDetailDTO result = projectRepository.selectDetail(1L);

	    // Then
	    assertEquals("수정된 프로젝트 이름", result.getProjectName(), "이름이 수정되어야 합니다.");
	}
	
	@Test
	@DisplayName("존재하지 않는 프로젝트 ID 수정 시 데이터 불변 테스트")
	void updateProject_Fail_InvalidId() {
	    // 1. Given: 절대 존재할 수 없는 ID 설정
	    Long invalidId = -999L;
	    ProjectDTO updateDTO = new ProjectDTO();
	    updateDTO.setProjectId(invalidId);
	    updateDTO.setProjectName("유령 프로젝트");

	    // 2. When: 수정 실행
	    projectRepository.updateProject(updateDTO);
	    
	    // 3. Then: 해당 ID로 조회했을 때 데이터가 없어야 함(null)
	    ProjectDetailDTO result = projectRepository.selectDetail(invalidId);
	    assertNull(result, "존재하지 않는 ID를 조회하면 결과가 null이어야 합니다.");
	}
	
	
}