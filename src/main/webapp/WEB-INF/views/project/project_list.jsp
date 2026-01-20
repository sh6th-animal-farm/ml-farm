<%@page import="com.animalfarm.mlf.constants.ProjectStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/project_list.css">
<script type="module" src="${pageContext.request.contextPath}/resources/js/domain/project/project_list.js"> </script>
<script src="${pageContext.request.contextPath}/resources/js/util/timer.js"></script>

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
                       active="${empty param.projectStatus}" 
                       onClick="filterCategory('', this)"/>
        	<t:menu_button label="청약중" 
                       active="${param.projectStatus == 'SUBSCRIPTION'}" 
                       onClick="filterCategory('SUBSCRIPTION', this)"/>
        	<t:menu_button label="공고중" 
                       active="${param.projectStatus == 'ANNOUNCEMENT'}" 
                       onClick="filterCategory('ANNOUNCEMENT', this)"/>
        	<t:menu_button label="진행중" 
                       active="${param.projectStatus == 'INPROGRESS'}" 
                       onClick="filterCategory('INPROGRESS', this)"/>
        </div>
        <t:search_bar />
    </div>

    <div class="row" id="projectCardContainer">
	    <jsp:include page="/WEB-INF/views/project/project_card_list.jsp" />
	    
	    <%-- 검색 결과가 없을 때 처리 --%>
	    <c:if test="${empty projectList}">
	        <div class="col-12" style="text-align: center; padding: 100px 0;">
	            <p style="color: var(--gray-400);">조건에 맞는 프로젝트가 없습니다.</p>
	        </div>
	    </c:if>
	</div>
</div>