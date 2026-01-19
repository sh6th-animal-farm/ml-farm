<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/header.jsp" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>로그인</title>
</head>
<body>

 <h2>로그인</h2>
 <form onsubmit="login(event)">
	  <input type="email" id="email">
	  <input type="password" id="password">
	  <button type="submit">로그인</button>
</form>
 
 <script>
 async function login(event) {
	 event.preventDefault(); // ⭐ 핵심: form submit 막기
	 
	 const email = document.getElementById("email").value;
	 const password = document.getElementById("password").value;
	 const ctx = "${pageContext.request.contextPath}";
	 console.log("Context Path: ", ctx); 
	 
	 const res = await fetch(ctx + "/api/auth/login", {
		method: "POST",
	 	headers: {"Content-Type": "application/json"},
	 	body: JSON.stringify({email, password})
	 });
	 
	 if (!res.ok) {
		 alert("로그인 실패");
		 return
	 }
	 
	 const data = await res.json();
	 
	 //최초 토큰 발급
	 localStorage.setItem("accessToken", data.accessToken);
	 localStorage.setItem("refreshToken", data.refreshToken);
	 
	 //활동 기준 시간 저장
	 const now = Date.now();
	 localStorage.setItem("loginStartTime", now);
	 localStorage.setItem("lastActivityTime", now);
	 
	 alert("로그인 성공!");
	 location.href = ctx+"/home";
 }
 
 
 </script>
 
 
 
</body>
</html>