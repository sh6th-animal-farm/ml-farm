// 초기 로딩 시, 호가창 렌더링
if (window.orderSellList && window.orderBuyList) {
    renderOrderBook(window.orderSellList, window.orderBuyList);
}

// 초기 로딩 시, 체결창 렌더링
if (window.tradeList) {
    renderTradeHistory(window.tradeList);
}

/* 웹소켓 연결 및 구독 */
WebSocketManager.connect('http://localhost:9090/ws-stomp', function() {

    // 1. 체결 토픽 구독
    WebSocketManager.subscribe('trade', `/topic/trades/${tokenId}`, function(data) {
        console.log('[WebSocket - 체결]', data);
        addNewTrade(data);
    });

    // 2. 주문 토픽 구독
    WebSocketManager.subscribe('order', `/topic/orders/${tokenId}`, function(data) {
        console.log('[WebSocket - 호가]', data);
        updateOrderBook(data);
    });
});

/* 틱 주기 토글 */
const periodBtns = document.querySelectorAll('.period-btn');

periodBtns.forEach(btn => {
    btn.addEventListener('click', () => {
        // 기존 active 제거
        periodBtns.forEach(b => b.classList.remove('active'));
        // 클릭한 버튼에 active 추가
        btn.classList.add('active');
    });
});

/* 매수/매도 탭 */
const orderBtns = document.querySelectorAll('.order-btn');

orderBtns.forEach(btn => {
    btn.addEventListener('click', () => {
        // 기존 active 제거
        orderBtns.forEach(b => b.classList.remove('active'));
        // 클릭한 버튼에 active 추가
        btn.classList.add('active');
    });
});

/* 매수/매도 탭 전환 */
document.querySelectorAll('.tab-menu').forEach(tabMenu => {
    tabMenu.addEventListener('click', e => {
        const btn = e.target.closest('.order-btn');
        if (!btn) return;

        const tabId = btn.dataset.tab;
        if (!tabId) return;

        // 버튼 active
        tabMenu.querySelectorAll('.order-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');

        // 탭 내용 전환
        const card = btn.closest('.order-card');
        card.querySelectorAll('.tab-content-wrapper').forEach(tab => tab.classList.remove('active'));
        card.querySelector(`#${tabId}`).classList.add('active');
    });
});

/* 호가/체결 탭 */
const tradeBtns = document.querySelectorAll('.trade-btn');

tradeBtns.forEach(btn => {
    btn.addEventListener('click', () => {
        // 기존 active 제거
        tradeBtns.forEach(b => b.classList.remove('active'));
        // 클릭한 버튼에 active 추가
        btn.classList.add('active');
    });
});

/* 호가/체결 탭 전환 */
document.querySelectorAll('.tab-menu').forEach(tabMenu => {
    tabMenu.addEventListener('click', e => {
        const btn = e.target.closest('.trade-btn');
        if (!btn) return;

        const tabId = btn.dataset.tab;
        if (!tabId) return;

        // 버튼 active
        tabMenu.querySelectorAll('.trade-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');

        // 탭 내용 전환
        const card = btn.closest('.trade-card');
        card.querySelectorAll('.scroll').forEach(tab => tab.classList.remove('active'));
        card.querySelector(`#${tabId}`).classList.add('active');
    });
});

/* 체결창 렌더링 */
function renderTradeHistory(tradeList) {
    const tbody = document.getElementById('trade-hist-body');
    if (!tbody) return;

    if (!tradeList || tradeList.length === 0) {
        tbody.innerHTML = '<tr><td colspan="3" style="text-align:center;">체결 내역이 없습니다.</td></tr>';
        return;
    }

    tradeList.forEach(item => {
        const row = createTradeRowHtml(item);
        tbody.insertAdjacentHTML('beforeend', row); // 목록 맨 아래에 추가
    });
}

