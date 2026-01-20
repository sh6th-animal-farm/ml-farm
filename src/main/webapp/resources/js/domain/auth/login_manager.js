/* js/domain/auth/login_manager.js */

async function login(event) {
    event.preventDefault(); // 폼 제출 방지
    
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    try {
        const res = await fetch(ctx + "/api/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email: email, password: password })
        });

        if (!res.ok) {
            alert("로그인 실패: 이메일 또는 비밀번호를 확인하세요.");
            return;
        }

        const data = await res.json();
        
        // [보완] 새로운 로그인을 위해 이전의 모든 흔적(낡은 토큰 등)을 삭제합니다.
        localStorage.clear();
        
        // 토큰 저장
        localStorage.setItem("accessToken", data.accessToken);
        localStorage.setItem("refreshToken", data.refreshToken);
        
        // 활동 시간 저장
        const now = Date.now();
        localStorage.setItem("loginStartTime", now);
        localStorage.setItem("lastActivityTime", now);
        
        alert("로그인 성공!");
        location.href = ctx + "/home";
    } catch (error) {
        console.error("로그인 중 에러 발생:", error);
        alert("서버와 통신할 수 없습니다.");
    }
}