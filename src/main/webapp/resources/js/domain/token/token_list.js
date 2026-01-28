import { TokenApi } from "./token_api.js";

/* 웹소켓 연결 및 구독 */
WebSocketManager.connect('http://localhost:9090/ws-stomp', function () {

    // 1. 전체 토큰 리스트 구독
    WebSocketManager.subscribe('list', '/topic/tokenList', function (data) {
        const row = document.getElementById(`token-row-${data.tokenId}`);
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

    let direction = null;

    // 현재가
    if (priceEl) {
        const oldPrice = parseFloat(priceEl.innerText.replace(/[^0-9.-]+/g, ""));
        const newPrice = data.marketPrice;

        const isChanged = oldPrice !== newPrice;
        priceEl.innerText = newPrice.toLocaleString();
        if (rateEl && rateBadge) {
            const rate = Number(data.changeRate);
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
    if (!tbody) {
        return;
    }

    const  rows = Array.from(tbody.querySelectorAll('tr[id^="token-row-"]'));

    rows.sort((a, b) => {
       const volA = parseFloat(a.querySelector('.daily-volume')?.getAttribute('data-value') || 0);
       const volB = parseFloat(b.querySelector('.daily-volume')?.getAttribute('data-value') || 0);
       return volB - volA;
    });

    rows.forEach((row, index) => {
        tbody.appendChild(row);
        const rankEl = row.querySelector('.ranking-num');
        if (rankEl) rankEl.innerText = index + 1;
    });
}

