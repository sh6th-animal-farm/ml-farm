package com.animalfarrm.mlf.domain.project.dto;

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
public class ProjectPictureDTO {
	private Long projectPictureId; // 프로젝트 사진 ID (PK)
	private Long projectId; // 프로젝트 ID (FK)
	private String imageUrl; // 사진 URL
	private Boolean isThumbnail; // 썸네일 여부
}
