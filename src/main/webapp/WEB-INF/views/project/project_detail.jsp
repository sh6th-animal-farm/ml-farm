<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>마이리틀스마트팜 | ${projectData.projectName}</title>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<style>
/* 페이지 전용 레이아웃 */
.grid-layout {
	display: grid;
	grid-template-columns: repeat(12, 1fr);
	gap: var(--gutter);
	margin-top: 40px;
	margin-bottom: 80px;
}

.content-main {
	grid-column: span 8;
}

.content-side {
	grid-column: span 4;
}

/* 캐러셀 */
.carousel-container {
	position: relative;
	margin-bottom: 40px;
}

.slider-wrapper {
	border-radius: var(--radius-m);
	overflow: hidden;
	height: 420px;
	background: var(--gray-100);
}

.slider {
	display: flex;
	transition: transform 0.4s ease-in-out;
	height: 100%;
}

.slide {
	min-width: 100%;
	height: 100%;
}

.slide img {
	width: 100%;
	height: 100%;
	/* 사진이 짤리지 않게 cover 대신 contain 사용  */
	object-fit: contain;
}

/* 캐러셀 컨트롤 (화살표와 점들을 한 줄로) */
.carousel-control { 
    display: flex; 
    align-items: center; 
    justify-content: center; 
    gap: 12px; /* 요소들 사이의 간격 */
    margin-top: 24px; 
}

/* 점들을 감싸는 박스 */
.dots { 
    display: flex; 
    align-items: center; 
    gap: 8px; /* 점들 사이의 간격 */
}

/* 개별 점 스타일 */
.dot { 
    width: 8px; 
    height: 8px; 
    border-radius: 50%; /* 완벽한 원형 */
    background: var(--gray-200); 
    cursor: pointer; 
    transition: all 0.2s ease; 
    flex-shrink: 0; 
}

/* 활성화된 점 */
.dot.active { 
    background: var(--green-600); 
    width: 20px; 
    border-radius: var(--radius-xl); 
}

/* 화살표 버튼 미세 조정 */
.nav-arrow { 
    background: none; 
    border: 1px solid var(--gray-200); 
    width: 32px; 
    height: 32px; 
    border-radius: 50%; 
    cursor: pointer; 
    display: flex; 
    align-items: center; 
    justify-content: center; 
    color: var(--gray-500); 
    line-height: 1;
}

/* 탭 */
.tab-nav {
	display: flex;
	gap: 32px;
	border-bottom: 2px solid var(--gray-100);
	margin-bottom: 32px;
}

.tab-btn {
	padding: 16px 4px;
	font: var(--font-subtitle-01);
	color: var(--gray-400);
	cursor: pointer;
	border: none;
	background: none;
	position: relative;
}

.tab-btn.active {
	color: var(--green-600);
}

.tab-btn.active::after {
	content: '';
	position: absolute;
	bottom: -2px;
	left: 0;
	width: 100%;
	height: 2px;
	background: var(--green-600);
}

.tab-panel {
	display: none;
	animation: fadeIn 0.4s ease;
}

.tab-panel.active {
	display: block;
}

/* [수정 포인트] 섹션 카드 스타일 - 선택자 강화로 font: var() 적용 보장 */
.info-section {
	display: grid;
	grid-template-columns: repeat(2, 1fr);
	gap: 16px;
}

.info-section .info-item {
	padding: 24px;
	background: #FFFFFF;
	border: 1px solid var(--gray-100);
	border-radius: var(--radius-m);
}

/* label 변수 적용 */
.info-section .info-item label {
	font: var(--font-caption-01) !important;
	color: var(--gray-500);
	display: block;
	margin-bottom: 8px;
}

/* p 태그 font-header-02 변수 적용 */
.info-section .info-item p {
	font: var(--font-subtitle-01) !important;
	color: var(--gray-900);
	margin: 0;
}

.sticky-side {
	position: sticky;
	top: 96px;
}

.invest-card {
	padding: 32px;
	border-radius: var(--radius-m);
	background: #fff;
	border: 1px solid var(--gray-100);
	box-shadow: 0 10px 30px rgba(0, 0, 0, 0.03);
	position: relative;
}

.chart-container {
	margin-top: 4px;
	padding: 24px;
	background: var(--gray-0);
	border: 1px solid var(--gray-100);
	border-radius: var(--radius-m);
}

