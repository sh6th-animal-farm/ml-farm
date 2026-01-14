package com.animalfarm.mlf.domain.project.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

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
public class ProjectDetailDTO {
	private Long projectId;
	private Long farmId;
	private String projectName;
	private String projectDescription;
	private Integer projectRound;
	private BigDecimal targetAmount;
	private BigDecimal minAmountPerInvestor;
	private BigDecimal maxAmountPerInvestor;
	private BigDecimal actualAmount;
	private BigDecimal subscriptionRate;
	private String projectStatus; // ENUM: PREPARING, ANNOUNCEMENT, etc.

	private OffsetDateTime announcementStartDate;
	private OffsetDateTime announcementEndDate;
	private OffsetDateTime subscriptionStartDate;
	private OffsetDateTime subscriptionEndDate;
	private OffsetDateTime resultAnnouncementDate;
	private OffsetDateTime projectStartDate;
	private OffsetDateTime projectEndDate;

	private BigDecimal expectedReturn;
	private Integer managerCount;

	private List<String> images; // ProjectImages 테이블 연관
	private Boolean isStared;

	private FarmDTO farm;

	private List<BigDecimal> temperatureInside; // FarmEnvData 테이블 연관

	private String crop; // Cultivations 테이블 연관
	private String method;
	private String tickerSymbol; //Tokens 테이블 연관
}
