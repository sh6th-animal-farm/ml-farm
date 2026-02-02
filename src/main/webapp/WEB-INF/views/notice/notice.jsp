<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<div class="notice-page-container">
    <t:section_header 
        title="공지사항" 
        subtitle="마이리틀 스마트팜의 새로운 소식과 투자 정보를 전해드립니다." 
    />

    <div class="notice-list-container">
        <div class="empty-notice">
            <div class="empty-icon">!</div>
            <p>등록된 공지사항이 없습니다.</p>
        </div>
    </div>

    <div class="pagination-wrapper">
        <div class="pagination">
            <a href="#" class="page-link active">1</a>
        </div>
    </div>
</div>

<style>

	.notice-page-container {
		margin-top: 60px;
		margin-bottom: 60px;
	}
	
    /* 공지사항 컨테이너: 디자인 가이드 radius-l 적용 */
    .notice-list-container {
        background-color: var(--gray-0);
        border: 1px solid var(--gray-100);
        border-radius: var(--radius-l);
        
        /* 화면 하단까지 닿는 느낌을 주기 위한 고정 높이 */
        min-height: 570px; 
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
        margin-bottom: 32px; /* 페이지네이션과의 간격 */
    }

    /* 공지사항 없음 상태 디자인 */
    .empty-notice {
        text-align: center;
    }

    .empty-icon {
        width: 64px;
        height: 64px;
        background-color: var(--gray-100);
        color: var(--gray-400);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 32px;
        font-weight: 700;
        margin: 0 auto 16px;
    }

    .empty-notice p {
        color: var(--gray-400);
        font-size: 18px;
        font-weight: 500;
    }

    /* 페이지네이션 외부 스타일 */
    .pagination-wrapper {
        display: flex;
        justify-content: center;
    }

    .pagination {
        display: flex;
        gap: 8px;
    }

    .page-link {
        width: 40px;
        height: 40px;
        display: flex;
        align-items: center;
        justify-content: center;
        text-decoration: none;
        border-radius: var(--radius-m);
        font-weight: 600;
        transition: all 0.2s ease;
        color: var(--gray-400);
        background-color: var(--white);
        border: 1px solid var(--gray-200);
    }

    .page-link.active {
        background-color: var(--green-600);
        color: white;
        border-color: var(--green-600);
    }

    .page-link:hover:not(.active) {
        border-color: var(--green-500);
        color: var(--green-600);
    }
</style>