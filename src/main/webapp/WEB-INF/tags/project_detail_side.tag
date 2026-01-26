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
                            <button id="submitBtn" class="btn" onclick="handleSubscriptionClick()" style="width: 100%; background: var(--green-600); color: #fff; font: var(--font-button-01); padding: 24px 0; border-radius: var(--radius-m); cursor: pointer; border:none;">청약 신청하기</button>
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
function handleSubscriptionClick() {
	$.ajax({
	    // 1. [어디로?] 서버의 이 주소로 찾아갈게!
	    url: '${pageContext.request.contextPath}/api/project/checkAccount', 
	    
	    // 2. [어떻게?] 단순히 데이터를 가져오는 거니까 GET 방식을 쓸게.
	    type: 'GET',
	    
	    // 3. [성공하면?] 서버가 대답을 무사히 보내주면 실행되는 구간이야.
	    success: function(res) {
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
	    error: function() {
	        alert("오류 발생!");
	    }
	});
}
</script>
