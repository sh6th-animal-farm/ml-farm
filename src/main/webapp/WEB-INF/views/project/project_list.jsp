<%@page import="com.animalfarm.mlf.constants.ProjectStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/project_list.css">

<div class="project-list-container">
    <div class="section-header">
        <h2>프로젝트 지도</h2>
        <p>진행중인 프로젝트를 지도에서 확인하세요</p>
    </div>
    
    <div class="map-area">
    	<t:region_accordian />
        <div style="width: 100%; height:100%; background-color: var(--gray-100); border-radius: var(--radius-l)"></div>
    </div>

    <div class="section-header">
        <h2>프로젝트 목록</h2>
        <p>프로젝트를 선택하여 자세한 정보를 확인하세요</p>
    </div>
    
    <div class="list-controls">
        <div class="filter-group">
            <t:menu_button label="전체보기" 
                       active="${empty param.category}" 
                       onClick="filterCategory('')"/>
        	<t:menu_button label="청약중" 
                       active="${param.category == 'SUBSCRIPTION'}" 
                       onClick="filterCategory('SUBSCRIPTION')"/>
        	<t:menu_button label="공고중" 
                       active="${param.category == 'ANNOUNCEMENT'}" 
                       onClick="filterCategory('ANNOUNCEMENT')"/>
        	<t:menu_button label="진행중" 
                       active="${param.category == 'INPROGRESS'}" 
                       onClick="filterCategory('INPROGRESS')"/>
        </div>
        <t:search_bar />
    </div>

    <div class="row">
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
	
	        <%-- 3. lowerDate 처리 (공고중일 때 청약예정일, 진행중일 때 운영기간) --%>
	        <c:set var="lowerDate" value="${statusEnum.name() == 'ANNOUNCEMENT' 
	                                        ? String.format('%tF ~ %tF', project.subscriptionStartDate, project.subscriptionEndDate) 
	                                        : (statusEnum.name() == 'INPROGRESS' 
	                                            ? String.format('%tF ~ %tF', project.projectStartDate, project.projectEndDate) 
	                                            : '')}" />
            <%-- 3. 태그 호출 --%>
            <t:project_card 
                status="${statusEnum}" 
                title="${project.projectName} ${project.projectRound}호" 
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
</div>
</div>
<script>
/**
 * 카테고리 필터링 함수
 * @param {string} category - Enum 명칭 (SUBSCRIPTION, ANNOUNCEMENT 등)
 */
function filterCategory(category) {
    // 기존의 다른 검색 조건(키워드 등)이 있다면 유지하고 category만 바꿀 수 있도록 구성
    const url = new URL(window.location.href);
    
    if (category) {
        url.searchParams.set('category', category);
    } else {
        // 전체보기 클릭 시 category 파라미터 삭제
        url.searchParams.delete('category');
    }
    
    // 페이지 이동
    location.href = url.pathname + url.search;
}
 </script>