/* 체결 내역 HTML 생성 */
function createTradeRowHtml(item) {
    const isSell = item.takerSide === 'SELL';
    const sideText = isSell ? '매도' : '매수';
    const sideClass = isSell ? 'hoga-sell' : 'hoga-buy';
    const dirClass = isSell ? 'sell' : 'buy';

    const formattedPrice = Number(item.price).toLocaleString();
    const formattedVolume = Number(item.volume).toFixed(8);

    return `
        <tr>
            <td class="trade-dir ${dirClass}">${sideText}</td>
            <td class="${sideClass}" style="text-align: right;">${formattedPrice}</td>
            <td style="text-align: right;">${formattedVolume}</td>
        </tr>
    `;
}

/* 웹소켓 체결 내역 추가 */
function addNewTrade(newItem) {
    const tbody = document.getElementById('trade-hist-body');
    if (!tbody) return;

    // "체결 내역이 없습니다" 문구가 있다면 제거
    if (tbody.rows.length === 1 && tbody.rows[0].cells.length === 1) {
        tbody.innerHTML = '';
    }

    const rowHtml = createTradeRowHtml(newItem);
    tbody.insertAdjacentHTML('afterbegin', rowHtml); // 맨 위에 추가

    // 최대 30개만 유지
    if (tbody.rows.length > 30) {
        tbody.lastElementChild.remove();
    }
}

/* 호가창 렌더링 */
function renderOrderBook(sellList, buyList) {
    const tbody = document.getElementById('order-hist-body');
    if (!tbody) return;

    if (sellList.length === 0 && buyList.length === 0) {
        tbody.innerHTML = '<tr><td colspan="3" style="text-align:center;">호가 데이터가 없습니다.</td></tr>';
        return;
    }

    // 매도: 가격 낮은 순 15개 (오름차순)
    const sortedSells = [...sellList].sort((a, b) => a.price - b.price).slice(0, 15);
    // 매수: 가격 높은 순 15개 (내림차순)
    const sortedBuys = [...buyList].sort((a, b) => b.price - a.price).slice(0, 15);

    // 매도 내림차순(reverse) -> 매수 내림차순
    const sellHtml = sortedSells.reverse().map(item => createOrderRowHtml(item, 'SELL')).join('');
    const buyHtml = sortedBuys.map(item => createOrderRowHtml(item, 'BUY')).join('');

    tbody.innerHTML = sellHtml + buyHtml;
}

/* 호가 HTML 생성 */
function createOrderRowHtml(item, type) {
    const isSell = type === 'SELL';
    const sideText = isSell ? '매도' : '매수';
    const sideClass = isSell ? 'hoga-sell' : 'hoga-buy';
    const dirClass = isSell ? 'sell' : 'buy';

    const formattedPrice = Number(item.price).toLocaleString();
    const formattedVolume = Number(item.totalVolume).toFixed(2);

    return `
        <tr>
            <td class="trade-dir ${dirClass}">${sideText}</td>
            <td class="${sideClass}" style="text-align: right;">${formattedPrice}</td>
            <td style="text-align: right;">${formattedVolume}</td>
        </tr>
    `;
}

/* 웹소켓 호가 추가 */
function updateOrderBook(data) {
    // 1. 해당되는 리스트 선택 (BUY 또는 SELL)
    const targetList = data.side === 'BUY' ? window.orderBuyList : window.orderSellList;

    // 2. 메모리 데이터 수정 (수정/삭제)
    updateOrderList(targetList, data);

    // 3. 수정된 데이터를 바탕으로 화면 다시 그리기
    renderOrderBook(window.orderSellList, window.orderBuyList);
}

/* 호가 리스트 수정 (UPDATE or DELETE) */
function updateOrderList(list, data) {
    const index = list.findIndex(item => item.price === data.price);

    if (data.action === 'UPDATE') {
        if (index > -1) {
            // 기존 가격이 있으면 수량 갱신
            list[index].totalVolume = data.updatedVolume;
        } else {
            // 기존 가격이 없으면 새로 추가
            list.push({ price: data.price, totalVolume: data.updatedVolume });
        }
    } else if (data.action === 'DELETE') {
        if (index > -1) {
            // 해당 가격 호가 삭제
            list.splice(index, 1);
        }
    }
}