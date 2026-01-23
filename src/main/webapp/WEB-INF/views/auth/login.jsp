<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<style>
    /* 1. 레이아웃: 원본의 중앙 정렬 및 여백 규격을 100% 재현 */
    .auth-page-container {
        display: flex;
        flex-direction: column;
        align-items: center;
        /* 헤더 높이(72px) + 상단 여백(40px)을 고려한 배치 */
        padding: calc(var(--header-height) + 40px) 24px 80px;
        min-height: calc(100vh - var(--header-height));
    }

    /* 2. 카드: 원본의 520px 너비와 40px 패딩 규격 적용 */
    .auth-wrapper {
        width: 100%;
        max-width: 520px;
    }

    .auth-card {
        background: #fff;
        padding: 40px;
        border-radius: var(--radius-l); /* 원본의 radius-xl과 common.css 호환 */
        box-shadow: 0 4px 24px rgba(0,0,0,0.06);
        border: 1px solid var(--gray-100);
    }

    /* 3. 타이틀 및 설명: 원본 텍스트 스타일 적용 */
    .auth-title {
        font-size: 24px;
        color: var(--gray-900);
        font-weight: 700;
        margin-bottom: 12px;
        text-align: center;
    }

    .auth-desc {
        font-size: 14px;
        color: var(--gray-500);
        text-align: center;
        margin-bottom: 32px;
        line-height: 1.5;
    }

    /* 4. 폼 요소: 원본의 50px/54px 높이 규격을 1px 오차 없이 적용 */
    .form-group { margin-bottom: 20px; position: relative; }

    .form-label { 
        display: block; 
        font-size: 14px; 
        font-weight: 700; 
        color: var(--gray-900); 
        margin-bottom: 8px;
    }

    .login-input {
        width: 100%; 
        height: 50px;
        padding: 0 16px; 
        border: 1px solid var(--gray-200);
        border-radius: var(--radius-m); 
        font-size: 15px;
        background: #fff; 
        transition: 0.2s;
        color: var(--gray-900);
    }

    .login-input:focus { 
        outline: none; 
        border-color: var(--green-600); 
    }

    .btn-main {
        width: 100%; 
        height: 54px;
        background: var(--green-600); 
        color: #fff;
        border: none; 
        border-radius: var(--radius-m); 
        font-size: 16px; 
        font-weight: 700; 
        cursor: pointer; 
        margin-top: 20px;
        transition: background 0.2s ease;
    }

    .btn-main:hover { 
        background: var(--green-800); 
    }

    /* 5. 푸터: 계정 생성 안내 영역 */
    .footer-info { 
        margin-top: 32px;
        text-align: center; 
        font-size: 14px;
        color: var(--gray-500); 
    }

    .footer-info a { 
        color: var(--green-600); 
        text-decoration: none; 
        font-weight: 700; 
        cursor: pointer; 
        margin-left: 4px;
    }
</style>

<div class="auth-page-container">
    <div class="auth-wrapper">
        <div class="auth-card">
            <h2 class="auth-title">로그인</h2>
            <p class="auth-desc">팜조각에 오신 것을 환영합니다</p>
            
            <form onsubmit="login(event, '${pageContext.request.contextPath}')">
                <div class="form-group">
                    <label for="email" class="form-label">이메일 주소</label>
                    <input type="email" id="email" class="login-input" 
                           placeholder="example@farmpiece.com" required>
                </div>
                <div class="form-group">
                    <label for="password" class="form-label">비밀번호</label>
                    <input type="password" id="password" class="login-input" 
                           placeholder="비밀번호를 입력하세요" required>
                </div>
                <button type="submit" class="btn-main">로그인</button>
            </form>
            
            <div class="footer-info">
                계정이 없으신가요? 
                <a href="${pageContext.request.contextPath}/auth/signup">회원가입</a>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/resources/js/domain/auth/login_manager.js"></script>