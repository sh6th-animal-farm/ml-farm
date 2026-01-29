<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- 전달받을 속성 정의 --%>
<%@ attribute name="projectData" type="java.lang.Object" required="true" %>

<%-- 날짜 계산 --%>
<jsp:useBean id="now" class="java.util.Date" />
<fmt:parseDate value="${projectData.announcementEndDate}" var="endDate" pattern="yyyy-MM-dd" />
<c:set var="diff" value="${endDate.time - now.time}" />
<fmt:formatNumber var="daysLeft" value="${diff / (1000*60*60*24)}" pattern="#" />

<aside class="content-side">
    <div class="sticky-side">
        <div class="invest-card">
            <%-- 상태 배지 영역 --%>
            <div style="position: absolute; top: 32px; right: 32px;">
                <c:choose>
                    <c:when test="${projectData.projectStatus eq 'ANNOUNCEMENT'}">
                        <%-- 이미지 참고: 공고중 배지 스타일 --%>
                        <span style="display: inline-block; padding: 6px 14px; background: #EBF5FF; color: #4FAAFF; border-radius: var(--radius-m); font: var(--font-button-02); font-weight: 600;">공고중</span>
                    </c:when>
                    <c:when test="${projectData.projectStatus eq 'SUBSCRIPTION'}">
                        <span style="display: inline-block; padding: 6px 14px; background: var(--warning-light); color: var(--warning); border-radius: var(--radius-m); font: var(--font-button-02); font-weight: 600;">청약중</span>
                    </c:when>
                    <c:when test="${projectData.projectStatus eq 'INPROGRESS'}">
                        <span style="display: inline-block; padding: 4px 12px; background: var(--green-0); color: var(--green-700); border-radius: var(--radius-s); font: var(--font-caption-03);">진행중</span>
                    </c:when>
                    <c:when test="${projectData.projectStatus eq 'COMPLETED'}">
                        <span style="display: inline-block; padding: 4px 12px; background: var(--gray-100); color: var(--gray-600); border-radius: var(--radius-s); font: var(--font-caption-03);">종료</span>
                    </c:when>
                    <c:when test="${projectData.projectStatus eq 'CANCELED'}">
                        <span style="display: inline-block; padding: 4px 12px; background: var(--gray-100); color: var(--gray-600); border-radius: var(--radius-s); font: var(--font-caption-03);">취소</span>
                    </c:when>
                </c:choose>
            </div>

            <p style="color: var(--gray-400); font: var(--font-caption-01); margin-bottom: 8px; font-weight: 500;">${projectData.tickerSymbol}</p>
            <h1 style="font: var(--font-header-02); color: var(--gray-900); margin-bottom: 20px;">${projectData.projectName}</h1>

            <c:choose>
                <%-- 공고중(ANNOUNCEMENT) 또는 청약중(SUBSCRIPTION) 일 때 --%>
                <c:when test="${projectData.projectStatus eq 'ANNOUNCEMENT' || projectData.projectStatus eq 'SUBSCRIPTION'}">
                    <div style="margin-bottom: 32px;">
                        <div style="display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 12px;">
                            <span style="font: var(--font-caption-01); color: var(--gray-900); font-weight: 500;">${projectData.subscriptionRate}% 모집됨</span>
                            <span style="font: var(--font-caption-01); color: var(--gray-600);">
                                <c:choose>
                                    <c:when test="${daysLeft > 0}">D-${daysLeft} 남음</c:when>
                                    <c:when test="${daysLeft == 0}">오늘 마감</c:when>
                                    <c:otherwise>마감됨</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div style="width: 100%; height: 8px; background: var(--gray-100); border-radius: var(--radius-xl); overflow: hidden;">
                            <div style="width: ${projectData.subscriptionRate}%; height: 100%; background: var(--green-600); border-radius: var(--radius-xl);"></div>
                        </div>
                    </div>
                    
                    <div style="background: var(--gray-100); padding: 20px; border-radius: var(--radius-m); margin-bottom: 12px;">
                        <span style="font: var(--font-caption-01); color: var(--gray-500); display: block; margin-bottom: 12px;">총 모집 금액 (Target)</span>
                        <strong style="font: var(--font-header-02); color: var(--gray-900); display: block; font-size: 28px;">
                            <fmt:formatNumber value="${projectData.targetAmount}" pattern="#,###"/>원
                        </strong>
                    </div>

                    <div style="background: #fff; border: 1px solid #E8F5E9; padding: 20px; border-radius: var(--radius-m); margin-bottom: 12px;">
                        <span style="font: var(--font-caption-01); color: var(--gray-500); display: block; margin-bottom: 12px;">1 토큰당 청약 금액</span>
                        <strong style="font: var(--font-header-02); color: var(--green-600); display: block; font-size: 28px;">
                            <fmt:formatNumber value="${projectData.targetAmount / projectData.totalSupply}" pattern="#,###"/>원
                        </strong>
                    </div>

                    <%-- 버튼 분기: 공고중일 때는 회색 비활성화 버튼 --%>
                    <c:choose>
                        <c:when test="${projectData.projectStatus eq 'ANNOUNCEMENT'}">
                            <button class="btn" style="width: 100%; background: #757575; color: #fff; font: var(--font-button-01); padding: 24px 0; border-radius: var(--radius-m); cursor: not-allowed; border:none;" disabled>청약 신청하기</button>
                        </c:when>
                        <c:otherwise>
                            <button class="btn" style="width: 100%; background: var(--green-600); color: #fff; font: var(--font-button-01); padding: 24px 0; border-radius: var(--radius-m); cursor: pointer; border:none;" onclick="handleSubscriptionClick()">청약 신청하기</button>
                        </c:otherwise>
                    </c:choose>
                    
                    <p style="text-align: center; font: var(--font-caption-01); color: var(--gray-400); margin-top: 24px;">* 본 자산은 세준 증권 원장에 실시간 기록됩니다.</p>
                </c:when>

                <%-- 진행중(INPROGRESS) 일 때 --%>
                <c:when test="${projectData.projectStatus eq 'INPROGRESS'}">
                    <div style="background: var(--gray-100); padding: 24px; border-radius: var(--radius-m); margin-bottom: 24px;">
                        <span style="font: var(--font-caption-01); color: var(--gray-500); display: block; margin-bottom: 8px;">현재 토큰가 (Market Price)</span>
                        <div style="display: flex; align-items: baseline; gap: 8px;">
                            <strong style="font: var(--font-header-02); color: var(--gray-900);">358,200 원</strong>
                            <span style="color: var(--error); font: var(--font-caption-01);">▲ 3.8%</span>
                        </div>
                    </div>
                    <button class="btn" style="width: 100%; background: var(--gray-900); color: #fff; font: var(--font-button-01); padding: 20px; border-radius: var(--radius-m); border:none; cursor:pointer;">토큰 거래소 바로가기</button>
                </c:when>
                <%-- 완료('COMPLETED'), 취소('CANCELED') 일 때 --%>
                <c:when test="${projectData.projectStatus eq 'COMPLETED' || projectData.projectStatus eq 'CANCELED'}">
                	<div style="background: var(--gray-100); padding: 24px; border-radius: var(--radius-m); margin-bottom: 24px;">
                        <span style="font: var(--font-caption-01); color: var(--gray-500); display: block; margin-bottom: 8px;">최종 토큰가 (Market Price)</span>
                        <div style="display: flex; align-items: baseline; gap: 8px;">
                            <strong style="font: var(--font-header-02); color: var(--gray-900);">358,200 원</strong>
                        </div>
                    </div>
                    <button class="btn" style="width: 100%; background: #757575; color: #fff; font: var(--font-button-01); padding: 24px 0; border-radius: var(--radius-m); cursor: not-allowed; border:none;" disabled>종료된 프로젝트입니다.</button>
                </c:when>
                <c:otherwise>
                	
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</aside>
<script>
function getUserIdFromToken() {
    const token = localStorage.getItem('accessToken'); // 토큰 저장 키 확인
    if (!token) return null;

    // 토큰의 Payload 부분을 디코딩 (atob 사용)
    const base64Payload = token.split('.')[1];
    const payload = JSON.parse(atob(base64Payload));
    console.log("토큰 내부 정보:", payload);
    return payload.userId; // 토큰에 담긴 key 이름(sub 또는 userId) 확인 필요
}

