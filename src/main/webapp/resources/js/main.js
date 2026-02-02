const PendingManager = {
    // 다음에 띄울 알림 저장
    setPending: function (message, type = "TOAST") {
        const data = { message, type, timestamp: Date.now() };
        localStorage.setItem("mlf-pending-action", JSON.stringify(data));
    },

    // 저장된 알림 확인 및 실행
    checkAndRun: function () {
        const rawData = localStorage.getItem("mlf-pending-action");
        if (!rawData) return;

        try {
            const data = JSON.parse(rawData);

            // 너무 오래된 정보(예: 1분 전)는 무시 (선택 사항)
            if (Date.now() - data.timestamp < 60000) {
                if (data.type === "TOAST") {
                    ToastManager.show(data.message);
                } else if (data.type === "MODAL") {
                    ModalManager.alert("알림", data.message);
                }
            }
        } catch (e) {
            console.error("Pending 액션 처리 중 오류:", e);
        } finally {
            // 실행 후 반드시 비우기 (무한 팝업 방지)
            localStorage.removeItem("mlf-pending-action");
        }
    },
};

// 페이지 로드 시 자동 실행
document.addEventListener("DOMContentLoaded", () => {
    // 모든 페이지에서 AuthManager를 초기화합니다.
    if (typeof AuthManager !== "undefined") {
        AuthManager.init("${pageContext.request.contextPath}");
    }
    PendingManager.checkAndRun();
});
