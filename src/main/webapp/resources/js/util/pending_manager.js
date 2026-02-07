const PendingManager = {
    // 다음에 띄울 알림 저장
    setPending: function (config, type = "TOAST") {
        const data = { config: config, type, timestamp: Date.now() };
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
                    // 토스트는 보통 문자열만 받으므로 config가 메시지 문자열일 경우 처리
                    const msg =
                        typeof data.config === "object"
                            ? data.config.message
                            : data.config;
                    ToastManager.show(msg);
                } else if (data.type === "MODAL") {
                    ModalManager.open(data.config);
                } else if (data.type === "ALERT_MODAL") {
                    ModalManager.alert(
                        data.config.title,
                        data.config.content,
                        data.config.onConfirm,
                    );
                } else if (data.type === "CONFIRM_MODAL") {
                    ModalManager.confirm(
                        data.config.title,
                        data.config.content,
                        data.config.onConfirm,
                    );
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
