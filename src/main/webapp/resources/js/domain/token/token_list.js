import { TokenApi } from "./token_api.js";

let sideChart = null;
let sideSeries = null;

let detailSubscription = null;
let hoverTimer = null;

let isPageExiting = false;

// 1. 페이지를 떠나기 직전에 플래그를 true로 바꾸고 alert을 무력화
window.addEventListener("beforeunload", () => {
    isPageExiting = true;

    // 브라우저의 alert 함수를 빈 함수로 덮어씌워 버림 (강력 차단)
    window.alert = function () {
        console.warn("페이지 이동 중 발생한 alert 무시됨:", arguments[0]);
    };
});

const TokenUIManager = {
    cache: new Map(),

    // 신규 생성 또는 업데이트
    sync(data) {
        let row = this.cache.get(data.tokenId);

        if (!row) {
            row = createNewTokenRow(data);
            this.cache.set(data.tokenId, row);
            this.insertToTable(row);
        }

        updateTokenRow(row, data);
    },

    // DOM 삽입 로직 전담
    insertToTable(row) {
        const tbody = document.querySelector(".token_table_main tbody");
        if (!tbody) return;

        // 데이터 없음 행 처리
        const emptyRow = tbody.querySelector("td[colspan]");
        if (emptyRow) emptyRow.remove();

        tbody.appendChild(row);
    },
};

document.addEventListener("DOMContentLoaded", () => {
    // DB 및 Redis에서 가져온 기존 행들을 캐시에 먼저 등록
    document.querySelectorAll('tr[id^="token-row-"]').forEach((row) => {
        const id = row.id.replace("token-row-", "");
        TokenUIManager.cache.set(id, row);
    });

    // 이벤트 위임: tbody 하나에만 클릭 리스너 부여
    const tbody = document.querySelector(".token_table_main tbody");
    if (!tbody) {
        return;
    }

    // 클릭 -> 상세 페이지로 이동
    tbody.addEventListener("click", (e) => {
        const row = e.target.closest("tr");
        if (row && row.id.startsWith("token-row-")) {
            clearTimeout(hoverTimer);

            if (detailSubscription) {
                detailSubscription.unsubscribe();
                detailSubscription = null;
            }

            const id = row.id.replace("token-row-", "");

            location.href = `/mlf/token/${id}`;
        }
    });

    // 호버 -> 우측 패널
    tbody.addEventListener("mouseover", (e) => {
        const row = e.target.closest("tr");
        if (row && row.id.startsWith("token-row-")) {
            const id = row.id.replace("token-row-", "");

            // 이전에 돌고 있던 타이머가 있다면 취소 (새로운 호버 시작)
            clearTimeout(hoverTimer);

            // 0.5초(500ms) 후에 구독 함수 실행
            hoverTimer = setTimeout(() => {
                console.log(`${id}번 토큰 정보 로드`);
                subscribeToTokenDetail(id);
                renderSideChart(id);
            }, 100);
        }
    });

    // 마우스가 벗어날 때: 타이머 취소 (스쳐 지나가는 경우 방지)
    tbody.addEventListener("mouseout", (e) => {
        const row = e.target.closest("tr");
        if (row) {
            clearTimeout(hoverTimer);
        }
    });
});

/* 웹소켓 연결 및 구독 */
WebSocketManager.connect(TokenApi.WS_CONN, function () {
    // 1. 전체 토큰 리스트 구독
    WebSocketManager.subscribe("list", "/topic/tokenList", function (data) {
        console.log("[WebSocket - 토큰 리스트]", data);
        TokenUIManager.sync(data);
    });
});

async function subscribeToTokenDetail(tokenId) {
    // 기존 구독이 있다면 해제 (중첩 방지)
    if (detailSubscription) {
        detailSubscription.unsubscribe();
    }

    try {
        const response = await TokenApi.getOhlc(tokenId);
        if (response && response) {
            updateRightPanelUI(response);
        }
    } catch (error) {
        console.error("상세 정보 로드 실패:", error);
    }

    // 새로운 토큰 상세 정보 구독
    detailSubscription = WebSocketManager.subscribe(
        "ohlcv",
        `/topic/tokenList/${tokenId}`,
        function (data) {
            console.log("[WebSocket - 우측 패널 업데이트]", data);
            updateRightPanelUI(data);
        },
    );
}

