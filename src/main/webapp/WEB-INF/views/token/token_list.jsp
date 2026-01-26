<%@page import="com.animalfarm.mlf.constants.ProjectStatus" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mp" tagdir="/WEB-INF/tags/token"%>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/resources/css/project_list.css">
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/resources/css/token_list.css">

<script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.5.1/sockjs.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>

<script type="module" src="${pageContext.request.contextPath}/resources/js/domain/token/token_list.js"></script>

<%--<script>--%>
<%--    window.onload = function() {--%>
<%--        connectExchange('localhost', '/topic/ticker/777777');--%>
<%--    };--%>
<%--</script>--%>

<div class="token-list-container">
    <div class="section-header">
        <h2>실시간 차트</h2>
    </div>

    <div class="container">
        <div class="background-border-wrapper">
            <form class="background-border" role="search">
                <label for="search-input" class="visually-hidden">검색</label>
                <input type="search" id="search-input" class="input" placeholder="검색어를 입력하세요"/>
                <button type="submit" class="icon-wrapper" aria-label="검색">
                    <img class="icon" src="img/icon.svg" alt=""/>
                </button>
            </form>
        </div>
    </div>
    <div class="container-2">
        <mp:token_table_main tokenList="${tokenList}"/>
        <aside class="aside-preview" aria-label="토큰 상세 정보">
            <div class="container-4">
                <div class="container-5">
                    <div class="container-6">
                        <div class="div-2"><span class="text-wrapper-15">KPYN01</span></div>
                    </div>
                    <div class="container-7">
                        <p class="text-wrapper-16">김포 스마트팜 A · 딸기(사계절)</p>
                    </div>
                </div>
                <div class="div-2">
                    <div class="container-8"><span class="text-wrapper-17">128,000</span></div>
                    <div class="container-9"><span class="text-wrapper-18">+0.76%</span></div>
                </div>
            </div>
            <div class="horizontal-border">
                <div class="container-10">
                    <div class="container-11"><span class="text-wrapper-16">5분봉</span></div>
                    <div class="container-11"><span class="text-wrapper-16">최근 5시간</span></div>
                </div>
                <div class="image" role="img" aria-label="차트 이미지"></div>
            </div>
        </aside>
    </div>
</div>
