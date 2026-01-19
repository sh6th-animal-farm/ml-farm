package com.animalfarm.mlf.common;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import com.animalfarm.mlf.domain.project.dto.ProjectInsertDTO;

public class ProjectTestFixture {
	
	// 테스트에서 공통으로 쓰이는 ProjectInsertDTO 빌더를 분리
	public static ProjectInsertDTO createBaseProjectDTO() {
	 return ProjectInsertDTO.builder()
	     .farmId(1L)
	     .projectName("다금바리 멜론 프로젝트")
		 .targetAmount(new BigDecimal("10000000"))
		 .actualAmount(new BigDecimal("5000000"))
		 .subscriptionRate(BigDecimal.ZERO)
		 .projectRound(1)
		 .minAmountPerInvestor(new BigDecimal("4000"))
		 .announcementStartDate(OffsetDateTime.now())
		 .announcementEndDate(OffsetDateTime.now().plusDays(7))
		 .subscriptionStartDate(OffsetDateTime.now().plusDays(8))
		 .subscriptionEndDate(OffsetDateTime.now().plusDays(10))
		 .resultAnnouncementDate(OffsetDateTime.now().plusDays(11))
		 .projectStartDate(OffsetDateTime.now().plusDays(12))
		 .projectEndDate(OffsetDateTime.now().plusYears(1))
		 .expectedReturn(new BigDecimal("10.00"))
		 .managerCount(3)
		 .totalSupply(new BigDecimal("45000"))
		 .projectImageNames(List.of("melon1.png"))
		 .tokenName("MELON-TOKEN")
		 .tickerSymbol("MELN")
		 .build();
	}
}