<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<header
  style="
    height: var(--header-height);
    background: #fff;
    border-bottom: 1px solid var(--gray-100);
    position: sticky;
    top: 0;
    z-index: 1000;
    display: flex;
    align-items: center;
  "
>
  <div class="container header-inner">
    <a href="/" class="logo-text">
      <t:icon name="leaf" color="var(--green-600)" size="48"/>
      <span class="keep">마이리틀</span>
      <span style="color: var(--green-600)">스마트팜</span>
    </a>

    <nav>
      <ul class="nav-list">
        <li><a href="#" class="nav-item active">프로젝트 목록</a></li>
        <li><a href="#" class="nav-item">토큰 거래소</a></li>
        <li><a href="#" class="nav-item">탄소 마켓</a></li>
        <li><a href="#" class="nav-item">공지사항</a></li>
      </ul>
    </nav>

    <div class="auth-group">
      <a href="#" class="btn-login">로그인</a>
      <a href="#" class="btn-signup">회원가입</a>
    </div>
  </div>
</header>
