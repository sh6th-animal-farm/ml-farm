<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="mp" tagdir="/WEB-INF/tags/mypage"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mypage.css">
<style>
    /* 거래 내역 전용 스타일 */
    .filter-container { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
    .filter-group { display: flex; gap: 4px; flex:1;}
    
    .period-select { 
    	width: 100px;
        padding: 8px 12px; border: 1px solid #F1F1F1; border-radius: 8px; 
        font-size: 13px; color: var(--gray-700); cursor: pointer; outline: none;
    }

</style>

<div class="mypage-container">
    <div class="sidebar-wrapper">
        <jsp:include page="/WEB-INF/views/common/mypage_sidebar.jsp" />
    </div>

    <div class="content-wrapper">
    
    	<t:section_header title="거래 내역" subtitle="투자, 충전, 정산 등 모든 거래 기록을 확인하세요." />

        <div class="filter-container">
	        <div class="filter-group">
	            <t:menu_button label="전체보기" 
	                       active="${empty param.projectStatus}" 
	                       onClick=""/>
	        	<t:menu_button label="청약중" 
	                       active="${param.projectStatus == 'SUBSCRIPTION'}" 
	                       onClick=""/>
	        	<t:menu_button label="공고중" 
	                       active="${param.projectStatus == 'ANNOUNCEMENT'}" 
	                       onClick=""/>
	        	<t:menu_button label="종료됨" 
	                       active="${param.projectStatus == 'COMPLETED'}" 
	                       onClick=""/>
	        </div>
            <select class="period-select">
                <option>전체 기간</option>
                <option>최근 1개월</option>
                <option>최근 3개월</option>
                <option>최근 6개월</option>
            </select>
        </div>

        <mp:transaction_history_table transactionList="${transactionList }"/>
		
		<c:if test="${transactionList.length>0}">
        	<button class="btn-more"  onclick="">+ 더보기</button>
		</c:if>
    </div>
</div>