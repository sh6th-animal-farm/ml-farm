package com.animalfarm.mlf.domain.project.dto;

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
public class ProjectSearchReqDTO {
	private Long userId;
	private String projectStatus;
	private String keyword;
}
