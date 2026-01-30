<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="mp" tagdir="/WEB-INF/tags/mypage"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<script src="${pageContext.request.contextPath}/resources/js/domain/mypage/wallet.js"></script>

<%
	/* 상단 참여한 프로젝트, 관심 프로젝트 탭에 들어갈 숫자 가져와서 List 생성 */
    List<Map<String, Object>> tokenTabs = new ArrayList<>();
    
    Map<String, Object> tab1 = new HashMap<>();
    tab1.put("title", "보유 토큰");
    tab1.put("value", "TOKEN_TYPES");
    tokenTabs.add(tab1);
    
    request.setAttribute("tokenTabs", tokenTabs);
%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mypage.css">
<style>
    /* 전자지갑 전용 스타일 */
    .header-with-btn { display: flex; justify-content: space-between; align-items: flex-end; }
    .btn-link-account { 
        background: var(--gray-900); color: #fff; border: none; padding: 12px 20px; 
        border-radius: var(--radius-m); font:var(--font-caption-03); cursor: pointer;
        display: flex; align-items: center; gap: 8px; margin-bottom: 24px;
    }

    /* 상단 계좌 카드 */
    .account-card {
        display: flex; justify-content: space-between; align-items: center;
        padding: 24px 24px; background: #fff; border: 1px solid #F1F1F1;
        border-radius: 16px; margin-bottom: 24px; box-shadow: var(--shadow);
    }
    .bank-info { display: flex; align-items: center; gap: 16px; }
    .bank-icon { width: 40px; height: 40px; background: #F5F5F5; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 20px; }
    .bank-name { font:var(--font-caption-01); color: var(--gray-400); margin-bottom: 4px; }
    .account-number { font: var(--font-body-02); color: var(--gray-900); }
    .total-amount { display:flex; font:var(--font-header-04); align-items: center; gap:2px; }
    .amount-unit { font:var(--font-caption-01); color: var(--gray-400) }

	
</style>

<div class="mypage-container">
    <div class="sidebar-wrapper">
        <jsp:include page="/WEB-INF/views/common/mypage_sidebar.jsp" />
    </div>

    <div class="content-wrapper">
    	
    	<div class="header-with-btn">
	    	<t:section_header title="나의 전자지갑" subtitle="연동된 증권 계좌와 실시간 투자 현황을 확인하세요." />	
	        <button class="btn-link-account"><t:icon name="link" size="16" color="white"/> 계좌 연동</button>
    	</div>
	
		<%-- 상단 계좌 및 투자 현황 카드 --%>
        <div class="account-card">
            <div class="bank-info">
                <div class="bank-icon">🏦</div>
                <div>
                    <p class="bank-name">${wallet.bankName}</p>
                    <p class="account-number">${wallet.accountNo}</p>
                </div>
            </div>
            <div class="total-amount">
            	<span id="main-cash-balance"><fmt:formatNumber value="${wallet.cashBalance}" type="number"/></span>
    			<span class="amount-unit">원</span>
            </div>
        </div>

		
		<div id="investment-section">
		    <mp:investment_grid 
			    totalAsset="${wallet.totalBalance}" 
			    deposit="${wallet.cashBalance}" 
			    purchaseAmount="${wallet.totalPurchasedValue}" 
			    marketValue="${wallet.totalMarketValue}" 
			    unrealizedGain="${wallet.profitLoss}"  
			    returnPct="${wallet.profitLossRate}%" 
		    />
		</div>
		
		<t:category_tab items="${tokenTabs}" activeValue="TOKEN_TYPES" />
        
        <%-- 보유 토큰 테이블 --%>
        <mp:token_wallet_table tokenList="${tokenList}" />

		<button id="btn-more-tokens" class="btn-more" style="display: none;">+ 더보기</button>
    </div>
</div>