<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<header style="background-color: #333; color: white; padding: 10px; position: sticky; top: 0; z-index: 1000;">
    <div style="display: flex; justify-content: space-between; align-items: center; max-width: 1200px; margin: 0 auto;">
        <div><strong>마이리틀스마트팜 STO</strong></div>
        
        <div style="font-family: monospace; font-size: 14px;">
            <span style="margin-right: 15px;">🔄 AT 갱신: <b id="refreshTimer">00:00</b></span>
            <span>⏱️ 자동 로그아웃: <b id="logoutTimer">00:00</b></span>
        </div>

        <button onclick="forceLogout()" style="background: #e74c3c; color: white; border: none; padding: 5px 10px; cursor: pointer; border-radius: 4px;">로그아웃</button>
    </div>
</header>

<script>
/* ================= 테스트용 설정 (기존 소스 활용) ================= */
const ACCESS_TOKEN_REFRESH_INTERVAL = 40 * 1000; // 40초
const LOGOUT_TIME = 120 * 1000; // 2분

let lastRefreshTime = Date.now(); // 마지막 갱신 시각 기록

/* ================= 타이머 업데이트 함수 ================= */
function updateVisualTimers() {
    const now = Date.now();
    
    // 1. AT 갱신 타이머 계산
    const timeSinceRefresh = now - lastRefreshTime;
    const remainRefresh = Math.max(0, ACCESS_TOKEN_REFRESH_INTERVAL - timeSinceRefresh);
    document.getElementById("refreshTimer").innerText = formatTime(remainRefresh);

    // 2. 자동 로그아웃 타이머 계산
    const lastActivity = parseInt(localStorage.getItem("lastActivityTime") || now);
    const timeSinceActivity = now - lastActivity;
    const remainLogout = Math.max(0, LOGOUT_TIME - timeSinceActivity);
    document.getElementById("logoutTimer").innerText = formatTime(remainLogout);

    // 로그아웃 시간이 다 되면 즉시 함수 호출 방지 로직 (브라우저 지연 대비)
    if (remainLogout <= 0) {
        console.log("시간 만료로 인한 로그아웃 실행");
    }
}

// ms를 MM:SS 형식으로 변환
function formatTime(ms) {
    const totalSeconds = Math.floor(ms / 1000);
    const m = String(Math.floor(totalSeconds / 60)).padStart(2, '0');
    const s = String(totalSeconds % 60).padStart(2, '0');
    return m + ":" + s;
}

/* ================= 기존 로직 수정 및 통합 ================= */
async function silentRefresh() {
    const ctx = "${pageContext.request.contextPath}";
    const rt = localStorage.getItem("refreshToken");
    
    if (!rt) return;

    const res = await fetch(ctx + "/api/auth/refresh", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({refreshToken: rt})
    });

    if (res.ok) {
        const data = await res.json();
        localStorage.setItem("accessToken", data.accessToken);
        localStorage.setItem("refreshToken", data.refreshToken);
        lastRefreshTime = Date.now(); // 갱신 성공 시 기준 시간 초기화
        console.log("토큰 갱신 완료");
    } else {
        forceLogout();
    }
}

// 초기 실행
window.addEventListener("load", () => {
    if (window.location.pathname.includes("login")) return;

    // 1초마다 화면 타이머 업데이트
    setInterval(updateVisualTimers, 1000);

    // 기존 갱신 및 로그아웃 로직 실행
    setInterval(silentRefresh, ACCESS_TOKEN_REFRESH_INTERVAL);
    
    ["click", "keydown", "mousemove"].forEach(e =>
        document.addEventListener(e, () => {
            localStorage.setItem("lastActivityTime", Date.now());
            // resetActivity() 내부에 있던 타이머 로직이 여기에 포함됩니다.
        })
    );

    // RT 만료 기준 강제 로그아웃 예약
    setTimeout(forceLogout, LOGOUT_TIME);
});

async function forceLogout() {
    const ctx = "${pageContext.request.contextPath}";
    const at = localStorage.getItem("accessToken");
    
    await fetch(ctx + "/api/auth/logout", {
        method: "POST",
        headers: { "Authorization": "Bearer " + at }
    });

    localStorage.clear();
    location.href = ctx + "/auth/login";
}
</script>