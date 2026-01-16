package com.animalfarm.mlf.domain.project.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public class ProjectDTO implements ImgEditable {
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

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	private OffsetDateTime announcementStartDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	private OffsetDateTime announcementEndDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	private OffsetDateTime subscriptionStartDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	private OffsetDateTime subscriptionEndDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	private OffsetDateTime resultAnnouncementDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	private OffsetDateTime projectStartDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	private OffsetDateTime projectEndDate;

	private BigDecimal expectedReturn;
	private Integer managerCount;

	private List<ProjectPictureDTO> images; // ProjectImages 테이블 연관
	private Boolean isStared;

	// 이미지 파일명 리스트와 삭제할 ID 리스트
	private List<String> projectImageNames; // 신규 추가된 파일명들
	private List<Long> deletedPictureIds; // 삭제 버튼 눌렀던 기존 이미지 ID들
}
