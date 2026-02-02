<%@page import="com.animalfarm.mlf.constants.ProjectStatus" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mp" tagdir="/WEB-INF/tags/token"%>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/resources/css/token_list.css">
<script src="${pageContext.request.contextPath}/resources/js/util/lightweight-charts.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/domain/token/token_list.js"></script>

<div class="token-list-container">
    <div class="content-wrapper header-flex">
        <t:section_header title="토큰 거래소" subtitle="실시간 차트를 확인해보세요." />
		<t:search_bar width="416px"/>
    </div>
    <div class="content-wrapper">
        <div class="left-column">
            <mp:token_table_main tokenList="${tokenList}"/>
        </div>
        <div class="right-column">
            <mp:token_chart_card/>
        </div>
    </div>
</div>
