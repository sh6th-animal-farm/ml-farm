<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/mypage.css">

<div class="mypage-container">
    <div class="sidebar-wrapper">
        <jsp:include page="/WEB-INF/views/common/mypage_sidebar.jsp" />
    </div>

    <div class="content-wrapper">
		<t:section_header title="내 정보" subtitle="FarmPiece에서 사용되는 회원님의 정보를 관리합니다." />
		
        <div class="info-card">
            <div class="profile-summary">
                <span class="user-name">${name}김세준</span>
                <t:status_badge label="${user_type}Professional Investor" status="${status!=null ? '':'inProgress'}"/>
            </div>
            <p class="join-date">가입일: ${signup_date} 2026. 01. 23</p>
        </div>

        <div class="info-card">
            <h2 class="card-title">기본 정보</h2>
            
            <div class="input-group-column">
	            <div class="input-group">
	                <label class="input-label">이메일</label>
	                <div class="input-value">${email}</div>
	            </div>
	
	            <div class="input-group">
	                <label class="input-label">휴대폰 번호</label>
	                <div class="input-value">${phone_number}</div>
	                <button class="btn-edit" onclick="">
	                	<t:status_badge label="수정" status="inProgress"/>
	                </button>
	            </div>
	
	            <div class="input-group">
	                <label class="input-label">주소</label>
	                <div class="input-value">${address}</div>
	                <button class="btn-edit" onclick="">
	                	<t:status_badge label="수정" status="inProgress"/>
	                </button>
	            </div>
	
	            <div class="input-group">
	                <label class="input-label">비밀번호</label>
	                <div class="input-value">••••••••</div>
	                <button class="btn-edit" onclick="">
	                	<t:status_badge label="수정" status="inProgress"/>
	                </button>
	            </div>
            </div>
        </div>

        <div class="info-card">
            <h2 class="card-title">알림 및 수신 설정</h2>
            
            <div class="input-group-column">
	            <div class="noti-setting-row">
	                <div class="setting-info">
	                    <p class="s-title">푸시 알림 동의</p>
	                    <p class="s-desc">투자 상품 오픈, 이벤트 및 서비스 혜택 알림을 실시간으로 받습니다.</p>
	                </div>
	                <t:toggle_button onchange="" checked=""/>
	            </div>
	
	            <div class="noti-setting-row">
	                <div class="setting-info">
	                    <p class="s-title">이메일 수신 동의</p>
	                    <p class="s-desc">자산 리포트 및 주요 뉴스레터를 이메일로 받아보실 수 있습니다.</p>
	                </div>
	                <t:toggle_button onchange="" checked=""/>
	            </div>
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