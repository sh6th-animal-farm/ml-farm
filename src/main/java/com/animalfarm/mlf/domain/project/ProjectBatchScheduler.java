package com.animalfarm.mlf.domain.project;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.animalfarm.mlf.domain.project.dto.ProjectStatusDTO;

@Component
public class ProjectBatchScheduler {
	@Autowired
	ProjectService projectService;

	@Scheduled(cron = "0 * * * * *")
	public void runBatch() {
		List<ProjectStatusDTO> status = projectService.selectStatus();
	}
}
