/* js/domain/auth/auth_manager.js */

const AuthManager = {
  // 토큰 갱신 주기: Access Token이 1시간(60분)이므로, 50~55분마다 갱신 권장
  refreshInterval: 55 * 60 * 1000, // 55분
  // 자동 로그아웃 시간: 사용자가 아무 활동이 없을 때 로그아웃시킬 시간
  logoutTime: 3 * 60 * 60 * 1000, // 3시간 (미활동 시 세션 만료)
  lastRefreshTime: Date.now(),

  // 초기화 함수: JSP에서 ctx를 전달받아 실행
  init: function () {
    // 인터셉터를 최상단에서 가장 먼저 설정하여 모든 요청을 감시합니다.
    this.setupAjaxInterceptor();

    // AT를 로컬스토리지에서 가져오기
    const token = localStorage.getItem("accessToken");

    // 인증이 제외되는 공개 페이지 목록을 백엔드 SecurityConfig와 맞춰줍니다.
    const path = window.location.pathname;

    const isPublicPage =
      path.includes("/auth/") || // 로그인, 회원가입 등
      path.includes("/project/") || // 프로젝트 목록, 상세 (화면)
      path.includes("/token/") || // 토큰 목록, 상세 (화면)
      path.includes("/main") || // 메인 페이지
      path === "/" || // 루트 경로
      path === ctx || // 컨텍스트 루트
      path === ctx + "/";

    // 로그인/회원가입 화면인지 (타이머 돌릴지 여부만 결정할 때 사용)
    const isAuthPage = path.includes("/auth/");

    // 서버에 요청을 보내기 전, 토큰 자체가 이미 만료되었는지 '선제적으로' 체크합니다.
    // 공개 페이지가 아닌데 토큰이 없거나 만료되었다면 즉시 컷
    if (!isPublicPage) {
      if (!token || this.isTokenExpired(token)) {
        console.warn("보호된 리소스 접근: 인증 정보가 없거나 만료되었습니다.");
        this.forceLogout();
        return;
      }
    }

    // ✅ UI 렌더링: 공개/비공개 페이지와 무관하게 "유효한 토큰이 있으면" 로그인 UI 표시
    this.renderAuthUI(token, isPublicPage);

    // ✅ 타이머 가동: "로그인/회원가입 페이지가 아니고" 유효 토큰이 있을 때만
    const hasValidToken = token && !this.isTokenExpired(token);
    if (!isAuthPage && hasValidToken) {
      setInterval(() => this.silentRefresh(), this.refreshInterval);

      ["click", "keydown", "mousemove", "scroll"].forEach((e) =>
        document.addEventListener(e, () => {
          localStorage.setItem("lastActivityTime", Date.now());
        })
      );

      // setTimeout은 한 번 실행되면 끝이지만, 활동에 따라 로그아웃은 미뤄져야 하기 때문입니다.
      setInterval(() => this.checkAutoLogout(), 1000);
    }

    // ❌ setupAjaxInterceptor() 중복 호출 제거 (맨 위에서 이미 호출함)
  },

  // JWT의 Payload를 디코딩하여 만료 시간(exp)을 현재 시간과 비교합니다.
  isTokenExpired: function (token) {
    try {
      const base64Url = token.split(".")[1];
      const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
      const payload = JSON.parse(window.atob(base64));

      const expTime = payload.exp * 1000;
      return Date.now() >= expTime;
    } catch (e) {
      // 토큰 형식이 잘못된 경우에도 만료된 것으로 간주
      return true;
    }
  },

  // 현재 로그인 상태인지 확인하고, 아니면 쫓아내는 함수
  ensureAuth: function () {
    const token = localStorage.getItem("accessToken");
    if (!token || this.isTokenExpired(token)) {
      this.forceLogout();
      return false;
    }
    return true;
  },

  // fetch 응답을 감시하는 인터셉터
  setupAjaxInterceptor: function () {
    // 이미 패치된 fetch면 중복 패치 방지 (안전장치)
    if (window.fetch && window.fetch.__isAuthPatched) return;

    const originalFetch = window.fetch;

    const patchedFetch = async (...args) => {
      try {
        const response = await originalFetch(...args);

        // [중앙 집중 처리] 모든 페이지의 fetch 응답은 이곳을 거칩니다.
        if (response.status === 401) {
          console.error("인증 실패(401): 로그인 페이지로 리다이렉트합니다.");
          this.forceLogout();
        }

        if (response.status === 403) {
          alert("권한이 부족합니다.");
        }

        // 서버 예외가 500으로 떨어졌는데, 내용에 만료/로그인 키워드가 있으면 방어적으로 로그아웃
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

    // 마킹
    patchedFetch.__isAuthPatched = true;
    window.fetch = patchedFetch;
  },

  // 로그인 상태에 따라 헤더 메뉴를 전환하는 함수
  renderAuthUI: function (token, isPublicPage) {
    const guestGroup = document.getElementById("guest-group");
    const userGroup = document.getElementById("user-group");

    // 요소가 존재하는지 확인 후 처리 (에러 방지)
    if (!guestGroup || !userGroup) return;

    // ✅ 공개/비공개 페이지와 무관하게, 유효 토큰이 있으면 로그인 UI 표시
    const hasValidToken = token && !this.isTokenExpired(token);

    if (hasValidToken) {
      guestGroup.style.display = "none";
      userGroup.style.display = "flex";
    } else {
      guestGroup.style.display = "flex";
      userGroup.style.display = "none";
    }
  },

  checkAutoLogout: function () {
    const now = Date.now();
    const lastActivity = parseInt(localStorage.getItem("lastActivityTime") || now, 10);

    if (now - lastActivity >= this.logoutTime) {
      console.log("세션 만료 로그아웃");
      this.forceLogout();
    }
  },

  silentRefresh: async function () {
    const rt = localStorage.getItem("refreshToken");

    if (!rt) {
      console.warn("리프레시 토큰이 유실되었습니다. 인증을 종료합니다.");
      this.forceLogout();
      return;
    }

    try {
      const res = await fetch(ctx + "/api/auth/refresh", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ refreshToken: rt }),
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
    } catch (e) {
      console.error("Refresh fail", e);
      // 네트워크 일시 장애는 바로 로그아웃 안 할 수도 있는데, 너희 정책이 빡세면 forceLogout 유지해도 됨
      // this.forceLogout();
    }
  },

  forceLogout: async function () {
    const at = localStorage.getItem("accessToken");
    if (at) {
      try {
        await fetch(ctx + "/api/auth/logout", {
          method: "POST",
          headers: { Authorization: "Bearer " + at },
        });
      } catch (e) {
        console.error("Logout API error", e);
      }
    }

    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("loginStartTime");
    localStorage.removeItem("lastActivityTime");

    location.href = ctx + "/auth/login";
  },
};
