<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<header style="background-color: #f8f9fa; padding: 10px; border-bottom: 1px solid #dee2e6;">
    <div style="display: flex; justify-content: space-between; align-items: center;">
        <strong>마이리틀스마트팜</strong>
        <div>
            <span id="userDisplay"></span> 님 환영합니다!
            <button onclick="forceLogout()" style="margin-left: 10px;">로그아웃</button>
        </div>
    </div>
</header>

 <script>
 /* ================= 공통 설정 ================= */
/*  const ACCESS_TOKEN_REFRESH_INTERVAL = 5 * 60 * 1000; //55분
 const WARNING_TIME = 2 * 60 * 60 * 1000 + 55 * 60 * 1000; //2H 55M
 const LOGOUT_TIME = 3 * 60 * 60 * 1000; //3h */
 
 let silentRefreshTimer;
 let inactivityTimer;
 
 
 /* ================= 테스트용 설정 ================= */

//AT 1분 → 40초마다 refresh
const ACCESS_TOKEN_REFRESH_INTERVAL = 40 * 1000; // 40초

//경고: 로그인 후 1분 30초 시점
const WARNING_TIME = 90 * 1000; // 1분 30초

//강제 로그아웃: 2분 (RT 만료 시점)
const LOGOUT_TIME = 120 * 1000; // 2분
 
 /* ================= Silent Refresh ================= */
 async function silentRefresh() {
	 const ctx =  "${pageContext.request.contextPath}";
	 const rt = localStorage.getItem("refreshToken");
	 
	 if (!rt) return; //refresh token이 없으면, 이 함수는 바로 종료
	 
	 const res = await fetch(`${ctx}/api/auth/refresh`, {
		method: "POST",
		headers: {"Content-Type": "application/json"},
		body: JSON.stringify({refreshToken: rt })
	 });
	 
	 if (!res.ok) { //서버 응답이 정상적이지 않을 때 (RT만료, 서버오류)
	 	forceLogout();
	    return;
	 }
	 
	 const data = await res.json();
	 localStorage.setItem("accessToken", data.accessToken);
	 localStorage.setItem("refreshToken", data.refreshToken);
	 
 }
 
 
 /* ================= 활동 감지 ================= */
 function resetActivity() {
	 localStorage.setItem("lastActivityTime", Date.now());
	 // clearTimeout: setTimeout으로 예약한 타이머를 취소하는 함수
	 clearTimeout(inactivityTimer);
	
	 // setTimeout: showWarning 함수를 WARNING_TIME(ms) 후에 실행하도록 예약
	 inactivityTimer = setTimeout(showWarning, WARNING_TIME);
	 
 }
 
 
 /* ================= 경고 ================= */
 function showWarning() {
	 alert("5분 후 자동 로그아웃됩니다.");
 }
 
 
 /* ================= 로그아웃 ================= */
 async function forceLogout() {
	 const ctx = "${pageContext.request.contextPath}";
	 const at = localStorage.getItem("accessToken");
	 
	 await fetch(ctx+"/api/auth/logout", {
		 method: "POST",
	     headers: { "Authorization": "Bearer " + at }
	 });
	 
	 localStorage.clear();
	 location.href = ctx+"/auth/login";
	 
 }
 
 
 /* ================= 초기 실행 ================= */
 window.addEventListener("load", () => {
	 // 현재 페이지 주소에 'login'이 포함되어 있으면 로직을 실행하지 않음
	 if (window.location.pathname.includes("login")) {
		 console.log("로그인 페이지이므로 보안 타이머를 작동하지 않습니다.");
	     return; 
	 }
	 
	 // Silent Refresh 주기 시작
	 silentRefreshTimer = setInterval(
		silentRefresh,
	    ACCESS_TOKEN_REFRESH_INTERVAL
	     );
	 
	// 활동 감지
    ["click", "keydown", "mousemove"].forEach(e =>
    	document.addEventListener(e, resetActivity)
    );

     resetActivity();
	 
  	// RT 만료 기준 강제 로그아웃 타이머
     setTimeout(forceLogout, LOGOUT_TIME);
 });


 </script>
 
 
 
</body>
</html>