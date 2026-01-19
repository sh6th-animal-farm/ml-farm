<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/header_test.jsp" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>마이리틀스마트팜 - 홈</title>
</head>
<body>
    <h1>Hello world!</h1>
    <p>서버 시간: ${serverTime}</p>
    
    <div style="margin-top: 20px;">
        <p>로그인에 성공하여 홈 화면에 접속했습니다.</p>
        <button onclick="forceLogout()">로그아웃 테스트</button>
    </div>
</body>
</html>