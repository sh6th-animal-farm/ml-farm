<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="mp" tagdir="/WEB-INF/tags/mypage"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mypage.css">
<style>
    /* 탄소 배출권 전용 추가 스타일 */    
    .header-with-btn { display: flex; justify-content: space-between; align-items: flex-end; }
    .btn-market { 
        background: var(--gray-900); color: #fff; border: none; padding: 12px 20px; 
        border-radius: var(--radius-m); font:var(--font-caption-03); cursor: pointer;
        display: flex; align-items: center; gap: 8px; margin-bottom: 24px;
    }



</style>

<div class="mypage-container">
    <div class="sidebar-wrapper">
        <jsp:include page="/WEB-INF/views/common/mypage_sidebar.jsp" />
    </div>

    <div class="content-wrapper">
    
    	<div class="header-with-btn">
	    	<t:section_header title="탄소 배출권 구매 내역" subtitle="회원님이 구매하신 탄소 배출권의 상세 내역을 확인하세요." />
            <button class="btn-market" onclick="location.href='${pageContext.request.contextPath}/market'">마켓으로 이동 ❯</button>
    	</div>
    	
        <mp:carbon_history_table carbonList="${carbonList}"/>

		<c:if test="${carbonList.length>0}">
        	<button class="btn-more"  onclick="">+ 더보기</button>
		</c:if>
    </div>
</div>