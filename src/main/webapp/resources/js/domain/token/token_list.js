import { TokenApi } from "./token_api.js";

/* 웹소켓 연결 및 구독 */
WebSocketManager.connect('http://localhost:9090/ws-stomp', function () {

    // 1. 전체 토큰 리스트 구독
    WebSocketManager.subscribe('list', '/topic/tokenList', function (data) {
        let row = document.getElementById(`token-row-${data.tokenId}`);
        if(!row) {
            row = createNewTokenRow(data);
            const tbody = document.querySelector('.token_table_main tbody');
            const emptyRow = tbody.querySelector('td[colspan]');
            if (emptyRow) emptyRow.parentElement.remove();

            tbody.appendChild(row);
        }
        updateTokenRow(row, data);
    });

    // 2. 우측 패널용 캔들 토픽 구독
    // WebSocketManager.subscribe('trade', `/topic/candles/${tokenId}`, function(data) {
    //     // 실시간 정보 업데이트
    //     //updateTokenRow(data);
    //     console.log('[WebSocket - 캔들]', data);
    // });
});

// 특정 행(row)만 찾아서 업데이트
function updateTokenRow(row, data) {

    const priceEl = row.querySelector('.market-price');     // 현재가
    const rateEl = row.querySelector('.change-rate');       // 등락률
    const rateBadge = row.querySelector('.rate-badge');     // 등락률
    const volumeEl = row.querySelector('.daily-volume');    // 거래대금

    // 현재가
    if (priceEl) {
        const oldPrice = parseFloat(priceEl.innerText.replace(/[^0-9.-]+/g, ""));
        const newPrice = data.marketPrice;
        const isChanged = oldPrice !== newPrice;
        priceEl.innerText = newPrice.toLocaleString();

        if (rateEl && rateBadge) {
            const rate = Number(data.changeRate) || 0;

            rateBadge.innerText = (rate > 0 ? '+' : '') + rate.toFixed(2) + '%';
            rateEl.classList.remove('text-plus', 'text-minus');

            if (rate > 0) {
                rateEl.classList.add('text-plus');
                if (isChanged) flashEffect(rateBadge, 'up'); // 양수면 무조건 빨간색
            } else if (rate < 0) {
                rateEl.classList.add('text-minus');
                if (isChanged) flashEffect(rateBadge, 'down'); // 음수면 무조건 파란색
            }
        }

    }

    // 거래대금
    if (volumeEl) {
        volumeEl.setAttribute('data-value', data.dailyTradeVolume);
        volumeEl.innerText = formatVolume(data.dailyTradeVolume);
    }

    reorderTable();
}

function flashEffect(el, direction) {
    const bgColor = direction === 'up' ? '#ffe2e2' : '#e0efff';

    el.style.transition = 'none';
    el.style.backgroundColor = bgColor;

    void el.offsetWidth;

    el.style.transition = 'background-color 0.6s ease';
    el.style.backgroundColor = 'transparent';
}

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
    const tbody = document.querySelector('.token_table_main tbody');
    if (!tbody) return;
    const rows = Array.from(tbody.querySelectorAll('tr[id^="token-row-"]'));

    // 1. 현재 화면상의 Top 위치 기록 (First)
    const firstPositions = new Map();
    rows.forEach(row => firstPositions.set(row.id, row.getBoundingClientRect().top));
    // 2. 정렬 로직
    rows.sort((a, b) => {
        const volA = parseFloat(a.querySelector('.daily-volume')?.getAttribute('data-value') || 0);
        const volB = parseFloat(b.querySelector('.daily-volume')?.getAttribute('data-value') || 0);
        return volB - volA;
    });
    // 3. DOM 순서 변경 및 순위 업데이트 (Last)
    rows.forEach((row, index) => {
        const rankEl = row.querySelector('.ranking-num');
        const oldRank = rankEl ? parseInt(rankEl.innerText) : 0;
        const newRank = index + 1;

        if (rankEl && oldRank !== newRank) {
            rankEl.innerText = newRank;
            // 순위 숫자 팝 효과
            rankEl.style.display = 'inline-block';
            rankEl.style.transition = 'transform 0.3s';
            rankEl.style.transform = 'scale(1.4)';
            setTimeout(() => rankEl.style.transform = 'scale(1)', 300);

            // 색상 하이라이트 (상승/하락)
            if (oldRank !== 0) {
                const highlight = oldRank > newRank ? 'rank-up-highlight' : 'rank-down-highlight';
                row.classList.add(highlight);
                setTimeout(() => row.classList.remove(highlight), 1000);
            }
        }
        tbody.appendChild(row);
    });
    // 4. 역변환 애니메이션 (Invert & Play)
    requestAnimationFrame(() => {
        rows.forEach(row => {
            const firstTop = firstPositions.get(row.id);
            const lastTop = row.getBoundingClientRect().top;
            const deltaY = firstTop - lastTop;

            if (deltaY !== 0) {
                row.style.transition = 'none';
                row.style.transform = `translateY(${deltaY}px)`;

                requestAnimationFrame(() => {
                    row.style.transition = 'transform 0.6s cubic-bezier(0.2, 0, 0, 1)';
                    row.style.transform = 'translateY(0)';
                });
            }
        });
    });
}

