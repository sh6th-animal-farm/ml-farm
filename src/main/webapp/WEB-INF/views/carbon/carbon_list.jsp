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
		<div class="header-with-description">
			<t:section_header title="탄소마켓" subtitle="보유한 포인트를 사용하여 탄소 배출권을 구매하고 ESG 경영을 실천하세요."/>
			<t:carbon_description/>
		</div>

        <a href="${pageContext.request.contextPath}/mypage/carbon-history" class="btn-history">
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
    </div>
</div>

<%-- [추가] 템플릿용 태그: 기존 t:carbon_card 디자인을 그대로 복사해서 쓰기 위함 --%>
<div id="cardTemplate" style="display: none;">
    <t:carbon_card item="${null}" ctx="${pageContext.request.contextPath}" />
</div>
