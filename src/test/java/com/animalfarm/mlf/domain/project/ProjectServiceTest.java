package com.animalfarm.mlf.domain.project;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.animalfarm.mlf.domain.project.dto.ProjectDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectInsertDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectListDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectSearchReqDTO;
import static com.animalfarm.mlf.common.ProjectTestFixture.createBaseProjectDTO;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

	@Mock
	private ProjectRepository projectRepository; // 가짜 객체 생성

	@InjectMocks
	private ProjectService projectService; // 가짜 객체를 주입받은 실제 서비스 객체

	@Test
	@DisplayName("프로젝트 조회/검색 테스트")
	void selectByConditionTest() {
		// 1. Given (준비)
		// 리포지토리가 반환할 가짜 데이터 리스트를 만듭니다.
		List<ProjectListDTO> mockReturnList = new ArrayList<>();
		mockReturnList.add(new ProjectListDTO()); // 내용물은 중요하지 않음 (개수 확인용)
		mockReturnList.add(new ProjectListDTO());

		ProjectSearchReqDTO searchDTO = new ProjectSearchReqDTO();
		searchDTO.setKeyword("농장");

		// mock 리포지토리의 동작 정의: 어떤 searchDTO가 들어오든 mockReturnList를 반환해라!
		when(projectRepository.selectByCondition(any(ProjectSearchReqDTO.class)))
			.thenReturn(mockReturnList);

		// 2. When (실행)
		List<ProjectListDTO> result = projectService.selectByCondition(searchDTO);

		// 3. Then (검증)
		assertNotNull(result); // 결과가 null이 아님을 확인
		assertEquals(2, result.size()); // 가짜 데이터 개수와 일치하는지 확인

		// 실제로 리포지토리의 메서드가 정확히 1번 호출되었는지 확인 (중요!)
		verify(projectRepository, times(1)).selectByCondition(searchDTO);
	}
	
	@Test
	@DisplayName("프로젝트 등록 시 청약률 계산 및 저장 로직 검증")
	void insertProject_LogicTest() {
	    // Given
	    ProjectInsertDTO insertDTO = createBaseProjectDTO();

	    // When
	    boolean result = projectService.insertProject(insertDTO);

	    // Then
	    assertTrue(result);
	    // 비즈니스 로직 검증: 500만/1000만 = 50.00%
	    assertEquals(new BigDecimal("50.0000"), insertDTO.getSubscriptionRate(), "청약률이 정확히 계산되어야 합니다.");
	    
	    // Repository 메서드들이 순서대로 호출되었는지 확인
	    verify(projectRepository, times(1)).insertProject(insertDTO);
	    verify(projectRepository, times(1)).insertPictureList(anyList());
	    verify(projectRepository, times(1)).insertToken(insertDTO);
	}

	@Test
	@DisplayName("프로젝트 수정 및 사진 삭제 로직 검증")
	void updateProject_WithDeletionsTest() {
	    // Given
	    ProjectDTO updateDTO = new ProjectDTO();
	    updateDTO.setProjectId(1L);
	    updateDTO.setProjectImageNames(List.of("new_img.png"));
	    updateDTO.setDeletedPictureIds(List.of(10L, 11L)); // 삭제할 사진 ID

	    // When
	    boolean result = projectService.updateProject(updateDTO);

	    // Then
	    assertTrue(result);
	    verify(projectRepository).updateProject(updateDTO);
	    verify(projectRepository).insertPictureList(anyList()); // 새 사진 등록 호출 확인
	    verify(projectRepository).deletePictureList(updateDTO.getDeletedPictureIds()); // 삭제 호출 확인
	}

}