function createNewTokenRow(data) {
    const tr = document.createElement('tr');
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

    tr.addEventListener('click', () => {
        location.href = `/token/${data.tokenId}`;
    });

    return tr;
}

// list hover 시 우측 패널 업데이트
document.querySelectorAll('.token_table_main tbody tr[id^="token-row-"]').forEach(row => {
    let hoverTimer;
    const tokenId = row.id.split('-').pop();

    row.addEventListener('mouseenter', () => {
        hoverTimer = setTimeout(() => {
            updateRightPanel(tokenId);
        }, 300);
    });

    row.addEventListener('mouseleave', () => {
        clearTimeout(hoverTimer);
    });

    row.addEventListener('click', () => {
        location.href = `/token/${tokenId}`;
    });
});

// 우측 패널 정보
async function updateRightPanel(tokenId) {
    if (!tokenId) {
        return;
    }
    console.log(`${tokenId} 토큰의 차트를 불러옵니다.`);

    try {
        const response = await TokenApi.getCandle(tokenId, {
            unit: 1,
            start: 0,
            end: Math.floor(Date.now() / 1000)
        });

        const candleCsvList = response.data || response.payload;
        const panel = document.querySelector('.token_chart_card');

        if (panel && candleCsvList && candleCsvList.length > 0) {
            // 2. 최신 캔들 데이터(리스트의 마지막) 추출
            const lastRow = candleCsvList[candleCsvList.length - 1];

            // CSV 파싱: [시간, unit, 시, 고, 저, 종, 양]
            const [time, unit, open, high, low, close, vol] = lastRow.split(',');

            // 3. 데이터 바인딩 (성능을 위해 파싱하며 즉시 할당)
            const currentPrice = parseFloat(close);
            const highPrice = parseFloat(high);
            const lowPrice = parseFloat(low);
            const volume = parseFloat(vol);

            // 현재가 업데이트
            panel.querySelector('.current-price').innerText = currentPrice.toLocaleString();

            // 고가/저가/거래량
            panel.querySelector('.high-price').innerText = highPrice.toLocaleString();
            panel.querySelector('.low-price').innerText = lowPrice.toLocaleString();
            panel.querySelector('.trade-volume').innerText = volume.toLocaleString();

            /* 주의: tickerSymbol, changeRate 등은 캔들 데이터에 포함되지 않습니다.
               이 정보들은 '토큰 리스트'에서 이미 가지고 있는 데이터를 쓰거나
               별도의 상세 정보 API(Snapshot)를 호출해야 합니다.
            */

            // 임시: 만약 다른 데이터 소스(예: 리스트에서 선택된 데이터)가 있다면 병합
            if (window.selectedTokenData) {
                const data = window.selectedTokenData;
                panel.querySelector('.token-title').innerText = data.tickerSymbol;
                panel.querySelector('.token-desc').innerText = data.tokenName;

                // 등락률 계산 예시 (오늘 시가 기준)
                const openPrice = parseFloat(open);
                const changeRate = ((currentPrice - openPrice) / openPrice) * 100;

                const rateEl = panel.querySelector('.current-rate');
                rateEl.innerText = (changeRate > 0 ? '+' : '') + changeRate.toFixed(2) + '%';
                // 클래스 제어 로직 동일...
            }
        }
        // if (panel && data) {
        //
        //     panel.querySelector('.token-title').innerText = data.tickerSymbol;
        //     panel.querySelector('.token-desc').innerText = data.tokenName;
        //
        //     const priceEl = panel.querySelector('.current-price');
        //     const rateEl = panel.querySelector('.current-rate');
        //
        //     rateEl.innerText = (data.changeRate > 0 ? '+' : '') + data.changeRate.toFixed(2) + '%';
        //     rateEl.classList.remove('text-plus', 'text-minus', 'text-zero');
        //     if (data.changeRate > 0) {
        //         rateEl.classList.add('text-plus');
        //     } else if (data.changeRate < 0) {
        //         rateEl.classList.add('text-minus');
        //     } else {
        //         rateEl.classList.add('text-zero');
        //     }
        //
        //     panel.querySelector('.prev-close').innerText = data.prevClose.toLocaleString();
        //     panel.querySelector('.high-price').innerText = data.highPrice.toLocaleString();
        //     panel.querySelector('.low-price').innerText = data.lowPrice.toLocaleString();
        //     panel.querySelector('.trade-volume').innerText = data.tradeVolume.toLocaleString();
        // }
    } catch (err) {
        console.log("로딩 실패: ", err);
    }
}


// let currentChart = null;
// let candleSeries = null;
//
// function initChart() {
//     const container = document.getElementById('tv_chart_container'); // HTML에 이 ID의 div가 있어야 함
//     if (!container) return;
//
//     currentChart = LightweightCharts.createChart(container, {
//         width: container.clientWidth,
//         height: 400,
//         layout: { backgroundColor: '#ffffff', textColor: '#333' },
//         timeScale: { timeVisible: true, secondsVisible: false }
//     });
//     candleSeries = currentChart.addCandlestickSeries();
// }
//
// // 페이지 로드 시 초기화
// document.addEventListener('DOMContentLoaded', initChart);