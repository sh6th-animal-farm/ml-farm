<%-- carbon_list.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/carbon_list.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/carbon_card.css" />
<script src="${pageContext.request.contextPath}/resources/js/domain/carbon/carbon_list.js" defer></script>

<%-- [수정] 현재 카테고리를 JS에서 알 수 있게 히든 태그 추가 --%>
<input type="hidden" id="currentCategory" value="${param.category}">

<div class="carbon-page">
	<div class="carbon-header">
        <div class="header-left">
            <h2 class="market-title">
                탄소마켓 
                <div class="guide-container">
                    <span id="guideIcon" class="help-circle" onclick="toggleGuide(event)">?</span>
                    
                    <div id="guideTooltip" class="guide-tooltip" style="display: none;">
                        <div class="tooltip-content">
                            <h3>탄소마켓이란?</h3>
                            <p>스마트팜 운영 및 친환경 농업 공법을 통해 감축한 탄소 배출권을 거래하는 시장입니다. 구매한 배출권은 기업의 탄소 중립 달성 및 ESG 경영 성과로 활용할 수 있습니다.</p>
                            
                            <h3 class="mt-20">구매 할인 정책</h3>
                            <div class="discount-info">
                                <div class="row"><span>구매 금액</span><span class="right">추가 할인</span></div>
                                <div class="divider"></div>
                                <div class="row"><span>1억 이하</span><span class="percent">1%</span></div>
                                <div class="row"><span>1억 ~ 2억</span><span class="percent">3%</span></div>
                                <div class="row"><span>2억 ~ 3억</span><span class="percent">5%</span></div>
                                <div class="row"><span>3억 초과</span><span class="percent">7%</span></div>
                            </div>
                        </div>
                    </div>
                </div>
            </h2>
            <p>보유한 포인트를 사용하여 탄소 배출권을 구매하고 ESG 경영을 실천하세요.</p>
        </div>

        <a href="${pageContext.request.contextPath}/carbon/history" class="btn-history">
            구매한 탄소 상품 보러가기 &gt;
        </a>
  	</div>

	<div class="list-controls">
    	<div class="filter-group">
            <t:menu_button
                label="전체보기"
                active="${empty param.category}"
                onClick="loadCarbonList('ALL')" />
            <t:menu_button
                label="제거형 (Removal)"
                active="${param.category eq 'REMOVAL'}"
                onClick="loadCarbonList('REMOVAL')" />
            <t:menu_button
                label="감축형 (Reduction)"
                active="${param.category eq 'REDUCTION'}"
                onClick="loadCarbonList('REDUCTION')" />
        </div>
  	</div>

	<%-- [수정] JS가 내 지분 상품만 필터링해서 채워넣을 공간 --%>
    <div id="carbonGrid" class="carbon-grid">
        <div class="empty-state">상품을 불러오는 중입니다...</div>
    </div>
</div>

<%-- [추가] 템플릿용 태그: 기존 t:carbon_card 디자인을 그대로 복사해서 쓰기 위함 --%>
<div id="cardTemplate" style="display: none;">
    <t:carbon_card item="${null}" ctx="${pageContext.request.contextPath}" />
</div>
