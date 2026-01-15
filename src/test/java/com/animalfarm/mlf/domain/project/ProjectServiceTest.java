package com.animalfarm.mlf.domain.project;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.animalfarm.mlf.domain.project.dto.ProjectListDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectSearchReqDTO;

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

}
