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

function updateRightPanel(tokenId) {
    if (!tokenId) {
        return;
    }
    console.log(`${tokenId} 토큰의 차트를 불러옵니다.`);
    // 여기에 Ajax 등으로 mp:token_chart_card의 내용을 갱신하는 로직을 넣으세요.
}
