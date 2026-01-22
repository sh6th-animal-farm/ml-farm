<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/carbon_list.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/carbon_card.css" />

<div class="carbon-page">
	<div class="carbon-header">
		<h2>탄소 배출권 상품</h2>
    	<p>감축·제거형 탄소 상품을 확인하세요.</p>
  	</div>

	<div class="list-controls">
    	<div class="filter-group">
      		<t:menu_button
  				label="전체보기"
  				active="${empty param.category}"
  				onClick="location.href='${pageContext.request.contextPath}/carbon/list'" />
		    <t:menu_button
        		label="감축형"
        		active="${selectedCategory eq 'REDUCTION'}"
        		onClick="location.href='${pageContext.request.contextPath}/carbon/list?category=REDUCTION'" />
		    <t:menu_button
        		label="제거형"
        		active="${selectedCategory eq 'REMOVAL'}"
        		onClick="location.href='${pageContext.request.contextPath}/carbon/list?category=REMOVAL'" />
    	</div>
  	</div>

	<div class="carbon-grid">
    	<c:forEach var="it" items="${carbonList}">
    		<t:carbon_card item="${it}" ctx="${pageContext.request.contextPath}" />
  		</c:forEach>

		<c:if test="${empty carbonList}">
   			<div class="empty-state">표시할 상품이 없습니다.</div>
  		</c:if>
	</div>
</div>
