<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

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
            <t:menu_button label="전체보기" active="true"/>
            <t:menu_button label="청약중"/>
            <t:menu_button label="공고중"/>
            <t:menu_button label="진행중"/>
        </div>
        <t:search_bar />
    </div>

    <div class="row">
        <div class="col-4"><t:project_card status="청약중" title="경남 밀양 딸기 스마트팜 5호" date="2026.04.10 - 04.12" percent="85" /></div>
        <div class="col-4"><t:project_card status="청약중" title="경남 밀양 딸기 스마트팜 5호" date="2026.04.10 - 04.12" percent="85" /></div>
        <div class="col-4"><t:project_card status="공고중" title="경남 밀양 딸기 스마트팜 5호" date="2026.04.10 - 04.12" /></div>
        <div class="col-4"><t:project_card status="공고중" title="경남 밀양 딸기 스마트팜 5호" date="2026.04.10 - 04.12" /></div>
        <div class="col-4"><t:project_card status="진행중" title="경남 밀양 딸기 스마트팜 5호" date="2026.04.10 - 04.12" /></div>
        <div class="col-4"><t:project_card status="진행중" title="경남 밀양 딸기 스마트팜 5호" date="2026.04.10 - 04.12" /></div>
    </div>
</div>