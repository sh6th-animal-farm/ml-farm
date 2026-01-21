<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/profile.css">
<style>
    /* 프로젝트 내역 전용 추가 스타일 */
    .project-tabs { display: flex; gap: 24px; border-bottom: 1px solid #F1F1F1; margin-bottom: 24px; }
    .tab-item { padding: 12px 4px; font-size: 16px; font-weight: 600; color: var(--gray-400); cursor: pointer; position: relative; }
    .tab-item.active { color: var(--green-600); }
    .tab-item.active::after { content: ''; position: absolute; bottom: -1px; left: 0; right: 0; height: 2px; background: var(--green-600); }
    .tab-count { font-size: 14px; margin-left: 4px; color: var(--gray-400); }

    .filter-group { display: flex; gap: 8px; margin-bottom: 32px; }
    .btn-filter { padding: 8px 16px; border-radius: 20px; border: 1px solid #F1F1F1; background: #fff; font-size: 13px; font-weight: 600; cursor: pointer; }
    .btn-filter.active { background: var(--green-600); color: #fff; border-color: var(--green-600); }

    .project-list { display: flex; flex-direction: column; gap: 16px; }
    .project-item { 
        display: flex; justify-content: space-between; align-items: center;
        padding: 24px 32px; background: #fff; border: 1px solid #F1F1F1; border-radius: 16px;
    }
    .project-info .p-title { font-size: 16px; font-weight: 700; margin-bottom: 4px; }
    .project-info .p-date { font-size: 13px; color: var(--gray-400); }

    .status-group { display: flex; align-items: center; gap: 12px; }
    .badge { padding: 4px 12px; border-radius: 4px; font-size: 12px; font-weight: 700; }
    .badge-notice { background: #E3F2FD; color: #1E88E5; } /* 공고중 */
    .badge-sub { background: #FFF3E0; color: #FB8C00; }    /* 청약중 */
    .badge-ing { background: #E8F5E9; color: #43A047; }    /* 진행중 */
    .badge-end { background: #F5F5F5; color: #9E9E9E; }    /* 프로젝트 종료 */
    .badge-fail { background: #F5F5F5; color: #757575; }   /* 낙첨 */

    .btn-trade { 
        background: #1A1D23; color: #fff; border: none; padding: 10px 24px; 
        border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer;
    }
    .btn-more { 
        width: 100%; padding: 16px; background: #fff; border: 1px solid #F1F1F1; 
        border-radius: 12px; color: var(--gray-900); font-weight: 600; margin-top: 24px; cursor: pointer;
    }
</style>

<div class="mypage-container">
    <div class="sidebar-wrapper">
        <jsp:include page="/WEB-INF/views/common/mypage_sidebar.jsp" />
    </div>

    <div class="content-wrapper">
        <div class="page-header">
            <h1>나의 프로젝트</h1>
            <p>참여 중이거나 관심 있는 농업 재생 프로젝트 현황입니다.</p>
        </div>

        <div class="project-tabs">
            <div class="tab-item active">참여한 프로젝트 <span class="tab-count">8</span></div>
            <div class="tab-item">관심 프로젝트 <span class="tab-count">14</span></div>
        </div>

        <div class="filter-group">
            <button class="btn-filter active">전체 선택</button>
            <button class="btn-filter">청약중</button>
            <button class="btn-filter">공고중</button>
            <button class="btn-filter">프로젝트 종료</button>
        </div>

        <div class="project-list">
            <div class="project-item">
                <div class="project-info">
                    <p class="p-title">연천 킹스베리 딸기 01호</p>
                    <p class="p-date">2026.04.10 - 2026.04.12</p>
                </div>
                <div class="status-group">
                    <span class="badge badge-notice">공고중</span>
                    <button class="btn-trade">토큰 거래</button>
                </div>
            </div>

            <div class="project-item">
                <div class="project-info">
                    <p class="p-title">영암 바이오매스 열분해 01호</p>
                    <p class="p-date">2026.04.10 - 2026.04.12</p>
                </div>
                <div class="status-group">
                    <span class="badge badge-sub">청약중</span>
                    <button class="btn-trade">토큰 거래</button>
                </div>
            </div>

            <div class="project-item">
                <div class="project-info">
                    <p class="p-title">고흥 완숙 토마토 04호</p>
                    <p class="p-date">2026.04.10 - 2026.04.12</p>
                </div>
                <div class="status-group">
                    <span class="badge badge-ing">진행중</span>
                    <span class="badge badge-fail">낙첨</span>
                    <button class="btn-trade">토큰 거래</button>
                </div>
            </div>

            <div class="project-item">
                <div class="project-info">
                    <p class="p-title">제주 감귤 스마트팜 03호</p>
                    <p class="p-date">2026.04.10 - 2026.04.12</p>
                </div>
                <div class="status-group">
                    <span class="badge badge-end">프로젝트 종료</span>
                    <button class="btn-trade">토큰 거래</button>
                </div>
            </div>
        </div>

        <button class="btn-more">+ 더보기</button>
    </div>
</div>