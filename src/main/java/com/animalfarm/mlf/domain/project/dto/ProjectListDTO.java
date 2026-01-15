package com.animalfarm.mlf.domain.project.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectListDTO {
	private Long projectId;
	private Long farmId;
	private String projectName;
	private Integer projectRound;
	private BigDecimal subscriptionRate;
	private String projectStatus; // ENUM: PREPARING, ANNOUNCEMENT, etc.

	private OffsetDateTime announcementStartDate;
	private OffsetDateTime announcementEndDate;
	private OffsetDateTime subscriptionStartDate;
	private OffsetDateTime subscriptionEndDate;
	private OffsetDateTime projectStartDate;
	private OffsetDateTime projectEndDate;

	private BigDecimal expectedReturn;

	private Boolean isStarred;
	private String thumbnailUrl;

}
