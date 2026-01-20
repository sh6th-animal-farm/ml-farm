<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/profile.css">
<div class="mypage-container">
    <div class="sidebar-wrapper">
        <jsp:include page="/WEB-INF/views/common/mypage_sidebar.jsp" />
    </div>

    <div class="content-wrapper">
        <div class="page-header">
            <h1>내 정보</h1>
            <p>FarmPiece에서 사용되는 회원님의 정보를 관리합니다.</p>
        </div>

        <div class="info-card">
            <div class="profile-summary">
                <span class="user-name">김세준</span>
                <span class="badge-investor">Professional Investor</span>
            </div>
            <p class="join-date">가입일: 2022. 05. 14</p>
        </div>

        <div class="info-card">
            <h2 class="card-title">기본 정보</h2>
            
            <div class="input-group">
                <label class="input-label">이메일</label>
                <div class="input-value">investor_kim@farmpiece.com</div>
            </div>

            <div class="input-group">
                <label class="input-label">휴대폰 번호</label>
                <div class="input-value">010-1234-5678</div>
                <button class="btn-edit">수정</button>
            </div>

            <div class="input-group">
                <label class="input-label">비밀번호</label>
                <div class="input-value">••••••••</div>
                <button class="btn-edit">수정</button>
            </div>
        </div>

        <div class="info-card">
            <h2 class="card-title">알림 및 수신 설정</h2>
            
            <div class="setting-row">
                <div class="setting-info">
                    <p class="s-title">푸시 알림 동의</p>
                    <p class="s-desc">투자 상품 오픈, 이벤트 및 서비스 혜택 알림을 실시간으로 받습니다.</p>
                </div>
                <label class="switch">
                    <input type="checkbox" checked>
                    <span class="slider"></span>
                </label>
            </div>

            <div class="setting-row" style="margin-bottom: 0;">
                <div class="setting-info">
                    <p class="s-title">이메일 수신 동의</p>
                    <p class="s-desc">자산 리포트 및 주요 뉴스레터를 이메일로 받아보실 수 있습니다.</p>
                </div>
                <label class="switch">
                    <input type="checkbox">
                    <span class="slider"></span>
                </label>
            </div>
        </div>

        <div class="delete-account">
            <div class="delete-info">
                <p class="d-title">계정 삭제가 필요하신가요?</p>
                <p class="d-desc">회원 탈퇴 시 모든 투자 기록 및 자산 정보가 삭제되며 복구할 수 없습니다.</p>
            </div>
            <button class="btn-withdraw">회원 탈퇴</button>
        </div>
    </div>
</div>