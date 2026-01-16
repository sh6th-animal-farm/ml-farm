package com.animalfarm.mlf.domain.project.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInsertDTO {
	private Long projectId;
	private Long farmId;
	private String projectName;
	private String projectDescription;
	private Integer projectRound;
	private BigDecimal targetAmount;
	private BigDecimal minAmountPerInvestor;
	private BigDecimal actualAmount;
	private BigDecimal subscriptionRate;
	private String projectStatus; // ENUM: PREPARING, ANNOUNCEMENT, etc.

	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mmXXX")
	private OffsetDateTime announcementStartDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mmXXX")
	private OffsetDateTime announcementEndDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mmXXX")
	private OffsetDateTime subscriptionStartDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mmXXX")
	private OffsetDateTime subscriptionEndDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mmXXX")
	private OffsetDateTime resultAnnouncementDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mmXXX")
	private OffsetDateTime projectStartDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mmXXX")
	private OffsetDateTime projectEndDate;

	private BigDecimal expectedReturn;
	private Integer managerCount;

	private List<ProjectPictureDTO> images; // ProjectImages 테이블 연관
	private Boolean isStared;

	private String tokenName; // Tokens 테이블 관련
	private String tickerSymbol;
	private BigDecimal totalSupply;

}
