/* 웹소켓 연결 및 구독 */
WebSocketManager.connect('http://localhost:9090/ws-stomp', function() {

    // 1. 체결 토픽 구독
    WebSocketManager.subscribe('trade', `/topic/trades/${tokenId}`, function(data) {
        console.log('[WebSocket - 체결]', data);
    });

    // 2. 주문 토픽 구독
    WebSocketManager.subscribe('order', `/topic/orders/${tokenId}`, function(data) {
        console.log('[WebSocket - 호가]', data);
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