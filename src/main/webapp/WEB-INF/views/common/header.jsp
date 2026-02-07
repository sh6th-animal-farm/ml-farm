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
            href="${pageContext.request.contextPath}/notice/list"
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
        <div class="name-text"></div>
      </div>
    </div>
  </div>
</header>
<script type="module">
import { AuthApi } from "${pageContext.request.contextPath}/resources/js/api/auth_api.js";
  (async function () {
    const token = localStorage.getItem("accessToken");
    const isPublicPage =
      location.pathname.includes("/auth/login") ||
      location.pathname.includes("/auth/signup");

    if (!token || isPublicPage) {
      const guestGroup = document.getElementById("guest-group");
      if (guestGroup) guestGroup.style.display = "flex";
      return;
    }

    const userGroup = document.getElementById("user-group");
    const nameEl = userGroup ? userGroup.querySelector(".name-text") : null;

    userGroup.style.display = "flex";

    const cachedName = localStorage.getItem("userName");
    const cachedRole = localStorage.getItem("userRole");
    if (cachedName && nameEl) {
      nameEl.textContent = cachedName + " 님";
    }

    if (!cachedName) {
        AuthApi.getUserName().then(name => {
            const safeName = name || "사용자";
            localStorage.setItem("userName", safeName);
            if (nameEl) nameEl.textContent = safeName + " 님";
        }).catch(e => console.warn("[header] name fetch fail", e));
    }

    let role = cachedRole;
    if (!cachedRole) {
      try {
        role = await AuthApi.getUserRole();
        localStorage.setItem("userRole", role);
      } catch (e) {
        console.warn("[header] role fetch failed", e);
      }
    }
    
    const safeRole = role || "USER"
    if (safeRole === "ADMIN") {
      const profileDropdown = document.getElementById("profile-dropdown");
      const logoutLink = profileDropdown.querySelector(".logout-text");
      
      // 이미 관리자 페이지 링크가 있는지 확인 (중복 추가 방지)
      if (!profileDropdown.querySelector(".admin-menu-link")) {
        const adminLink = document.createElement("a");
        adminLink.href = "${pageContext.request.contextPath}/admin"; // 관리자 메인 경로
        adminLink.textContent = "관리자 페이지";
        adminLink.classList.add("admin-menu-link");
        
        // 로그아웃 링크 바로 앞에 삽입
        profileDropdown.insertBefore(adminLink, logoutLink);
      }
    }
  })();

  window.toggleDropdown = function (id) {
    document.querySelectorAll(".dropdown-content").forEach((d) => {
      if (d.id !== id) d.classList.remove("show");
    });
    document.getElementById(id).classList.toggle("show");
  }

  window.onclick = function (event) {
    if (!event.target.closest(".dropdown-wrapper")) {
      document.querySelectorAll(".dropdown-content").forEach((d) => d.classList.remove("show"));
    }
  };
</script>

