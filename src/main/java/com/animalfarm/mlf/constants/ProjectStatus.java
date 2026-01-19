package com.animalfarm.mlf.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProjectStatus {
	PREPARING("준비중", "preparing", "others"),
	ANNOUNCEMENT("공고중", "announcement", "bg-info"),
	SUBSCRIPTION("청약중", "subscription", "bg-warning"),
	INPROGRESS("진행중", "inProgress", "bg-primary"),
	CANCELLED("취소됨", "canceled", "others"),
	COMPLETED("종료됨", "completed", "others");

	private final String label;
	private final String badgeStatus;
	private final String btnClass;

}