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
        <div class="section-header">
            <h2>실시간 차트</h2>
        </div>
        <div class="search_box">
            <form class="background-border" role="search">
                <input type="search" id="search-input" class="input" placeholder="검색어를 입력하세요"/>
                <button class="search-icon-btn" onclick="searchKeyword()">
                    <t:icon name="search" size="20" color="var(--gray-900)"/>
                </button>
            </form>
        </div>
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