function updateRightPanelUI(data) {
    // 1. 기본 텍스트 정보 업데이트
    const elTicker = document.getElementById("panel-ticker-symbol");
    const elName = document.getElementById("panel-token-name");
    const elPrice = document.getElementById("panel-market-price");
    const elRate = document.getElementById("panel-change-rate");
    const elOpen = document.getElementById("panel-open-price");
    const elHigh = document.getElementById("panel-high-price");
    const elLow = document.getElementById("panel-low-price");
    const elVol = document.getElementById("panel-daily-volume");

    // 2. 값 대입 (숫자는 콤마 포맷팅 적용)
    if (elTicker) elTicker.innerText = data.tickerSymbol || "-";
    if (elName) elName.innerText = data.tokenName || "-";
    if (elPrice) elPrice.innerText = (data.marketPrice || 0).toLocaleString();
    if (elOpen) elOpen.innerText = (data.openPrice || 0).toLocaleString();
    if (elHigh) elHigh.innerText = (data.highPrice || 0).toLocaleString();
    if (elLow) elLow.innerText = (data.lowPrice || 0).toLocaleString();

    // 거래대금은 아까 만든 '백만/만' 포맷팅 함수 적용
    if (elVol) elVol.innerText = formatVolume(data.dailyTradeVolume || 0);

    // 3. 등락률 색상 및 기호 처리
    if (elRate) {
        const rate = Number(data.changeRate) || 0;
        const sign = rate > 0 ? "+" : "";
        elRate.innerText = `${sign}${rate.toFixed(2)}%`;

        // CSS 클래스 적용 (positive, negative 분기)
        elRate.classList.remove("positive", "negative");
        if (rate > 0) {
            elRate.classList.add("positive");
            elRate.style.color = "var(--semantic-red)";
        } else if (rate < 0) {
            elRate.classList.add("negative");
            elRate.style.color = "#1E88E5"; // 파란색 직접 지정하거나 변수 추가
        } else {
            elRate.style.color = "var(--gray-600)";
        }
    }
}

// 특정 행(row)만 찾아서 업데이트
function updateTokenRow(row, data) {
    if (!row || !data) {
        return;
    }

    const priceEl = row.querySelector(".market-price"); // 현재가
    const rateEl = row.querySelector(".change-rate"); // 등락률
    const rateBadge = row.querySelector(".rate-badge"); // 등락률
    const volumeEl = row.querySelector(".daily-volume"); // 거래대금

    let isChanged = false;

    // 현재가
    if (priceEl && data.marketPrice !== undefined) {
        const oldPrice = parseFloat(
            priceEl.innerText.replace(/[^0-9.-]+/g, ""),
        );
        const newPrice = data.marketPrice;

        isChanged = oldPrice !== newPrice;
        priceEl.innerText = newPrice.toLocaleString();
    }

    // 등락률
    if (rateEl && rateBadge && data.changeRate !== undefined) {
        const rate = Number(data.changeRate) || 0;
        const sign = rate > 0 ? "+ " : rate < 0 ? "- " : "";
        const absRate = Math.abs(rate).toFixed(2);

        rateBadge.innerText = sign + absRate + "%";

        rateEl.classList.remove("text-plus", "text-minus");
        if (rate > 0) {
            rateEl.classList.add("text-plus");
            if (isChanged) flashEffect(rateBadge, "up"); // 양수면 무조건 빨간색
        } else if (rate < 0) {
            rateEl.classList.add("text-minus");
            if (isChanged) flashEffect(rateBadge, "down"); // 음수면 무조건 파란색
        }
    }

    // 거래대금
    if (volumeEl && data.dailyTradeVolume !== undefined) {
        volumeEl.setAttribute("data-value", data.dailyTradeVolume);
        volumeEl.innerText = formatVolume(data.dailyTradeVolume);
    }

    reorderTable();
}

// 등락률 변화 이펙트
function flashEffect(el, direction) {
    const bgColor = direction === "up" ? "#ffe2e2" : "#e0efff";

    el.style.transition = "none";
    el.style.backgroundColor = bgColor;

    void el.offsetWidth;

    el.style.transition = "background-color 0.6s ease";
    el.style.backgroundColor = "transparent";
}

// 거래디금 단위
function formatVolume(value) {
    if (!value) return "0";
    const num = parseFloat(value);

    if (num >= 1000000) {
        const million = Math.floor(num / 1000000);
        const thousand = Math.round((num % 1000000) / 10000);

        if (thousand === 0) return million.toLocaleString() + "백만";
        return million.toLocaleString() + "백 " + thousand + "만";
    }

    if (num < 10000) {
        return Math.floor(num).toLocaleString();
    }

    return num.toLocaleString();
}

