<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/domain/project/project_detail.js"> </script>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/project_detail.css">

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
							<c:if test="${projectData.projectStatus eq 'ANNOUNCEMENT'}">공고중</c:if>
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
		<t:subscription_modal 
		    id="subscriptionModal"
		    title="${projectData.projectName} 공모"
		    price="345000"
		    thumbnail="${pageContext.request.contextPath}/uploads/projects/${projectData.images[0]}"
		    userLimit="40000000"
		    walletBalance="${myCash}"
		/>
		<t:warning_card id="noAccountModal" title="연동된 계좌 없음">
   			현재 팜조각에 연동된 <strong>증권사 계좌</strong>가 없습니다.<br> 
			청약 참여를 위해 계좌를 먼저 연동해 주세요.
		</t:warning_card>
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

const chartCtx = document.getElementById('tempBarChart').getContext('2d');
new Chart(chartCtx, {
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
