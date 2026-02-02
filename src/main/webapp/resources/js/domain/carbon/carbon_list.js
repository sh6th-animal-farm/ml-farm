/**
 * [탄소 마켓 리스트 관리 스크립트]
 * 로그인한 사용자의 지분 정보에 기반하여 관련 상품만 필터링하여 노출합니다.
 */
document.addEventListener("DOMContentLoaded", function () {
    // 1. URL 파라미터 미리 파싱
    const params = new URLSearchParams(window.location.search);
    const category = params.get("category") || "ALL";
    
    // 2. [핵심] AuthManager를 통한 선제적 차단
    if (typeof AuthManager !== 'undefined') {
        const token = localStorage.getItem("accessToken");
        
        // 토큰이 아예 없거나 만료된 경우
        if (!token || AuthManager.isTokenExpired(token)) {
            alert("로그인이 필요한 서비스입니다."); // 텍스트 화면 대신 alert 실행
            AuthManager.forceLogout(); // 로그인 페이지로 리다이렉트
            return; // API 호출(loadCarbonList)을 아예 시작하지 않음
        }
    }

    // 3. 인증이 확인된 경우에만 리스트 로드
    loadCarbonList(category);
});

async function loadCarbonList(category) {
    try {
        // 탭 활성화 상태 동기화 (진한 녹색 적용)
        syncActiveMenu(category);
        const token = localStorage.getItem("accessToken");
        LoadingManager.show("carbonGrid");
        const response = await fetch(
            `${ctx}/api/carbon/category?category=${category}`,
            {
                method: "GET",
                headers: {
                    Authorization: "Bearer " + token,
                    "Content-Type": "application/json",
                },
            },
        );
        LoadingManager.hide("carbonGrid");

        if (response.ok) {
            const result = await response.json();
            renderFilteredList(result.payload);
        }
    } catch (e) {
        console.error("리스트 로딩 실패", e);
        LoadingManager.hide("carbonGrid");
    }
}

/**
 * [통합 및 유지] 메뉴 활성화 동기화 함수
 * 팀장님이 만드신 라벨 텍스트와 t:menu_button 구조에 최적화
 */
function syncActiveMenu(category) {
    const upperCat = category ? category.toUpperCase() : "ALL";

    document.querySelectorAll(".filter-group .menu-btn").forEach((btn) => {
        const label = btn.textContent.trim();

        // 텍스트 매칭 로직 보강
        const isActive =
            (upperCat === "ALL" && label.includes("전체")) ||
            (upperCat === "REDUCTION" && label.includes("감축")) ||
            (upperCat === "REMOVAL" && label.includes("제거"));

        btn.classList.toggle("active", isActive);
    });
}

/**
 * [수정] 템플릿 복제 방식의 렌더링 함수
 * 기존 CSS 구조(carbon_card.css)와 클래스명을 절대적으로 준수합니다.
 */
function renderFilteredList(dataList) {
    const grid = document.getElementById("carbonGrid");
    const template = document
        .getElementById("cardTemplate")
        .querySelector(".carbon-card");

    grid.innerHTML = "";

    if (!dataList || dataList.length === 0) {
        grid.innerHTML =
            '<div class="empty-state"><div class="empty-icon">!</div><p>구매 가능한 상품이 없습니다.</p></div>';
        return;
    }

    dataList.forEach((item) => {
        const card = template.cloneNode(true);
        const benefit = item.userBenefit;

        // 1. [이미지 매핑] js-img 클래스를 찾아 src 주입
        const imgElement = card.querySelector(".js-img");
        if (imgElement) {
            imgElement.src =
                item.thumbnailUrl || ctx + "/resources/img/carbon_sample.jpg";
        }

        // 2. [제목 매핑] js-title
        const titleElement = card.querySelector(".js-title");
        if (titleElement) {
            titleElement.textContent = item.cpTitle;
        }

        // 3. [배지 매핑] js-badge (텍스트 + 컬러 클래스)
        const badge = card.querySelector(".js-badge");
        if (badge) {
            badge.textContent = `${item.category === "REMOVAL" ? "제거형" : "감축형"} · ${item.vintageYear}`;
            // 기존 클래스 제거 후 새로 추가 (중복 방지)
            badge.classList.remove("blue", "green");
            badge.classList.add(item.category === "REMOVAL" ? "green" : "blue");
        }

        // 4. [수량 매핑] js-amount
        const amountElement = card.querySelector(".js-amount");
        if (amountElement) {
            amountElement.textContent =
                Number(item.cpAmount || 0).toLocaleString() + " tCO2e";
        }

        // 5. [가격 및 혜택 매핑] js-benefit-area & js-price
        const benefitArea = card.querySelector(".js-benefit-area");
        const priceElement = card.querySelector(".js-price");

        if (benefit && benefitArea && priceElement) {
            // 할인 정보 (취소선 가격 + 할인율)
            benefitArea.innerHTML = `
                <span style="text-decoration:line-through; color:#999;">${Number(item.cpPrice).toLocaleString()} P</span>
                <span style="color:#ff4d4f; font-weight:800; margin-left:5px;">${benefit.discountRate}% 할인</span>
            `;
            // 최종가 (P 단위 포함)
            priceElement.textContent =
                Number(benefit.currentPrice).toLocaleString() + " P";
        }

        // 6. [버튼 매핑] js-btn (이동 로직 + 컬러 클래스)
        const btn = card.querySelector(".js-btn");
        if (btn) {
            // 1. 기존 클래스 싹 비우고 기본 디자인과 검정색 배경 주입
            btn.className = "btn-action js-btn btn-dark";

            // 2. 카테고리에 따라 "호버했을 때만 바뀔 색상" 클래스 추가
            if (item.category === "REDUCTION") {
                btn.classList.add("hover-blue");
            } else if (item.category === "REMOVAL") {
                btn.classList.add("hover-green");
            }

            // 3. 이동 로직
            btn.onclick = () => (location.href = ctx + "/carbon/" + item.cpId);
        }

        // 최종 결과물을 그리드에 추가
        grid.appendChild(card);
    });
}

/**
 * [추가] 물음표 아이콘 가이드 툴팁 토글 함수
 */
function toggleGuide(event) {
    // 1. 클릭 이벤트가 부모 요소로 전달되어 툴팁이 바로 닫히는 것을 방지
    event.stopPropagation();

    const icon = document.getElementById("guideIcon");
    const tooltip = document.getElementById("guideTooltip");

    // 2. 아이콘 색상 토글 (회색 <-> 녹색)
    icon.classList.toggle("active");

    // 3. 툴팁 표시 여부 토글
    const isVisible = tooltip.style.display === "block";
    tooltip.style.display = isVisible ? "none" : "block";
}

// [추가] 툴팁 외부 영역 클릭 시 자동으로 닫기 (사용자 편의성)
document.addEventListener("click", function (e) {
    const tooltip = document.getElementById("guideTooltip");
    const icon = document.getElementById("guideIcon");

    if (tooltip && tooltip.style.display === "block") {
        // 클릭한 곳이 툴팁 내부가 아니라면 닫기
        if (!tooltip.contains(e.target) && e.target !== icon) {
            tooltip.style.display = "none";
            icon.classList.remove("active");
        }
    }
});
