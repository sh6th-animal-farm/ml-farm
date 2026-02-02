const LoadingManager = {
    // 1. 특정 영역(Element) 내부에 스피너 생성
    show: function (targetOrId) {
        const target =
            typeof targetOrId === "string"
                ? document.getElementById(targetOrId)
                : targetOrId;

        if (!target) return;

        // 이미 로딩 중이면 중복 생성 방지
        if (target.querySelector(".mlf-loading-overlay")) return;

        // 부모 요소에 relative가 없으면 위치가 틀어지므로 강제 지정
        const originalPos = window.getComputedStyle(target).position;
        if (originalPos === "static") {
            target.style.position = "relative";
        }

        const overlay = document.createElement("div");
        overlay.className = "mlf-loading-overlay";
        overlay.innerHTML = '<div class="mlf-spinner"></div>';

        target.appendChild(overlay);
    },

    // 2. 특정 영역의 스피너 제거
    hide: function (targetOrId) {
        const target =
            typeof targetOrId === "string"
                ? document.getElementById(targetOrId)
                : targetOrId;

        if (!target) return;
        const overlay = target.querySelector(".mlf-loading-overlay");
        if (overlay) overlay.remove();
    },

    // 3. 화면 전체를 막는 로딩 (결제, 페이지 전환 등)
    showGlobal: function () {
        if (document.getElementById("globalLoading")) return;

        const overlay = document.createElement("div");
        overlay.id = "globalLoading";
        overlay.className = "mlf-loading-overlay global";
        overlay.innerHTML = '<div class="mlf-spinner"></div>';

        document.body.appendChild(overlay);
    },

    hideGlobal: function () {
        const global = document.getElementById("globalLoading");
        if (global) global.remove();
    },
};
