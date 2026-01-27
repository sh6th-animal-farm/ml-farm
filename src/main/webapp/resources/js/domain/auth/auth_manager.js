/* js/domain/auth/auth_manager.js */

const AuthManager = {
    // 토큰 갱신 주기: Access Token이 1시간(60분)이므로, 50~55분마다 갱신 권장
    refreshInterval: 55 * 60 * 1000, // 55분 (운영 환경에 맞춰 상향)

    // 자동 로그아웃 시간: 사용자가 아무 활동이 없을 때 로그아웃시킬 시간 
    logoutTime: 3 * 60 * 60 * 1000, // 3시간 (미활동 시 세션 만료)

    lastRefreshTime: Date.now(),

    // 초기화 함수: JSP에서 ctx를 전달받아 실행
    init: function() {
        
        const token = localStorage.getItem("accessToken");
        const isLoginPage = window.location.pathname.includes("/auth/login") || 
                           window.location.pathname.includes("/auth/signup");

        this.renderAuthUI(token, isLoginPage); // UI 렌더링 상태 결정

        // 로그인 페이지가 아니고 토큰이 있을 때만 타이머 가동
        if (token && !isLoginPage) {
            setInterval(() => this.silentRefresh(), this.refreshInterval);
            
            ["click", "keydown", "mousemove","scroll"].forEach(e =>
                document.addEventListener(e, () => {
                    localStorage.setItem("lastActivityTime", Date.now());
                })
            );
            // setTimeout은 한 번 실행되면 끝이지만, 활동에 따라 로그아웃은 미뤄져야 하기 때문입니다.
            setInterval(() => this.checkAutoLogout(), 1000);
        }
        // 모든 fetch 응답을 감시하는 로직 (인터셉터 대용)
        this.setupAjaxInterceptor();

    },

    setupAjaxInterceptor: function() {
        const originalFetch = window.fetch;
        window.fetch = async (...args) => {
            const response = await originalFetch(...args);

            // 서버의 JwtAuthenticationEntryPoint가 보낸 401 응답을 체크
            if (response.status === 401) {
                console.error("인증 실패: 로그인 페이지로 이동합니다.");
                this.forceLogout(); // 세션 정리 및 리다이렉트
            }
            
            // 서버의 JwtAccessDeniedHandler가 보낸 403 응답을 체크
            if (response.status === 403) {
                alert("권한이 부족합니다.");
            }

            return response;
            };
    },
    
    // 로그인 상태에 따라 헤더 메뉴를 전환하는 함수
    renderAuthUI: function(token, isLoginPage) {
        const guestGroup = document.getElementById("guest-group");
        const userGroup = document.getElementById("user-group");
	
		// 요소가 존재하는지 확인 후 처리 (에러 방지)
        if (!guestGroup || !userGroup) return;
		
        if (token && !isLoginPage) {
            // 로그인 상태: guest 숨기고 user 보여줌
            if (guestGroup) guestGroup.style.display = "none";
            if (userGroup) userGroup.style.display = "flex";
        } else {
            // 비로그인 상태: guest 보여주고 user 숨김
            if (guestGroup) guestGroup.style.display = "flex";
            if (userGroup) userGroup.style.display = "none";
        }
    },

    checkAutoLogout: function() {
        const now = Date.now();
        const lastActivity = parseInt(localStorage.getItem("lastActivityTime") || now);
        
        // 마지막 활동으로부터 3시간이 지났다면 강제 로그아웃
        if (now - lastActivity >= this.logoutTime) {
            console.log("세션 만료 로그아웃");
            this.forceLogout();
        }
    },

	silentRefresh: async function() {
        const rt = localStorage.getItem("refreshToken");
        if (!rt) return;

        try {
            const res = await fetch(ctx + "/api/auth/refresh", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({refreshToken: rt})
            });

            if (res.ok) {
                const data = await res.json();
                localStorage.setItem("accessToken", data.accessToken);
                localStorage.setItem("refreshToken", data.refreshToken);
                
                this.lastRefreshTime = Date.now(); 
                console.log("토큰 갱신 완료: " + new Date(this.lastRefreshTime).toLocaleTimeString());
            } else {
                console.warn("❌ [실패] 리프레시 토큰이 만료되었거나 서버 응답이 없습니다.");
                this.forceLogout();
            }
        } catch (e) { console.error("Refresh fail", e); }
    },

    forceLogout: async function() {
        const at = localStorage.getItem("accessToken");
        if (at) {
            try {
                await fetch(ctx + "/api/auth/logout", {
                    method: "POST",
                    headers: { "Authorization": "Bearer " + at }
                });
            } catch (e) { console.error("Logout API error", e); }
        }
        localStorage.removeItem("accessToken");
		localStorage.removeItem("refreshToken");
		localStorage.removeItem("loginStartTime");
		localStorage.removeItem("lastActivityTime");
		
        location.href = ctx + "/auth/login"; // 로그인 페이지 경로 확인 필요
    }
};