package com.animalfarm.mlf.domain.project;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.animalfarm.mlf.domain.subscription.SubscriptionService;

@Component
public class ProjectBatchScheduler {
	@Autowired
	ProjectService projectService;

	@Autowired
	SubscriptionService subscriptionService;

	@Scheduled(cron = "0 * * * * *")
	public void runBatch() {
		subscriptionService.projectStartCheck();
	}
}
