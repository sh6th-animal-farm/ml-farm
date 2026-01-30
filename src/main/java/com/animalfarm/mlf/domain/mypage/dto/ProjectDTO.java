package com.animalfarm.mlf.domain.mypage.dto;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectDTO {
	private Long project_id;
	private String project_name;
	private Integer project_round;
	private String project_status; // <= 전체보기, 청약중 등등 프로젝트 필터를 위한 상태

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	private OffsetDateTime announcement_start_date;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	private OffsetDateTime announcement_end_date;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	private OffsetDateTime subscription_start_date;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	private OffsetDateTime subscription_end_date;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	private OffsetDateTime project_start_date;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	private OffsetDateTime project_end_date;

	private String subscription_status; // 대기중인지, 당첨됐는지, 낙첨됐는지 등 의 상태
	private Boolean is_starred;
}
