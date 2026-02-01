package com.animalfarm.mlf.domain.project;

import org.springframework.stereotype.Service;

import com.animalfarm.mlf.batch.DividendBatchService;
import com.animalfarm.mlf.domain.project.dto.ProjectStatusDTO;
import com.animalfarm.mlf.domain.token.TokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectClosingService {
    private final DividendBatchService dividendBatchService;
    private final ProjectRepository projectRepository;
    private final TokenRepository tokenRepository;
    
    // 배당 수행 (배당금 계산 + 이메일 보내기)
    public void processFinalDividends(Long projectId) throws Exception {
    	dividendBatchService.runDividendBatch(projectId);
        dividendBatchService.runEmailBatch();
    }

    // 프로젝트 상태 COMPLETED, 토큰 소각일 업데이트
	public void updateProjectAndTokenStatus(Long projectId, Long tokenId) {
		ProjectStatusDTO projectStatus = ProjectStatusDTO.builder()
				.projectId(projectId)
				.projectStatus("INPROGRESS")
				.nextStatus("COMPLETED")
				.build();
		projectRepository.updateProjectStatus(projectStatus);
		tokenRepository.updateDeletedAt(tokenId);
	}

}