// 페이지 로드 시 자동 실행
document.addEventListener("DOMContentLoaded", () => {
    // 모든 페이지에서 AuthManager를 초기화합니다.
    if (typeof AuthManager !== "undefined") {
        AuthManager.init("${pageContext.request.contextPath}");
    }

    PendingManager.checkAndRun();
});
