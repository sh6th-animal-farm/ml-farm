<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/home.css" />
<script src="${pageContext.request.contextPath}/resources/js/util/timer.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/domain/home/home.js"></script>

<main class="main-content">
	<section class="hero-minimal">
		<div class="container">
			<div class="hero-wrapper">
				<div class="hero-text-content">
					<t:status_badge status="inProgress" label="Green Investment" className="hero-chip"/>
					<h1 class="hero-main-title">
						농장의 주인이 되는<br> <span class="highlight">가장 가벼운 방법</span>
					</h1>
					<p class="hero-desc">
						어렵기만 했던 스마트팜 투자, 이제 STO 조각 투자로<br> 수익과 탄소배출권까지 한 번에 관리하세요.
					</p>
					<div class="hero-action">
						<a class="btn-main" href="${pageContext.request.contextPath}/auth/signup">지금 시작하기</a>
						<button class="btn-sub" onclick="ToastManager.show('가이드 준비중입니다.')">이용 가이드</button>
					</div>
				</div>

				<div class="hero-graphic">
					<img
						src="https://images.unsplash.com/photo-1558449028-b53a39d100fc?q=80&w=600"
						alt="스마트팜" class="rounded-img">
				</div>
			</div>
		</div>
	</section>

	<div class="container">
		<div class="stats-border-box">
			<div class="s-item">
				<span class="s-label">누적 투자금</span> <span class="s-value">149.8억</span>
			</div>
			<div class="s-line"></div>
			<div class="s-item">
				<span class="s-label">탄소 저감량</span> <span class="s-value">3,420
					<small>tCO2</small>
				</span>
			</div>
			<div class="s-line"></div>
			<div class="s-item">
				<span class="s-label">연 평균 수익률</span> <span
					class="s-value">14.2%</span>
			</div>
		</div>
	</div>

	<section class="container project-on-subscription">
		<div class="section-header-flex">
			<h2 class="section-title">청약 진행 중인 프로젝트</h2>
			<a href="${pageContext.request.contextPath}/project/list"
				class="btn-text-link">전체보기 ></a>
		</div>
		<div class="project-card-list">
			<jsp:include page="/WEB-INF/views/project/project_card_list.jsp" />
		</div>
	</section>

	<section class="carbon-banner">
		<div class="container">
			<div class="carbon-wrapper">
				<div class="carbon-text">
					<h2 class="section-title">
						투명한 탄소 배출권<br>수익 증명
					</h2>
					<p class="section-desc">우리의 스마트팜은 지열 히트펌프 설치 등을 통해 탄소 배출을 획기적으로
						줄입니다. 여기서 발생한 KOC(국내 상쇄 배출권) 수익은 모두 투자자에게 돌아갑니다.</p>
					<a class="btn-outline-green" href="${pageContext.request.contextPath}/carbon/list">마켓 데이터 보기</a>
				</div>
				<div class="carbon-chart-container">
					<div class="chart-ui">
						<canvas id="kocChart"></canvas>
					</div>
				</div>
			</div>
		</div>
	</section>

	<div class="container token-market-top-10">
		<div class="section-header-flex">
			<h2 class="section-title">토큰 거래소 TOP 10</h2>
			<a href="${pageContext.request.contextPath}/token"
				class="btn-text-link">전체보기 ></a>
		</div>
		<div class="top-list">
			<c:forEach var="token" items="${tokenList}" varStatus="status" begin="0" end="9">
				<div class="top-item" onclick="location.href='/token/${token.tokenId}'" style="cursor: pointer;">
					<div class="top-item-left">
						<span class="top-rank">${status.count}</span>
						<span class="top-name">${token.tokenName}</span>
					</div>
					<div class="top-item-right">
						<div class="price">
							<fmt:formatNumber value="${token.marketPrice}" type="number" />원
						</div>
						<c:set var="rateColor" value="${token.changeRate > 0 ? 'var(--error)' : 'var(--info)'}" />
						<div style="color: ${rateColor}; font: var(--font-caption-02);">
							<c:if test="${token.changeRate > 0}">+</c:if>
							<fmt:formatNumber value="${token.changeRate}" pattern="0.00" />%
						</div>
					</div>
				</div>
			</c:forEach>
		</div>
	</div>
	
	<section class="sponsor-section">
    <div class="container">
        <h3 class="sponsor-title">함께하는 파트너사</h3>
    </div>
    <div class="slider-wrapper">
        <div class="slider-track">
            <div class="slide"><img src="https://img.icons8.com/color/96/google-logo.png" alt="Google"></div>
            <div class="slide"><img src="https://img.icons8.com/color/96/microsoft.png" alt="MS"></div>
            <div class="slide"><img src="https://img.icons8.com/color/96/amazon.png" alt="Amazon"></div>
            <div class="slide"><img src="https://img.icons8.com/color/96/nvidia.png" alt="Nvidia"></div>
            <div class="slide"><img src="https://img.icons8.com/color/96/meta--v1.png" alt="Meta"></div>
            <div class="slide"><img src="https://img.icons8.com/?size=100&id=TaJZJbJzrhhN&format=png&color=000000" alt="Intel"></div>
            
            <div class="slide"><img src="https://img.icons8.com/color/96/google-logo.png" alt="Google"></div>
            <div class="slide"><img src="https://img.icons8.com/color/96/microsoft.png" alt="MS"></div>
            <div class="slide"><img src="https://img.icons8.com/color/96/amazon.png" alt="Amazon"></div>
            <div class="slide"><img src="https://img.icons8.com/color/96/nvidia.png" alt="Nvidia"></div>
            <div class="slide"><img src="https://img.icons8.com/color/96/meta--v1.png" alt="Meta"></div>
            <div class="slide"><img src="https://img.icons8.com/?size=100&id=TaJZJbJzrhhN&format=png&color=000000" alt="Intel"></div>
        </div>
    </div>
</section>
</main>