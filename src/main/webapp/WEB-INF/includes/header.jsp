<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
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
      <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 640 640"
        width="32"
        height="32"
      >
        <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2026 Fonticons, Inc.-->
        <path
          d="M535.3 70.7C541.7 64.6 551 62.4 559.6 65.2C569.4 68.5 576 77.7 576 88L576 274.9C576 406.1 467.9 512 337.2 512C260.2 512 193.8 462.5 169.7 393.3C134.3 424.1 112 469.4 112 520C112 533.3 101.3 544 88 544C74.7 544 64 533.3 64 520C64 445.1 102.2 379.1 160.1 340.3C195.4 316.7 237.5 304 280 304L360 304C373.3 304 384 293.3 384 280C384 266.7 373.3 256 360 256L280 256C240.3 256 202.7 264.8 169 280.5C192.3 210.5 258.2 160 336 160C402.4 160 451.8 137.9 484.7 116C503.9 103.2 520.2 87.9 535.4 70.7z"
        />
      </svg>
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