function handleSubscriptionClick() {
    const token = localStorage.getItem('accessToken');
    
    // 1. 로그인 체크 (atob를 이용한 payload 검증은 detail.js에서 이미 하므로 토큰 존재여부만 체크)
    if (!token) {
        alert("로그인이 필요한 서비스입니다.");
        return;
    }

    // 2. 계좌 여부 체크 (AJAX)
    $.ajax({
        url: '${pageContext.request.contextPath}/api/project/checkAccount',
        type: 'GET',
        headers: {
            "Authorization": "Bearer " + token
        },
        success: function(res) {
            // 서버 응답이 true이면 계좌가 있는 것
            if (res === true) {
                // detail.js에 정의된 함수를 호출하여 잔액 조회 및 청약 모달 실행
                openSubscriptionModal('subscriptionModal');
            } else {
                // 계좌가 없으면 안내 모달 오픈
                document.getElementById('noAccountModal').style.display = 'flex';
            }
        },
        error: function(xhr) {
            // 아까 로그에서 본 400 에러(계좌 없음) 상황 포함
            console.error("계좌 확인 중 오류 발생:", xhr.status);
            document.getElementById('noAccountModal').style.display = 'flex';
        }
    });
}

/* 
function handleSubscriptionClick() {
	const userId = getUserIdFromToken();
    
    if (!userId) {
        alert("로그인이 필요합니다.");
        location.href = "/login";
        return;
    }
	
	$.ajax({
	    // 1. [어디로?] 서버의 이 주소로 찾아갈게!
	    url: '${pageContext.request.contextPath}/api/project/checkAccount', 
	    
	    // 2. [어떻게?] 단순히 데이터를 가져오는 거니까 GET 방식을 쓸게.
	    type: 'GET',
	    headers: {
            "Authorization": "Bearer " + localStorage.getItem('accessToken')
        },
	    // 3. [성공하면?] 서버가 대답을 무사히 보내주면 실행되는 구간이야.
	    success: function(res) {
	    	console.log("서버 응답:", res);
	        if (res === true) { 
	            // 서버가 "응, 계좌 있어(true)"라고 대답하면
	            // 서버가 "아니, 없어(false)"라고 대답하면
	        	const modal = document.getElementById('subscriptionModal'); // 모달창 태그를 찾아서
	            modal.style.display = 'flex';
	        } else {
	            const modal = document.getElementById('noAccountModal'); // 모달창 태그를 찾아서
	            modal.style.display = 'flex'; // 눈에 보이게 만들어!
	        }
	    },
	    // 4. [실패하면?] 인터넷이 끊겼거나 서버가 터졌을 때 실행돼.
	    error: function(xhr) {
	    	console.error("에러 상태:", xhr.status);
	        alert("오류 발생!");
	    }
	});
} */
</script>
