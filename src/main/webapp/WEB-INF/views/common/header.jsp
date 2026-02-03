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
<script>
  async function fetchMyName() {
    const token = localStorage.getItem("accessToken");

    const res = await fetch(ctx + "/api/user/me/name", {
      method: "GET",
      headers: {
        "Authorization": token ? "Bearer " + token : "",
        "Content-Type": "application/json"
      }
    });

    if (!res.ok) throw new Error("name api failed: " + res.status);
    return (await res.text()).trim();
  }

  (async function () {
    const token = localStorage.getItem("accessToken");
    const isPublicPage =
      location.pathname.includes("/auth/login") ||
      location.pathname.includes("/auth/signup");

    const guestGroup = document.getElementById("guest-group");
    const userGroup = document.getElementById("user-group");
    const nameEl = userGroup ? userGroup.querySelector(".name-text") : null;

    if (token && !isPublicPage) {
      userGroup.style.display = "flex";

      const cached = localStorage.getItem("userName");
      if (cached && nameEl) {
        nameEl.textContent = cached + " 님";
        return;
      }

      try {
        const name = await fetchMyName();
        const safe = name || "사용자";
        localStorage.setItem("userName", safe);
        if (nameEl) nameEl.textContent = safe + " 님";
      } catch (e) {
        console.warn("[header] name fetch failed", e);
        if (nameEl) nameEl.textContent = "사용자 님";
      }
    } else {
      guestGroup.style.display = "flex";
    }
  })();

  function toggleDropdown(id) {
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

