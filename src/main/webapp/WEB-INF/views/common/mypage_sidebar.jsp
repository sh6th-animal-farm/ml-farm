<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<%-- 현재 요청된 URI를 가져와서 active 메뉴를 판단합니다 --%>
<c:set var="currentUri" value="${pageContext.request.requestURI}" />

<style>
    .mypage-sidebar {
        width: 100%;
        max-width: 280px;
        background: #F8FAFB;
        border-radius: 16px;
        padding: 12px;
        display: flex;
        flex-direction: column;
        gap: 4px;
    }

    .sidebar-menu-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 16px 20px;
        text-decoration: none;
        color: #565656;
        font: var(--font-body-01);
        border-radius: 12px;
        transition: all 0.2s ease;
        margin-bottom: 4px;
    }

    /* 마우스 호버 시 효과 */
    .sidebar-menu-item:hover {
        background: rgba(74, 159, 46, 0.05);
        color: #4A9F2E;
    }

    /* 활성화된 메뉴 스타일 */
    .sidebar-menu-item.active {
        background: #FFFFFF;
        color: #4A9F2E;
        font: var(--font-body-04);
        box-shadow: var(--shadow);
    }

	/* 활성화된 메뉴 스타일 */
    .sidebar-menu-item.active svg path {
        fill: #4A9F2E;
    }
    
    .sidebar-menu-item .arrow {
        font-size: 18px;
        display: none; /* 기본적으로 숨김 */
    }

    .sidebar-menu-item.active .arrow {
        display: block; /* active일 때만 화살표 표시 */
    }
</style>

<div class="mypage-sidebar">
    <a href="${pageContext.request.contextPath}/mypage/profile" 
       class="sidebar-menu-item ${menu eq 'profile' ? 'active' : ''}">
        <span>내 정보 관리</span>
        <t:icon name="chevron_right" size="14" color="var(--gray-500)"/>
    </a>

    <a href="${pageContext.request.contextPath}/mypage/project-history" 
       class="sidebar-menu-item ${menu eq 'project' ? 'active' : ''}">
        <span>나의 프로젝트</span>
        <t:icon name="chevron_right" size="14" color="var(--gray-500)"/>
    </a>

    <a href="${pageContext.request.contextPath}/mypage/wallet" 
       class="sidebar-menu-item ${menu eq 'wallet' ? 'active' : ''}">
        <span>나의 전자지갑</span>
        <t:icon name="chevron_right" size="14" color="var(--gray-500)"/>
    </a>

    <a href="${pageContext.request.contextPath}/mypage/transaction-history" 
       class="sidebar-menu-item ${menu eq 'transaction' ? 'active' : ''}">
        <span>거래 내역</span>
        <t:icon name="chevron_right" size="14" color="var(--gray-500)"/>
    </a>

    <a href="${pageContext.request.contextPath}/mypage/carbon-history" 
       class="sidebar-menu-item ${menu eq 'carbon' ? 'active' : ''}">
        <span>탄소 배출권 구매 내역</span>
        <t:icon name="chevron_right" size="14" color="var(--gray-500)"/>
    </a>
</div>