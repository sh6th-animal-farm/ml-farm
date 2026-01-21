<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page import="com.animalfarm.mlf.constants.ProjectStatus" %>

<c:forEach var="project" items="${projectList}">
    <div class="col-4">
        <%-- 1. status 변환 로직: DTO의 String 상태값을 Enum 객체로 변환 --%>
            <c:set var="statusEnum" value="${ProjectStatus.valueOf(project.projectStatus)}" />
            
            <%-- 2. upperDate 처리 (청약중일 때만 청약기간, 공고중일 때만 공고기간) --%>
	        <c:set var="upperDate" value="${statusEnum.name() == 'SUBSCRIPTION' 
	                                        ? String.format('%tF ~ %tF', project.subscriptionStartDate, project.subscriptionEndDate) 
	                                        : (statusEnum.name() == 'ANNOUNCEMENT' 
	                                            ? String.format('%tF ~ %tF', project.announcementStartDate, project.announcementEndDate) 
	                                            : '')}" />
	        <c:set var="endTime" value="${statusEnum.name() == 'SUBSCRIPTION'
	        								? project.subscriptionEndDate : statusEnum.name() == 'ANNOUNCEMENT'
	        								? project.announcementEndDate : ''}"/>
	
	        <%-- 3. lowerDate 처리 (공고중일 때 청약예정일, 진행중일 때 운영기간) --%>
	        <c:set var="lowerDate" value="${statusEnum.name() == 'ANNOUNCEMENT' 
	                                        ? String.format('%tF ~ %tF', project.subscriptionStartDate, project.subscriptionEndDate) 
	                                        : (statusEnum.name() == 'INPROGRESS' 
	                                            ? String.format('%tF ~ %tF', project.projectStartDate, project.projectEndDate) 
	                                            : '')}" />
            <%-- 3. 태그 호출 --%>
            <t:project_card 
                status="${statusEnum}" 
                title="${project.projectName} ${project.projectRound}회차"
                id="${project.projectId}"
                isStarred="${project.isStarred}"
                thumbnailUrl="${project.thumbnailUrl}"
                endTime="${endTime}"
                upperDate="${upperDate}"
                lowerDate="${lowerDate}" 
                percent="${project.subscriptionRate}" 
            />
    </div>
</c:forEach>
<%-- 검색 결과가 없을 때 처리 --%>
<c:if test="${empty projectList}">
    <div class="col-12" style="text-align: center; padding: 100px 0;">
        <p style="color: var(--gray-400);">조건에 맞는 프로젝트가 없습니다.</p>
    </div>
</c:if>