<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ attribute name="item" required="true" type="com.animalfarm.mlf.domain.carbon.dto.CarbonListDTO" %>
<%@ attribute name="ctx"  required="true" type="java.lang.String" %>

<!-- category 안전 처리 -->
<c:set var="cat" value="${item.category}" />
<c:if test="${empty cat}">
  <c:set var="cat" value="ALL" />
</c:if>

<!-- category별 UI -->
<c:choose>
  <c:when test="${cat eq 'REDUCTION'}">
    <c:set var="badgeCls" value="blue"/>
    <c:set var="badgeLabel" value="감축형"/>
    <c:set var="btnCls" value="btn-blue"/>
  </c:when>
  <c:when test="${cat eq 'REMOVAL'}">
    <c:set var="badgeCls" value="green"/>
    <c:set var="badgeLabel" value="제거형"/>
    <c:set var="btnCls" value="btn-green"/>
  </c:when>
  <c:otherwise>
    <c:set var="badgeCls" value=""/>
    <c:set var="badgeLabel" value="전체"/>
    <c:set var="btnCls" value="btn-dark"/>
  </c:otherwise>
</c:choose>

<!-- badge text: "감축형 · 2023" -->
<c:set var="badgeText" value="${badgeLabel} · ${item.vintageYear}" />


<!-- 탄소 카드안의 title-->
<c:set var="title" value="${item.cpTitle}" />

<!-- 이미지: 일단 고정(신경X) -->
<c:set var="imgUrl" value="${ctx}/resources/img/carbon_sample.jpg" />

<article class="carbon-card" data-category="${cat}">
  <div class="carbon-img">
    <img src="${imgUrl}" alt="carbon" />
    <span class="badge ${badgeCls}">
      <c:out value="${badgeText}"/>
    </span>
  </div>

  <div class="carbon-body">
    <div class="carbon-title">
      <c:out value="${title}"/>
    </div>

    <div class="divider"></div>

    <div class="meta-row">
      <span>구매 가능 수량</span>
      <span class="value">
        <c:choose>
          <c:when test="${empty item.cpAmount}">-</c:when>
          <c:otherwise><fmt:formatNumber value="${item.cpAmount}" /></c:otherwise>
        </c:choose>
        tCO2e
      </span>
    </div>

    <div class="meta-row" style="margin-top:8px;">
      <span>인증</span>
      <span class="value">
        <c:out value="${empty item.productCertificate ? '-' : item.productCertificate}" />
      </span>
    </div>

    <div class="price-area">
      <div class="price">
        <c:choose>
          <c:when test="${empty item.cpPrice}">-</c:when>
          <c:otherwise><fmt:formatNumber value="${item.cpPrice}" /></c:otherwise>
        </c:choose>
        P
      </div>
    </div>
  </div>

  <div class="carbon-action">
    <button class="btn-action ${btnCls}" type="button"
      onclick="location.href='${ctx}/carbon/detail?cpId=${item.cpId}'">
      상세 보기 및 주문
    </button>
  </div>
</article>
