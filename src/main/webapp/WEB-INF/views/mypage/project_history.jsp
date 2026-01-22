<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ page import="java.util.*" %>
<%
	/* 상단 참여한 프로젝트, 관심 프로젝트 탭에 들어갈 숫자 가져와서 List 생성 */
    List<Map<String, Object>> projectTabs = new ArrayList<>();
    
    Map<String, Object> tab1 = new HashMap<>();
    tab1.put("title", "참여한 프로젝트");
    tab1.put("count", 8);
    tab1.put("value", "JOIN");
    projectTabs.add(tab1);
    
    Map<String, Object> tab2 = new HashMap<>();
    tab2.put("title", "관심 프로젝트");
    tab2.put("count", 14);
    tab2.put("value", "LIKE");
    projectTabs.add(tab2);
    
    request.setAttribute("projectTabs", projectTabs);
%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mypage.css">
<style>
    /* 프로젝트 내역 전용 추가 스타일 */
    
    .filter-group { display: flex; gap: 4px; margin-bottom: 24px; }
    .project-list { background: #fff; display: flex; flex-direction: column; padding-inline: 24px; border-radius: var(--radius-l); box-shadow: var(--shadow);  }

</style>

<div class="mypage-container">
    <div class="sidebar-wrapper">
        <jsp:include page="/WEB-INF/views/common/mypage_sidebar.jsp" />
    </div>

    <div class="content-wrapper">
		<t:section_header title="나의 프로젝트" subtitle="참여 중이거나 관심 있는 농업 재생 프로젝트 현황입니다." />
		
		<t:category_tab 
		    items="${projectTabs}" 
		    activeValue="${empty param.type ? 'JOIN' : param.type}" 
		/>
        
        <div class="filter-group">
            <t:menu_button label="전체보기" 
                       active="${empty param.projectStatus}" 
                       onClick=""/>
        	<t:menu_button label="청약중" 
                       active="${param.projectStatus == 'SUBSCRIPTION'}" 
                       onClick=""/>
        	<t:menu_button label="공고중" 
                       active="${param.projectStatus == 'ANNOUNCEMENT'}" 
                       onClick=""/>
        	<t:menu_button label="종료됨" 
                       active="${param.projectStatus == 'COMPLETED'}" 
                       onClick=""/>
        </div>

        <div class="project-list">
        	
        	<t:project_history_list_item startDate="2026.04.10" name="연천 킹스베리 딸기 01호" status1="공고중" endDate="2026.04.12"/>
        	<t:project_history_list_item startDate="2026.04.10" name="연천 킹스베리 딸기 01호" status1="공고중" endDate="2026.04.12"/>
        	<t:project_history_list_item startDate="2026.04.10" name="연천 킹스베리 딸기 01호" status1="진행중" status2="낙첨" endDate="2026.04.12"/>
        	<t:project_history_list_item startDate="2026.04.10" name="연천 킹스베리 딸기 01호" status1="종료됨" endDate="2026.04.12"/>
        	
        </div>

        <button class="btn-more" onclick="">+ 더보기</button>
    </div>
</div>