// 거래대금 순 정렬
function reorderTable() {
    const tbody = document.querySelector(".token_table_main tbody");
    if (!tbody) return;
    const rows = Array.from(tbody.querySelectorAll('tr[id^="token-row-"]'));

    // 1. 행의 현재 위치 기록
    const firstPositions = new Map();
    rows.forEach((row) =>
        firstPositions.set(row.id, row.getBoundingClientRect().top),
    );

    // 2. 정렬 로직
    rows.sort((a, b) => {
        const volA = parseFloat(
            a.querySelector(".daily-volume")?.getAttribute("data-value") || 0,
        );
        const volB = parseFloat(
            b.querySelector(".daily-volume")?.getAttribute("data-value") || 0,
        );
        return volB - volA;
    });

    // 3. DOM 순서 변경 및 순위 업데이트 (Last)
    rows.forEach((row, index) => {
        const rankEl = row.querySelector(".ranking-num");
        const oldRank = rankEl ? parseInt(rankEl.innerText) : 0;
        const newRank = index + 1;

        if (rankEl && oldRank !== newRank) {
            rankEl.innerText = newRank;
            // 순위 숫자 팝 효과
            rankEl.style.display = "inline-block";
            rankEl.style.transition = "transform 0.3s";
            rankEl.style.transform = "scale(1.4)";
            setTimeout(() => (rankEl.style.transform = "scale(1)"), 300);

            // 색상 하이라이트 (상승/하락)
            if (oldRank !== 0) {
                const highlight =
                    oldRank > newRank
                        ? "rank-up-highlight"
                        : "rank-down-highlight";
                row.classList.add(highlight);
                setTimeout(() => row.classList.remove(highlight), 1000);
            }
        }
        tbody.appendChild(row);
    });

    // 4. 역변환 애니메이션 (Invert & Play)
    requestAnimationFrame(() => {
        rows.forEach((row) => {
            const firstTop = firstPositions.get(row.id);
            const lastTop = row.getBoundingClientRect().top;
            const deltaY = firstTop - lastTop;

            if (deltaY !== 0) {
                row.style.transition = "none";
                row.style.transform = `translateY(${deltaY}px)`;

                requestAnimationFrame(() => {
                    row.style.transition =
                        "transform 0.6s cubic-bezier(0.2, 0, 0, 1)";
                    row.style.transform = "translateY(0)";
                });
            }
        });
    });
}

// 리스트
function createNewTokenRow(data) {
    const tr = document.createElement("tr");
    tr.id = `token-row-${data.tokenId}`;

    tr.innerHTML = `
        <td class="tx-center ranking-num">-</td>
        <td>
            <div class="token-name">${data.tokenName}</div>
            <div class="token-code">${data.tickerSymbol}</div>
        </td>
        <td class="tx-right">
            <div class="token-name market-price">0</div>
        </td>
        <td class="tx-right change-rate">
            <span class="rate-badge">0.00%</span>
        </td>
        <td class="token-amount tx-right daily-volume" data-value="0">0</td>
    `;

    return tr;
}

// 차트
export async function renderSideChart(tokenId) {
    const container = document.getElementById("tokenDetailChart");
    if (!container) {
        return;
    }

    container.innerHTML = "";

    // 기존 차트 제거
    if (sideChart) {
        sideChart.remove();
        sideChart = null;
    }

    // 2. 차트 생성
    sideChart = LightweightCharts.createChart(container, {
        width: container.clientWidth,
        height: 180,
        layout: { background: { color: "transparent" }, textColor: "#999" },
        grid: { vertLines: { visible: false }, horzLines: { visible: false } }, // 그리드 제거
        rightPriceScale: { visible: false, borderVisible: false },
        timeScale: { borderVisible: false, visible: false }, // 시간축 숨김 (깔끔하게)
        handleScroll: false,
        handleScale: false, // 줌/스크롤 차단
    });

    sideSeries = sideChart.addSeries(LightweightCharts.CandlestickSeries, {
        upColor: "#E53935",
        downColor: "#1E88E5",
        borderUpColor: "#E53935",
        borderDownColor: "#1E88E5",
        wickUpColor: "#E53935",
        wickDownColor: "#1E88E5",
    });

    try {
        // 최근 50개 캔들만 가져옴
        const response = await TokenApi.getCandles(tokenId, 1); // 5분봉
        if (response && response.length > 0) {
            const chartData = response.map((d) => ({
                time: Number(d.candleTime) / 1000,
                open: Number(d.openingPrice),
                high: Number(d.highPrice),
                low: Number(d.lowPrice),
                close: Number(d.closingPrice),
            }));
            sideSeries.setData(chartData);
            sideChart.timeScale().fitContent();
        }
    } catch (e) {
        console.error("Side Chart Error:", e);
    }
}
