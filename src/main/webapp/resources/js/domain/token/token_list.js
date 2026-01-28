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

    //현재가 업데이트
    const priceEl = row.querySelector('.market-price');
    if (priceEl) {
        // 기존 가격과 비교해서 상승/하락 판단 (반짝임 효과용)
        const oldPrice = parseFloat(priceEl.innerText.replace(/[^0-9.-]+/g, ""));
        const newPrice = data.marketPrice;

        priceEl.innerText = newPrice.toLocaleString();

        // 가격 변동 애니메이션
        if (newPrice > oldPrice) {
            flashEffect(priceEl, 'up');
        } else if (newPrice < oldPrice) {
            flashEffect(priceEl, 'down');
        }
    }

    // 등락률 업데이트
    const rateEl = row.querySelector('.change-rate');
    if (rateEl) {
        const rate = data.changeRate.toFixed(2);
        rateEl.innerText = (rate > 0 ? '+' : '') + rate + '%';

        rateEl.classList.remove('text-plus', 'text-minus');
        rateEl.classList.add(rate > 0 ? 'text-plus' : rate < 0 ? 'text-minus' : '');
    }

    // 거래대금 업데이트
    const volumeEl = row.querySelector('.daily-volume');
    if (volumeEl) {
        volumeEl.setAttribute('data-value', data.dailyTradeVolume);
        volumeEl.innerText = Math.floor(data.dailyTradeVolume).toLocaleString();

        // 가격 및 등락률 텍스트 업데이트
        // priceDiv.innerText = `${newPrice.toLocaleString()} 원`;
        // pctDiv.innerText = `${data.gain > 0 ? '+' : ''}${data.gainPct}%`;
        //
        // // 색상 클래스 갱신
        // const colorClass = data.gain > 0 ? 'text-plus' : data.gain < 0 ? 'text-minus' : '';
        // pctDiv.className = `token-code ${colorClass}`;
        //
        // // 가격 셀 업데이트
        // const priceCell = row.querySelector('.td-price .token-name');
        // if (priceCell) {
        //     priceCell.innerText = Number(data.marketPrice).toLocaleString() + " 원";
        // }
        //
        // // 반짝임 효과 적용
        // if (newPrice > oldPrice) {
        //     priceDiv.classList.remove('up-flash');
        //     void priceDiv.offsetWidth; // 리플로우 강제 발생 (애니메이션 재시작)
        //     priceDiv.classList.add('up-flash');
        // } else if (newPrice < oldPrice) {
        //     priceDiv.classList.remove('down-flash');
        //     void priceDiv.offsetWidth;
        //     priceDiv.classList.add('down-flash');
        // }

        // // 등락률 및 색상 업데이트
        // const pctCell = row.querySelector('.td-pct .token-code');
        // if (pctCell) {
        //     const isPlus = data.gain > 0;
        //     pctCell.className = `token-code ${isPlus ? 'text-plus' : 'text-minus'}`;
        //     pctCell.innerText = `${isPlus ? '+' : ''}${data.gainPct}%`;
        // }
    }
}

// 거래대금 순 정렬
function reorderTable() {
    const tbody = document.querySelector('.token_table_main tbody');
    if (!tbody) {
        return;
    }

    const  rows = Array.from(tbody.querySelectorAll('tr'));

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

function flashEffact(el, direction) {
    const color = direction === 'up' ? 'rgba(255, 68, 68, 0.2)' : 'rgba(0, 102, 255, 0.2)';
    el.style.backgroundColor = color;
    el.style.transition = 'background-color 0.3s';
    setTimeout(() => {
        el.style.backgroundColor = 'transparent';
    }, 300);
}

// document.addEventListener('DOMContentLoaded', fetchStocks);
// window.fetchTokens = fetchTokens;
// // window.connectExchange = connectExchange;
// document.addEventListener('DOMContentLoaded', async () => {
//     await fetchTokens();
//     connectExchange();
// });
