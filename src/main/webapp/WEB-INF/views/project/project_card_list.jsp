<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ page import="com.animalfarm.mlf.constants.ProjectStatus"%>

<c:forEach var="project" items="${projectList}">
	<div class="col-4">
		<%-- 1. status 변환 로직: DTO의 String 상태값을 Enum 객체로 변환 --%>
		<c:set var="statusEnum"
			value="${ProjectStatus.valueOf(project.projectStatus)}" />

		<%-- 2. upperDate 처리 (청약중일 때만 청약기간, 공고중일 때만 공고기간) --%>
		<c:set var="upperDate"
			value="${statusEnum.name() == 'SUBSCRIPTION' 
	                                        ? String.format('%tF ~ %tF', project.subscriptionStartDate, project.subscriptionEndDate) 
	                                        : (statusEnum.name() == 'ANNOUNCEMENT' 
	                                            ? String.format('%tF ~ %tF', project.announcementStartDate, project.announcementEndDate) 
	                                            : '')}" />
		<c:set var="endTime"
			value="${statusEnum.name() == 'SUBSCRIPTION'
	        								? project.subscriptionEndDate : statusEnum.name() == 'ANNOUNCEMENT'
	        								? project.announcementEndDate : ''}" />

		<%-- 3. lowerDate 처리 (공고중일 때 청약예정일, 진행중일 때 운영기간) --%>
		<c:set var="lowerDate"
			value="${statusEnum.name() == 'ANNOUNCEMENT' 
	                                        ? String.format('%tF ~ %tF', project.subscriptionStartDate, project.subscriptionEndDate) 
	                                        : (statusEnum.name() == 'INPROGRESS' 
	                                            ? String.format('%tF ~ %tF', project.projectStartDate, project.projectEndDate) 
	                                            : '')}" />
		<%-- 3. 태그 호출 --%>
		<t:project_card status="${statusEnum}"
			title="${project.projectName} ${project.projectRound}회차"
			id="${project.projectId}" isStarred="false"
			thumbnailUrl="${project.thumbnailUrl}" endTime="${endTime}"
			upperDate="${upperDate}" lowerDate="${lowerDate}"
			percent="${project.subscriptionRate}" />
	</div>
</c:forEach>
<%-- 검색 결과가 없을 때 처리 --%>
<c:if test="${empty projectList}">
	<div class="col-12" style="text-align: center; padding: 100px 0;">
		<p style="color: var(--gray-400);">조건에 맞는 프로젝트가 없습니다.</p>
	</div>
</c:if>

<style>
.project-card {
	background: white;
	border-radius: var(--radius-l);
	overflow: hidden;
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
	margin-bottom: 24px;
	cursor: pointer;
}

.card-image {
	height: 220px;
	background: #e5e5e5;
	position: relative;
}

.card-image img {
	width: 100%;
	height: 100%;
	object-fit: cover;
	object-position: center;
}

.badge {
	position: absolute;
	top: 16px;
	left: 16px;
}

.interest-btn {
	position: absolute;
	top: 18px;
	right: 16px;
	border: none;
	background-color: transparent;
	cursor: pointer;
	filter: drop-shadow(1px 0 0 var(--gray-100)) 
            drop-shadow(-1px 0 0 var(--gray-100)) 
            drop-shadow(0 1px 0 var(--gray-100)) 
            drop-shadow(0 -1px 0 var(--gray-100));
}

.interest-btn path {
	fill: white;
    transition: fill 0.3s ease;
}

.interest-btn .active path {
    fill: var(--error); /* 활성화 시 색상 */
}

.card-content {
	display: flex;
	flex-direction: column;
	padding: 24px;
}

.card-title {
	font: var(--font-subtitle-01);
	color: var(--gray-900);
}

.card-info {
	display: flex;
	flex-direction: column;
	gap: 4px
}

.card-info-row {
	display: flex;
	justify-content: space-between;
	font: var(--font-caption-01);
	color: var(--gray-400);
}

.card-date {
	font: var(--font-caption-01);
}

.card-dday {
	font: var(--font-button-02);
}

.progress-text {
	display: flex;
	gap: 4px;
	align-items: end;
	color: var(--green-600)
}

.progress-percent {
	font: var(--font-body-03);
}

.progress-percent-text {
	font: var(--font-button-02);
}

.progress-bar {
	height: 6px;
	background: var(--gray-100);
	border-radius: 3px;
	margin-top: 4px;
	margin-bottom: 4px;
}

.bar {
	height: 100%;
	background: var(--green-600);
	border-radius: 3px;
}

.info-list {
	display: flex;
	flex-direction: column;
	gap: 4px;
}

.info-list p {
	display: flex;
	justify-content: space-between;
	font: var(--font-caption-01);
}

.btn-action {
	width: 100%;
	padding: 12px;
	border: none;
	border-radius: var(--radius-s);
	background-color: var(--gray-900);
	color: white;
	font: var(--font-button-01);
	cursor: pointer;
	transition: background-color 0.3s ease, transform 0.2s ease;
}

.project-card:hover .bg-warning {
	background-color: var(--warning);
}

.project-card:hover .bg-info {
	background-color: var(--info);
}

.project-card:hover .bg-primary {
	background-color: var(--green-600);
}

.bg-warning:hover {
	background: var(--warning);
}

.bg-info:hover {
	background: var(--info);
}

.bg-primary:hover {
	background: var(--green-600);
}
</style>