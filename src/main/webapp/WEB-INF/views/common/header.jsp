<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<header>
  <div class="container header-inner">
    <a href="${pageContext.request.contextPath}/main" class="logo-text">
      <t:icon name="leaf" color="var(--green-600)" size="24" />
      <span class="keep">마이리틀</span>
      <span style="color: var(--green-600)">스마트팜</span>
    </a>

    <nav>
      <ul class="nav-list">
        <li>
          <a
            href="${pageContext.request.contextPath}/project/list"
            class="nav-item ${activeMenu == 'project' ? 'active' : ''}"
            >프로젝트 목록</a
          >
        </li>
        <li>
          <a
            href="${pageContext.request.contextPath}/token"
            class="nav-item ${activeMenu == 'token-market' ? 'active' : ''}"
            >토큰 거래소</a
          >
        </li>
        <li>
          <a
            href="${pageContext.request.contextPath}/carbon/list"
            class="nav-item ${activeMenu == 'carbon-market' ? 'active' : ''}"
            >탄소 마켓</a
          >
        </li>
        <li>
          <a
            href="${pageContext.request.contextPath}/notice"
            class="nav-item ${activeMenu == 'notice' ? 'active' : ''}"
            >공지사항</a
          >
        </li>
      </ul>
    </nav>

  <div class="auth-group">
      <div id="guest-group" style="display: none; align-items: center; gap: 24px;">
        <a href="${pageContext.request.contextPath}/auth/login" class="btn-login">로그인</a>
        <a href="${pageContext.request.contextPath}/auth/signup" class="btn-signup">회원가입</a>
      </div>

      <div id="user-group" style="display: none;">
      	<div class="dropdown-wrapper">
	        <button type="button" class="icon-btn" onclick="toggleDropdown('noti-dropdown')">
	            <t:icon name="bell_on"/>
	        </button>
	        <div id="noti-dropdown" class="dropdown-content msg-box">
	            <p class="empty-msg">알림이 없습니다.</p>
	        </div>
	    </div>
      	<div class="dropdown-wrapper">
	        <button type="button" class="icon-btn" onclick="toggleDropdown('profile-dropdown')">
	            <t:icon name="profile"/>
	        </button>
	        <div id="profile-dropdown" class="dropdown-content profile-menu">
	            <a href="${pageContext.request.contextPath}/mypage/profile">내 정보</a>
	            <a href="${pageContext.request.contextPath}/mypage/project-history">나의 프로젝트</a>
	            <a href="${pageContext.request.contextPath}/mypage/wallet">나의 전자지갑</a>
	            <a href="${pageContext.request.contextPath}/mypage/transaction-history">거래 내역</a>
	            <a href="javascript:void(0)" onclick="AuthManager.forceLogout()" class="logout-text">로그아웃</a>
	        </div>
	    </div>
        <div class="name-text">
        	${name}마리팜 님
        </div>
      </div>
    </div>
  </div>
</header>
<script>
// DOMContentLoaded까지 기다리지 않고 로그인 상태를 판단하여 헤더 상태 바꾸기
(function() {
        const token = localStorage.getItem("accessToken");
        // 로그인/회원가입 페이지인지는 체크 제외 (필요 시 window.location.pathname 확인)
        const isPublicPage = window.location.pathname.includes("/auth/login") || 
                           window.location.pathname.includes("/auth/signup");

     	// 조건에 맞는 요소만 즉시 display를 설정
        if (token && !isPublicPage) {
            document.getElementById("user-group").style.display = "flex";
        } else {
            document.getElementById("guest-group").style.display = "flex";
        }
    })();

function toggleDropdown(id) {
    // 다른 드롭다운 닫기
    document.querySelectorAll('.dropdown-content').forEach(dropdown => {
        if (dropdown.id !== id) dropdown.classList.remove('show');
    });
    
    // 클릭한 드롭다운 토글
    document.getElementById(id).classList.toggle('show');
}

// 메뉴 외부 클릭 시 닫기
window.onclick = function(event) {
    if (!event.target.closest('.dropdown-wrapper')) {
        document.querySelectorAll('.dropdown-content').forEach(dropdown => {
            dropdown.classList.remove('show');
        });
    }
}
</script>
