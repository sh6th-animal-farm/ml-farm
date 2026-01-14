package com.animalfarm.mlf.domain.project;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.animalfarm.mlf.domain.project.dto.ProjectListDTO;
import com.animalfarm.mlf.domain.project.dto.ProjectSearchReqDTO;

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
            System.out.println("프로젝트명: " + firstProject.getProjectName());
            System.out.println("찜 여부(isStarred): " + firstProject.getIsStarred());
            System.out.println("썸네일 URL: " + firstProject.getThumbnailUrl());
            
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
}