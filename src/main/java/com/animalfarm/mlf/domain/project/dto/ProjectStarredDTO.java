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
public class ProjectStarredDTO {
	//관심 프로젝트를 위한 DTO

	private Long userId;
	private Long projectId;
}
