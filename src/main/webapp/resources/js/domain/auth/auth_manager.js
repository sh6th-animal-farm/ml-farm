/* js/domain/auth/auth_manager.js */

const AuthManager = {
  // 토큰 갱신 주기: Access Token이 1시간(60분)이므로, 50~55분마다 갱신 권장
  refreshInterval: 55 * 60 * 1000, // 55분
  // 자동 로그아웃 시간: 사용자가 아무 활동이 없을 때 로그아웃시킬 시간
  logoutTime: 3 * 60 * 60 * 1000, // 3시간 (미활동 시 세션 만료)
  lastRefreshTime: Date.now(),

    // 초기화 함수: JSP에서 ctx를 전달받아 실행
    init: function() {
        // 인터셉터를 최상단에서 가장 먼저 설정하여 모든 요청을 감시합니다.
        this.setupAjaxInterceptor();
        
        // AT를 로컬스토리지에서 가져오기
        const token = localStorage.getItem("accessToken");
        // 인증이 제외되는 공개 페이지 목록을 백엔드 SecurityConfig와 맞춰줍니다.
        const path = window.location.pathname;
        const isPublicPage = 
            path.includes("/auth/") ||      // 로그인, 회원가입 등
            path.includes("/project/") ||   // 프로젝트 목록, 상세 (화면)
            path.includes("/token") ||     // 토큰 목록, 상세 (화면)
            path.includes("/home") ||       // 메인 페이지
            path.includes("/main") ||       // 메인 페이지
            path.includes("/policy") ||     // 약관
            path.includes("/notice/list") ||// 공지사항
            path.includes("/carbon/list") ||// 탄소
            path === "/" ||                 // 루트 경로
            path === ctx ||                 // 컨텍스트 루트
            path === ctx + "/";
                           
        // 서버에 요청을 보내기 전, 토큰 자체가 이미 만료되었는지 '선제적으로' 체크합니다.
        // 공개 페이지가 아닌데 토큰이 없거나 만료되었다면 즉시 컷
        if (!isPublicPage) {
            if (!token || this.isTokenExpired(token)) {
                console.warn("보호된 리소스 접근: 인증 정보가 없거나 만료되었습니다.");
                this.forceLogout(); 
                return; 
            }
        } else {
            // [수정] 공개 페이지인데 토큰이 만료된 경우, 쫓아내지 않고 데이터만 정리 (비로그인 UI 유지용)
            if (token && this.isTokenExpired(token)) {
                this.clearStorageOnly(); 
            }
        }

        // 로그인 상태라면 UI 렌더링 및 타이머 가동
        this.renderAuthUI(token, isPublicPage); // UI 렌더링 상태 결정

        // [수정] 토큰이 있을 때만 타이머 가동 (비로그인 시 불필요한 에러 방지)
        if (token && !this.isTokenExpired(token)) {
            setInterval(() => this.silentRefresh(), this.refreshInterval);
            
            ["click", "keydown", "mousemove","scroll"].forEach(e =>
                document.addEventListener(e, () => {
                    localStorage.setItem("lastActivityTime", Date.now());
                })
            );
            setInterval(() => this.checkAutoLogout(), 1000);
        }
    },

    // JWT의 Payload를 디코딩하여 만료 시간(exp)을 현재 시간과 비교합니다.
    isTokenExpired: function(token) {
        try {
            // JWT는 [헤더].[페이로드].[서명] 구조이며, 페이로드는 1번 인덱스에 있습니다.
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            // 브라우저 내장 함수 atob를 사용하여 base64를 디코딩합니다.
            const payload = JSON.parse(window.atob(base64));
            
            // exp값은 초(second) 단위이므로 1000을 곱해 밀리초로 변환합니다.
            const expTime = payload.exp * 1000;
            return Date.now() >= expTime;
        } catch (e) {
            // 토큰 형식이 잘못된 경우에도 만료된 것으로 간주하고 로그아웃 처리합니다.
            return true;
        }
    },

    // 현재 로그인 상태인지 확인하고, 아니면 쫓아내는 함수
    ensureAuth: function() {
        const token = localStorage.getItem("accessToken");
        if (!token || this.isTokenExpired(token)) {
            this.forceLogout();
            return false;
        }
        return true;
    },

    // JWT 토큰의 payload를 해석해 만료 여부 반환
    isTokenExpired: function(token) {
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const payload = JSON.parse(window.atob(base64));
            
            const exp = payload.exp * 1000; // 초 단위를 밀리초로
            return Date.now() >= exp;
        } catch (e) {
            return true; // 해석 불가능한 토큰도 만료된 것으로 간주
        }
    },

    setupAjaxInterceptor: function() {
        const originalFetch = window.fetch;
        window.fetch = async (...args) => {
        
            try {
                const response = await originalFetch(...args);

                // [중앙 집중 처리] 모든 페이지의 fetch 응답은 이곳을 거칩니다.
                // 서버의 JwtAuthenticationEntryPoint가 보낸 401 응답을 체크
                // 401(인증실패) 발생 시 무조건 로그아웃시키던 로직 변경
                if (response.status === 401) {
                    const path = window.location.pathname;
                    // 현재 페이지가 공개 페이지라면 튕겨내지 않음 (토큰만 지움)
                    const isPublic = path.includes("/token") || path.includes("/project") || path === "/" || path.includes("/main");
                    
                    if (isPublic) {
                        console.warn("비로그인 상태 또는 토큰 만료 (공개 페이지이므로 유지)");
                        this.clearStorageOnly();
                        this.renderAuthUI(null, true); 
                        return response; // 튕기지 않고 응답 리턴
                    } else {
                        console.error("인증 실패: 로그인 페이지로 리다이렉트");
                        this.forceLogout();
                    }
                }

                // 서버의 JwtAccessDeniedHandler가 보낸 403 응답을 체크
                if (response.status === 403) {
                    ToastManager.show("권한이 부족합니다.");
                }

                // 3. 만약 서버가 500 에러를 던졌는데, 에러 내용에 '만료'나 '로그인' 키워드가 있다면 로그아웃 처리합니다.
                // (서버에서 예외 처리가 미흡하여 500이 터지는 경우를 대비한 2중 방어선)
                if (response.status === 500) {
                    const cloneRes = response.clone();
                    const errorText = await cloneRes.text();
                    if (errorText.includes("만료") || errorText.includes("로그인")) {
                        this.forceLogout();
                    }
                }

                return response;
            } catch (e) {
                console.error("서버 연결 실패:", e);
                throw e;
            } 
        };
    },
    
    // 로그인 상태에 따라 헤더 메뉴를 전환하는 함수
    renderAuthUI: function(token, isPublicPage) {
        const guestGroup = document.getElementById("guest-group");
        const userGroup = document.getElementById("user-group");
	
		// 요소가 존재하는지 확인 후 처리 (에러 방지)
        if (!guestGroup || !userGroup) return;
		
        if (token && !this.isTokenExpired(token)) {
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

        // RT가 없으면 그냥 끝내는 게 아니라, 로그아웃
        if (!rt) {
            console.warn("리프레시 토큰이 유실되었습니다. 인증을 종료합니다.");
            this.forceLogout();
            return;
        }

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

    // 리다이렉트 없이 저장소 데이터만 삭제하는 함수 추가
    clearStorageOnly: function() {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        localStorage.removeItem("loginStartTime");
        localStorage.removeItem("lastActivityTime");
        localStorage.removeItem("userName");
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
        this.clearStorageOnly();
		
        location.href = ctx + "/auth/login"; // 로그인 페이지 경로 확인 필요
    }
};