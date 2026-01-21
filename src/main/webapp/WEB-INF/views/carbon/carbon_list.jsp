<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/carbon_list.css" />

<div class="carbon-page">
  <div class="carbon-header">
    <h2>탄소 배출권 상품</h2>
    <p>감축·제거형 탄소 상품을 확인하세요.</p>
  </div>

  <div class="list-controls">
    <div class="filter-group">
  		<t:menu_button
   		 	label="전체보기"
   		 	active="true"
   		 	onClick="filterCategory('ALL', this)" />

  		<t:menu_button
    		label="감축형"
    		onClick="filterCategory('REDUCTION', this)" />

  		<t:menu_button
    		label="제거형"
    		onClick="filterCategory('REMOVAL', this)" />
	</div>
  </div>

  <!-- 카드 컨테이너 -->
  <div id="carbonCardContainer" class="carbon-grid"></div>
</div>

<script src="${pageContext.request.contextPath}/resources/js/domain/carbon/carbon_list.js"></script>