@
keyframes fadeIn {from { opacity:0;
	transform: translateY(10px);
}

to {
	opacity: 1;
	transform: translateY(0);
}
}
</style>
</head>
<body>

	<div class="container">
		<div class="grid-layout">
			<main class="content-main">
				<div class="carousel-container">
					<div class="slider-wrapper">
						<div class="slider" id="slider">
							<c:forEach items="${projectData.images}" var="imgUrl">
								<div class="slide">
									<img src="${pageContext.request.contextPath}/uploads/projects/${imgUrl}" alt="이미지">
								</div>
							</c:forEach>
						</div>
					</div>
					<div class="carousel-control">
						<%-- 왼쪽 화살표 --%>
						<button class="nav-arrow" onclick="moveSlide(-1)">❮</button>

						<%-- 점들 (CSS에서 flex 적용됨) --%>
						<div class="dots" id="dots">
							<c:forEach items="${projectData.images}" varStatus="status">
								<div class="dot ${status.first ? 'active' : ''}"
									onclick="currentSlide(${status.index})"></div>
							</c:forEach>
						</div>

						<%-- 오른쪽 화살표 --%>
						<button class="nav-arrow" onclick="moveSlide(1)">❯</button>
					</div>
				</div>

				<div class="tab-nav">
					<button class="tab-btn active"
						onclick="openTab(event, 'invest-info')">투자 정보</button>
					<button class="tab-btn" onclick="openTab(event, 'farm-info')">농장 정보</button>
				</div>

				<div id="invest-info" class="tab-panel active">
					<div class="info-section">
						<div class="info-item">
							<label>예상 수익률</label>
							<p>${projectData.expectedReturn}%</p>
						</div>
						<div class="info-item">
							<label>청약 달성률</label>
							<p>${projectData.subscriptionRate}%</p>
						</div>
						<div class="info-item">
							<label>총 모집 금액</label>
							<p>
								<fmt:formatNumber value="${projectData.actualAmount}"
									pattern="#,###" />원
							</p>
						</div>
						<div class="info-item">
							<label>목표 금액</label>
							<p>
								<fmt:formatNumber value="${projectData.targetAmount}"
									pattern="#,###" />원
							</p>
						</div>
						<div class="info-item">
							<label>인당 투자 최소 금액</label>
							<p>
								<fmt:formatNumber value="${projectData.minAmountPerInvestor}"
									pattern="#,###" />원
							</p>
						</div>
						<div class="info-item">
							<label>진행 상태</label>
							<p>
								<c:if test="${projectData.projectStatus eq 'PREPARING'}">공모중</c:if>
								<c:if test="${projectData.projectStatus eq 'SUBSCRIPTION'}">청약중</c:if>
								<c:if test="${projectData.projectStatus eq 'INPROGRESS'}">진행중</c:if>
								<c:if test="${projectData.projectStatus eq 'CANCELED'}">취소</c:if>
								<c:if test="${projectData.projectStatus eq 'COMPLETED'}">종료</c:if>
							</p>
						</div>
					</div>
				</div>

				<div id="farm-info" class="tab-panel">
					<div class="info-section">
						<div class="info-item">
							<label>농장 위치</label>
							<p>${projectData.farm.addressSido}</p>
						</div>
						<div class="info-item">
							<label>운영 인원</label>
							<p>${projectData.managerCount}명</p>
						</div>
						<div class="info-item">
							<label>농장 면적</label>
							<p>
								<fmt:formatNumber value="${projectData.farm.area}"
									pattern="#,###.##" />m²
							</p>
						</div>
						<div class="info-item">
							<label>재배 방법</label>
							<p>${projectData.method}</p>
						</div>
						<div class="info-item" style="width: 100%; margin-bottom: 24px;">
							<label>운영 계획</label>
							<p style="font-weight: 400;">${projectData.projectDescription}</p>
						</div>
					</div>
					<div class="chart-container">
						<p
							style="font: var(--font-caption-02); color: var(--gray-900); margin-bottom: 20px;">농장 실시간 기온 추이</p>
						<canvas id="tempBarChart"></canvas>
					</div>
				</div>
			</main>
			<t:project_detail_side projectData="${projectData}" />
		</div>
	</div>

	<script>
    function openTab(evt, tabName) {
        const panels = document.getElementsByClassName("tab-panel");
        for (let p of panels) p.classList.remove("active");
        const btns = document.getElementsByClassName("tab-btn");
        for (let b of btns) b.classList.remove("active");
        document.getElementById(tabName).classList.add("active");
        evt.currentTarget.classList.add("active");
    }

    let currentIdx = 0;
    const slider = document.getElementById('slider');
    const dots = document.querySelectorAll('.dot');
    const totalSlides = ${projectData.images.size() > 0 ? projectData.images.size() : 1};

    function updateCarousel() {
        slider.style.transform = `translateX(-` + (currentIdx * 100) + `%)`;
        dots.forEach((dot, i) => dot.classList.toggle('active', i === currentIdx));
    }
    function moveSlide(step) {
        currentIdx = (currentIdx + step + totalSlides) % totalSlides;
        updateCarousel();
    }
    function currentSlide(idx) {
        currentIdx = idx;
        updateCarousel();
    }

    const tempData = [];
    const tempLabels = [];
    <c:forEach items="${projectData.temperatureInside}" var="temp" varStatus="status">
        tempData.push(${temp});
        tempLabels.push("${status.index + 9}시");
    </c:forEach>

    const ctx = document.getElementById('tempBarChart').getContext('2d');
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: tempLabels,
            datasets: [{
                label: '내부 기온 (℃)',
                data: tempData,
                backgroundColor: '#6CC32D',
                borderRadius: 4
            }]
        },
        options: {
            responsive: true,
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: false, min: 15, max: 30 } }
        }
    });
</script>
</body>
</html>