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
            href="${pageContext.request.contextPath}/token-market"
            class="nav-item ${activeMenu == 'token-market' ? 'active' : ''}"
            >토큰 거래소</a
          >
        </li>
        <li>
          <a
            href="${pageContext.request.contextPath}/carbon-market"
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
      <a href="${contextPath}/login" class="btn-login">로그인</a>
      <a href="${contextPath}/signup" class="btn-signup">회원가입</a>
    </div>
  </div>
</header>
