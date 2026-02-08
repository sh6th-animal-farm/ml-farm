import { TokenApi } from "./token_api.js";

const tokenId = window.tokenId;
const norm = (p) => Number(Number(p || 0).toFixed(8));
const KST_OFFSET = 9 * 60 * 60;

let candleSeries; // 캔들
let volumeSeries; // 거래량
let isChartReady = false; // 차트 초기화
let isPageExiting = false;
let tokenList = []; // 토큰 목록

// 페이지 로드 후 실행
document.addEventListener('DOMContentLoaded', () => {

    // 토큰 목록 행에 클릭 이벤트 추가
    const tbody = document.getElementById('token-list-body');
    tbody.addEventListener('click', (event) => {
        const row = event.target.closest('tr');

        if (row && row.id && row.id.startsWith('token-row-')) {
            const tokenId = row.id.replace('token-row-', '');
            location.href = `/token/${tokenId}`; // 상세 페이지로 이동
        }
    });

    // 토큰 목록 그리기
    TokenListManager.init();
});

// 페이지를 떠나기 직전에 플래그를 true로 바꾸고 alert을 무력화
window.addEventListener('beforeunload', () => {
    isPageExiting = true;

    // 브라우저의 alert 함수를 빈 함수로 덮어씌워 버림 (강력 차단)
    window.alert = function () {
        console.warn("페이지 이동 중 발생한 alert 무시됨:", arguments[0]);
    };
});

// 캔들 데이터 변환 (과거 데이터용, 배열)
const transformCandleData = (data) => {
    return data.map(data => transformSingleCandle(data));
}

// 캔들 데이터 변환 (실시간 데이터용, 객체 하나)
export const transformSingleCandle = (data) => ({
    time: Number(data.candleTime) + KST_OFFSET,
    open: Number(data.openingPrice),
    high: Number(data.highPrice),
    low: Number(data.lowPrice),
    close: Number(data.closingPrice)
});

// 거래량 데이터 변환 (과거 데이터용, 배열)
const transformVolumeData = (data) => {
    return data.map(item => transformSingleVolume(item));
};

// 거래량 데이터 변환 (실시간 데이터용, 객체 하나)
const transformSingleVolume = (data) => ({
    time: Number(data.candleTime) + KST_OFFSET,
    value: Number(data.tradeVolume || 0),
    color: Number(data.closingPrice) >= Number(data.openingPrice)
        ? 'rgba(209,47,47,0.3)'
        : 'rgba(25,117,208,0.3)'
});

// 종목 리스트 변환 (실시간 데이터용, 배열)
const TokenListManager = {
    cache: new Map(),
    tbody: null,

    init() {
        this.tbody = document.getElementById('token-list-body');
        // DB 조회를 통해 HTML에 그려진 기존 행들을 캐시에 등록
        document.querySelectorAll('#token-list-body tr').forEach(row => {
            const id = row.id.replace('token-row-', '');
            this.cache.set(id.toString(), row);
        });
    },

    sync(data) {
        let row = this.cache.get(data.tokenId.toString());

        if (!row) {
            // 1. 목록에 없으면 새로 생성
            row = this.createNewRow(data);
            this.cache.set(data.tokenId.toString(), row);
            this.tbody.appendChild(row); // 일단 맨 뒤에 붙이고 reorder에서 정렬
        }

        // 2. 데이터 업데이트
        this.updateUI(row, data);

        // 3. 거래대금 순 정렬
        this.reorder(row, data.dailyTradeVolume);
    },

    createNewRow(data) {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>
                <span class="token-name">${data.tokenName}</span>
                <span class="token-code">${data.tokenCode}</span>
            </td>
            <td class="price" style="text-align: right;"></td>
            <td class="rate" style="text-align: right;"></td>
        `;
        return tr;
    },

    updateUI(row, data) {
        const priceTd = row.querySelector('.price');
        const rateTd = row.querySelector('.rate');

        const isUp = data.changeRate > 0;
        const color = isUp ? 'var(--error)' : 'var(--info)';

        // 현재가 업데이트 (천단위 콤마)
        priceTd.innerText = Number(data.marketPrice).toLocaleString();

        // 등락률 업데이트
        updateRateUI(rateTd, data.changeRate);
        rateTd.style.color = color;

        // 정렬 기준값(거래대금) 업데이트
        row.dataset.volume = data.dailyTradeVolume;
    },

    reorder(row, currentVolume) {
        const rows = Array.from(this.tbody.querySelectorAll('tr'));

        // 나보다 거래대금이 작은 첫 번째 행을 찾음
        const target = rows.find(r => {
            if (r === row) return false;
            return parseFloat(r.dataset.volume || 0) < parseFloat(currentVolume);
        });

        if (target) {
            // 나보다 작은 행이 있으면 그 행 바로 위에 삽입
            this.tbody.insertBefore(row, target);
        } else {
            // 내가 작거나 목록에 나밖에 없으면 맨 뒤에 삽입
            this.tbody.appendChild(row);
        }
    }
};

// Ticker 상태 관리 객체
const tickerState = {
    referencePrice: 0, // 전일 종가
    lastPrice: 0,
    elements: null
};

// 초기화 시 DOM 캐싱
function initTickerElements() {
    if (tickerState.elements) {
        return;
    }
    tickerState.elements = {
        container: document.querySelector('.price-header'),
        price: document.querySelector('.current-price .price'),
        rate: document.querySelector('.change-rate'),
        high: document.querySelector('.info-row span:nth-child(1) b'),
        low: document.querySelector('.info-row span:nth-child(2) b'),
        volume: document.querySelector('.info-row span:nth-child(3) b')
    };
}

/* 웹소켓 연결 및 구독 */
WebSocketManager.connect(TokenApi.WS_CONN, function () {

    // 1. 체결 토픽 구독
    WebSocketManager.subscribe('trade', `/topic/trades/${tokenId}`, function (data) {
        console.log('[WebSocket - 체결]', data);
        addNewTrade(data);
    });

    // 2. 주문 토픽 구독
    WebSocketManager.subscribe('order', `/topic/orders/${tokenId}`, function (data) {
        console.log('[WebSocket - 호가]', data);
        updateOrderBook(data);
    });

    // 3. 캔들 토픽 구독
    WebSocketManager.subscribe('candle', `/topic/candles/${tokenId}`, function (data) {
        console.log('[WebSocket - 캔들]', data);
        // 실시간 캔들 데이터 갱신
        if (!isChartReady || !candleSeries) {
            return;
        }
        candleSeries.update(transformSingleCandle(data));
        volumeSeries.update(transformSingleVolume(data));
    });

    // 4. OHLCV 구독
    WebSocketManager.subscribe('ohlcv', `/topic/tokenList/${tokenId}`, function (data) {
        console.log('[WebSocket - OHLCV]', data);
        syncTicker(data);
    });

    // 5. 전체 토큰 리스트 구독
    WebSocketManager.subscribe('tokenList', `/topic/tokenList`, function (data) {
        console.log('[WebSocket - 토큰 목록]', data);
        TokenListManager.sync(data);
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

/* 매수, 매도, 미체결 탭 */
const orderBtns = document.querySelectorAll('.order-btn');

orderBtns.forEach(btn => {
    btn.addEventListener('click', () => {
        orderBtns.forEach(b => b.classList.remove('active'));
        btn.classList.add('active');

        const tabId = btn.dataset.tab;

        if (tabId === 'history-tab') {
            fetchPendingOrders(); // 미체결 내역 조회
        }
    });
});

/* 매수, 매도, 미체결 탭 전환 */
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

/* 시장가, 지정가 인풋박스 */
document.querySelectorAll('.order-type').forEach(select => {
    select.addEventListener('change', function () {
        const tabContent = this.closest('.tab-content');
        const isBuyTab = this.closest('#buy-tab') !== null;
        const orderType = this.value; // LIMIT 또는 MARKET

        const priceGroup = tabContent.querySelector('.price-group');
        const volumeGroup = tabContent.querySelector('.volume-group');
        const amountGroup = tabContent.querySelector('.amount-group');

        if (orderType === 'MARKET') {
            if (isBuyTab) {
                // 시장가 매수: 가격, 수량 X / 총액 O
                priceGroup.style.display = 'none';
                volumeGroup.style.display = 'none';
                if (amountGroup) amountGroup.style.display = 'flex';
            } else {
                // 시장가 매도: 가격, 총액 X / 수량 O
                priceGroup.style.display = 'none';
                volumeGroup.style.display = 'flex';
                if (amountGroup) amountGroup.style.display = 'none';
            }
        } else {
            // 지정가: 가격, 수량 O / 총액 X
            priceGroup.style.display = 'flex';
            volumeGroup.style.display = 'flex';
            amountGroup.style.display = 'none';
        }

        // 매수/매도 탭 변경 시, 총 주문 금액/수량 재계산
        updateOrderTotal(tabContent);
    });
});

// 총 주문 금액/수량 계산
function updateOrderTotal(tabContent) {
    const isBuyTab = tabContent.closest('#buy-tab') !== null;
    const orderType = tabContent.querySelector('.order-type').value;
    const totalDisplay = tabContent.querySelector('.total-amount');

    const getValue = (selector) => {
        const input = tabContent.querySelector(selector);
        return input ? parseFloat(input.value.replace(/,/g, '')) || 0 : 0;
    };

    if (isBuyTab) {
        if (orderType === 'MARKET') {
            // 시장가 매수: 사용자가 입력한 '주문 총액' 그대로
            const amount = getValue('.amount-group .input-box');
            totalDisplay.textContent = `${Math.floor(amount).toLocaleString()}원`;
        } else {
            // 지정가 매수: 가격 * 수량
            const total = getValue('.price-group .input-box') * getValue('.volume-group .input-box');
            totalDisplay.textContent = `${Math.floor(total).toLocaleString()}원`;
        }
    } else {
        // 매도 탭: 시장가/지정가 상관없이 '주문 수량' 표시
        const volume = getValue('.volume-group .input-box');
        totalDisplay.textContent = `${volume.toLocaleString()}`;
    }
}

// 인풋박스 입력 시 실시간 계산 및 업데이트
document.querySelectorAll('.tab-content input.input-box').forEach(input => {
    input.addEventListener('input', function () {
        const start = this.selectionStart;
        const prevLen = this.value.length;

        if (typeof handleNumericInput === 'function') {
            handleNumericInput(this);
        }

        const currLen = this.value.length;

        if (document.activeElement === this && this.offsetWidth > 0) {
            try {
                const newPos = start + (currLen - prevLen);
                this.setSelectionRange(newPos, newPos);
            } catch (e) {
                // 혹시 모를 브라우저 예외에도 스크립트가 죽지 않게 보호
                console.warn("커서 위치 조정 실패:", e);
            }
        }

        const tabContent = this.closest('.tab-content');
        if (typeof updateOrderTotal === 'function') {
            updateOrderTotal(tabContent);
        }
    });
});

/* % 버튼 */
document.querySelectorAll('.perc-btn').forEach(button => {
    button.addEventListener('click', (e) => {
        const percentage = parseInt(e.target.innerText); // 버튼에서 숫자만 추출 (예: "25%" -> 25)
        fetchBalance(percentage);
    });
});

/* % 버튼 클릭 시, 자동 수량 계산 */
async function fetchBalance(perc) {
    try {
        const activeTab = document.querySelector('.tab-content-wrapper.active');
        const isBuyTab = activeTab.id === 'buy-tab';
        const totalDisplay = activeTab.querySelector('.total-amount');

        let balance = 0;
        if (isBuyTab) {
            // 매수: 주문 가능 금액
            balance = await TokenApi.getCashBalance();
        } else {
            // 매도: 보유 토큰 수량
            balance = await TokenApi.getTokenBalance(window.tokenId);
        }
        console.log("잔액: ", balance);

        const calculatedAmount = (balance * (perc / 100)).toFixed(4);

        const orderType = activeTab.querySelector('.order-type').value;

        const priceInput = activeTab.querySelector('.price-group .input-box');
        const volumeInput = activeTab.querySelector('.volume-group .input-box');
        const amountInput = activeTab.querySelector('.amount-group .input-box');

        if (orderType === 'MARKET') {
            if (isBuyTab) {
                // 시장가 매수: 주문 총액 초기화
                amountInput.value = calculatedAmount;
                totalDisplay.textContent = `${calculatedAmount} KRW`;
            } else {
                // 시장가 매도: 주문 수량 초기화
                volumeInput.value = calculatedAmount;
                totalDisplay.textContent = calculatedAmount;
            }
        } else {
            const price = parseFloat(priceInput.value.replace(/,/g, ''));

            if (isBuyTab) {
                // 지정가 매수: 주문 수량 초기화
                volumeInput.value = (calculatedAmount / price).toFixed(4);
                totalDisplay.textContent = `${calculatedAmount} KRW`;
            } else {
                // 지정가 매도: 주문 수량 초기화
                if (volumeInput) volumeInput.value = calculatedAmount;
                totalDisplay.textContent = calculatedAmount;
            }
        }
    } catch (error) {
        console.error("잔액 조회 실패:", error);
    }
}

/* 매수, 매도 주문 */
document.querySelector('.btn-buy').addEventListener('click', (e) => handleOrder(e, 'BUY'));
document.querySelector('.btn-sell').addEventListener('click', (e) => handleOrder(e, 'SELL'));

/* 매수, 매도 주문 처리 */
async function handleOrder(event, side) {
    const container = event.target.closest('.tab-content-wrapper');
    const orderType = container.querySelector('.order-type').value; // LIMIT or MARKET

    const getValue = (selector) => {
        const element = container.querySelector(selector);
        return (element && element.value) ? Number(element.value.replace(/,/g, '')) : 0;
    };

    const price = getValue('.price-group input');
    const volume = getValue('.volume-group input');
    const amount = getValue('.amount-group input');

    const orderDTO = {
        tokenId: window.tokenId,
        orderSide: side,
        orderType: orderType,
        orderPrice: orderType === 'LIMIT' ? price : 0,  // 지정가는 주문가격, 시장가는 0
        orderVolume: (side === 'BUY' && orderType === 'MARKET') ? 0 : volume, // 시장가 매수는 0, 그 외는 주문수량
        totalPrice: (side === 'BUY') ? (orderType === 'LIMIT' ? price * volume : amount) : 0 // 지정가 매수는 가격*수량, 시장가 매수는 총액, 매도는 0
    };

    // 검증 실행
    const check = validateOrder(orderDTO);
    if (!check.valid) {
        ToastManager.show(check.msg);
        return;
    }

    // API 호출
    try {
        const result = await TokenApi.createOrder(orderDTO.tokenId, orderDTO);
        if (result) {
            ToastManager.show("주문이 완료되었습니다.");
        }
    } catch (e) {
        ToastManager.show(e.message);
        console.error("주문 실패: ", e);
    }
};

/* 주문 검증 */
function validateOrder(dto) {
    const { orderSide, orderType, orderPrice, orderVolume, totalPrice } = dto;

    // 1. 계좌 연동 여부 검증
    // -> 서비스 함수에서 처리
    // if (!dto.walletId) {
    //     return { valid: false, msg: "계좌 연동이 필요한 서비스입니다." };
    // }

    // 2. 숫자 형식 및 음수 체크
    if (isNaN(orderPrice) || isNaN(orderVolume) || isNaN(totalPrice) ||
        orderPrice < 0 || orderVolume < 0 || totalPrice < 0) {
        return { valid: false, msg: "가격 또는 수량을 정확히 입력해주세요." };
    }

    // 3. 매수 조건 검증
    if (orderSide === 'BUY') {
        let finalBuyAmount = 0;

        if (orderType === 'LIMIT') {
            // 지정가 매수: 가격 * 수량으로 총액 계산
            if (orderPrice <= 0) {
                return { valid: false, msg: "가격을 입력해주세요." };
            } else if (orderVolume <= 0) {
                return { valid: false, msg: "수량을 입력해주세요." };
            }

            finalBuyAmount = orderPrice * orderVolume;
        } else {
            // 시장가 매수: 입력된 총액(totalPrice) 그대로 사용
            finalBuyAmount = totalPrice;
        }

        // 최소 금액 1,000원 검증
        if (finalBuyAmount < 1000) {
            return { valid: false, msg: "최소 주문 금액은 1,000원입니다." };
        }
    }

    // 4. 매도 조건 검증
    else if (orderSide === 'SELL') {
        // 지정가 매도 시 가격 입력 체크
        if (orderType === 'LIMIT' && orderPrice <= 0) {
            return { valid: false, msg: "가격을 입력해주세요." };
        }

        // 최소 수량 0.00001개 검증 (시장가/지정가 공통)
        if (orderVolume < 0.00001) {
            return { valid: false, msg: "최소 주문 수량은 0.00001개입니다." };
        }
    }

    return { valid: true };
};

/* 미체결 내역 조회 */
async function fetchPendingOrders() {
    try {
        const data = await TokenApi.getPendingList(window.tokenId);
        renderPendingOrders(data);
    } catch (error) {
        console.error("미체결 내역 조회 실패:", error);
    }
}

/* 미체결 내역 렌더링 */
function renderPendingOrders(pendingList) {
    const listContainer = document.querySelector('.transaction-list');
    const summaryCount = document.querySelector('.history-summary div:first-child');

    if (!listContainer) return;

    // 1. 총 건수 업데이트
    if (summaryCount) {
        summaryCount.innerText = `총 ${pendingList.length}건`;
    }

    // 2. 리스트가 비었을 경우 처리
    if (!pendingList || pendingList.length === 0) {
        listContainer.innerHTML = '<li class="transaction-item no-content">미체결 내역이 없습니다.</li>';
        return;
    }

    // 3. 리스트 생성
    const html = pendingList.map(item => createPendingRowHtml(item)).join('');

    listContainer.innerHTML = html;
}

/* 미체결 내역 Html 생성 */
function createPendingRowHtml(item) {
    const isBuy = item.orderSide === 'BUY';
    const sideText = isBuy ? '매수' : '매도';
    const sideClass = isBuy ? 'buy' : 'sell';

    // 날짜 포맷 (2026-01-25 19:09:55 형식)
    const date = new Date(item.createdAt);
    const formattedDate = date.getFullYear() + '-' +
        String(date.getMonth() + 1).padStart(2, '0') + '-' +
        String(date.getDate()).padStart(2, '0') + ' ' +
        String(date.getHours()).padStart(2, '0') + ':' +
        String(date.getMinutes()).padStart(2, '0') + ':' +
        String(date.getSeconds()).padStart(2, '0');

    return `
            <li class="transaction-item" data-order-id="${item.orderId}">
                <div class="item-hover-layer">
                    <div class="trashcan-box" onclick="cancelOrder(${item.orderId})">
                        ${Icons.trashCan}
                    </div>
                </div>

                <div class="trade-title">
                    <div class="item-header">
                        <span class="asset-name">HSSJ01/KRW</span>
                        <span class="trade-type ${sideClass}">${sideText}</span>
                    </div>
                    <div class="trade-date">${formattedDate}</div>
                </div>

                <div class="trade-info">
                    <div class="trade-info-row">
                        <span class="label">주문가격</span>
                        <span class="value">${Number(item.orderPrice).toLocaleString()}</span>
                    </div>
                    <div class="trade-info-row">
                        <span class="label">주문수량</span>
                        <span class="value">${Number(item.orderVolume).toFixed(4)}</span>
                    </div>
                    <div class="trade-info-row">
                        <span class="label">미체결량</span>
                        <span class="value">${Number(item.remainingToken).toFixed(4)}</span>
                    </div>
                </div>
            </li>
        `;
}

/* 주문 취소 */
async function cancelOrder(orderId) {
    try {
        await TokenApi.cancelOrder(window.tokenId, orderId);
        ToastManager.show("주문 취소");
        fetchPendingOrders();
    } catch (e) {
        ToastManager.show("취소 실패");
        console.error("취소 실패: ", e);
    }
}

/* 호가, 체결 탭 */
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
        tbody.innerHTML = '<tr><td colspan="3" class="no-content">체결 내역이 없습니다.</td></tr>';
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
    const formattedVolume = Number(item.volume).toFixed(4);

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

    const tradePrice = norm(newItem.price);
    if (norm(window.ohlcv.marketPrice) !== tradePrice) {
        window.ohlcv.marketPrice = tradePrice; // 시장가 갱신

        if (typeof renderOrderBook === 'function') {
            renderOrderBook(window.orderSellList, window.orderBuyList);
        }
    }

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
        tbody.innerHTML = '<tr><td colspan="3" class="no-content">호가 정보가 없습니다.</td></tr>';
        return;
    }

    const marketPrice = Number(window.ohlcv.marketPrice || 0);
    const tickSize = getTickSize(marketPrice);
    const basePrice = norm(Math.round(marketPrice / tickSize) * tickSize);

    const sellMap = new Map();
    sellList.forEach(s => {
        const p = norm(s.price);
        sellMap.set(p, (sellMap.get(p) || 0) + Number(s.totalVolume));
    });
    const buyMap = new Map();
    buyList.forEach(b => {
        const p = norm(b.price);
        buyMap.set(p, (buyMap.get(p) || 0) + Number(b.totalVolume));
    });

    const ladder = [];

    // 매도 15개 (위로 상승)
    for (let i = 15; i >= 1; i--) {
        const p = norm(basePrice + (i * tickSize));
        if (p > 0) {
            ladder.push({ price: p, volume: sellMap.get(p) || 0, type: 'SELL' });
        } else {
            ladder.push({ price: null, volume: 0, type: 'EMPTY' });
        }
    }

    const sVol = sellMap.get(basePrice) || 0;
    const bVol = buyMap.get(basePrice) || 0;

    // 현재가
    ladder.push({
        price: basePrice,
        volume: sVol || bVol || 0,
        type: 'CURRENT',
        actualSide: sVol > 0 ? 'SELL' : (bVol > 0 ? 'BUY' : (basePrice >= marketPrice ? 'SELL' : 'BUY'))
    });

    // 매수 15개 (아래로 하락)
    for (let i = 1; i <= 15; i++) {
        const p = norm(basePrice - (i * tickSize));
        if (p > 0) {
            ladder.push({ price: p, volume: buyMap.get(p) || 0, type: 'BUY' });
        } else {
            ladder.push({ price: null, volume: 0, type: 'EMPTY' });
        }
    }

    // 물량 바 최대치 계산
    const maxVol = Math.max(...ladder.map(r => r.volume || 0), 0.0001);
    tbody.innerHTML = ladder.map(row => createOrderRowHtml(row, maxVol)).join('');
}

/* 호가 행 생성 */
function createOrderRowHtml(row, maxVol) {

    if (row.type === 'EMPTY' || row.price === null) {
        return `<tr class="order-hist-row empty-row"><td></td><td>-</td><td></td></tr>`;
    }

    const isCurrent = row.type === 'CURRENT';
    const marketPrice = norm(window.ohlcv.marketPrice);

    const isSell = row.type === 'SELL' || (isCurrent && row.actualSide === 'SELL');
    const sideClass = isSell ? 'hoga-sell' : 'hoga-buy';

    const ratio = (row.volume / maxVol).toFixed(4);
    const depthColor = isSell ? 'var(--info-light)' : 'var(--error-light)';

    const formattedPrice = Number(row.price).toLocaleString();
    const formattedVolume = row.volume > 0 ? Number(row.volume).toFixed(4) : '';

    return `
        <tr class="order-hist-row ${isSell ? 'sell-row' : 'buy-row'} ${isCurrent ? 'is-current' : ''}" 
            style="--depth-ratio: ${ratio}; --depth-color: ${depthColor};"
            onclick="selectPrice('${row.price}')">
            <td>${isSell ? formattedVolume : ''}</td>
            <td class="${sideClass}">${formattedPrice}</td>
            <td>${!isSell ? formattedVolume : ''}</td>
        </tr>
    `;
}

/* 호가 클릭 시 가격 자동 주입 (기존 782~795라인 리스너 대체) */
function selectPrice(price) {
    const priceInputs = document.querySelectorAll('.price-group .input-box');
    priceInputs.forEach(input => {
        input.value = Number(price).toLocaleString();
        // 기존에 만든 콤마 포맷팅 함수 활용
        if (typeof handleNumericInput === 'function') handleNumericInput(input);
    });
    // 주문 총액 자동 재계산
    document.querySelectorAll('.tab-content').forEach(tab => {
        if (typeof updateOrderTotal === 'function') updateOrderTotal(tab);
    });
}

/* 호가 HTML 생성
function createOrderRowHtml(item, type) {
    const isSell = type === 'SELL';
    const sideText = isSell ? '매도' : '매수';
    const sideClass = isSell ? 'hoga-sell' : 'hoga-buy';
    const dirClass = isSell ? 'sell' : 'buy';

    const formattedPrice = Number(item.price).toLocaleString();
    const formattedVolume = Number(item.totalVolume).toFixed(4);

    return `
        <tr class="order-hist-row">
            <td class="trade-dir ${dirClass}">${sideText}</td>
            <td class="${sideClass}" style="text-align: right;">${formattedPrice}</td>
            <td style="text-align: right;">${formattedVolume}</td>
        </tr>
    `;
}*/

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
    const targetPrice = norm(data.price);
    const index = list.findIndex(item => norm(item.price) === targetPrice);

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

// 호가창 클릭 시, 주문 가격을 해당 가격으로
document.getElementById('order-hist-body')?.addEventListener('click', (e) => {
    const row = e.target.closest('tr');
    if (row) {
        // 가격 추출
        const priceText = row.cells[1].innerText.replace(/,/g, '');
        const priceInputs = document.querySelectorAll('.price-group .input-box');
        priceInputs.forEach(input => {
            input.value = Number(priceText).toLocaleString();
        });

        // 주문 총액 재계산
        document.querySelectorAll('.tab-content').forEach(tab => updateOrderTotal(tab));
    }
});

// OHLCV
function syncTicker(data) {
    if (!tickerState.elements) initTickerElements();

    const current = Number(data.marketPrice || 0);
    const high = Number(data.highPrice || 0);
    const low = Number(data.lowPrice || 0);
    const rate = Number(data.changeRate || 0);
    const volume = Number(data.dailyTradeVolume || 0);

    const { elements, lastPrice } = tickerState;
    if (!elements || !elements.price) return;

    // 가격 변화 애니메이션
    if (lastPrice !== 0 && lastPrice !== current) {
        const flashClass = current > lastPrice ? 'up-flash' : 'down-flash';
        elements.price.classList.add(flashClass);
        setTimeout(() => elements.price.classList.remove(flashClass), 300);
    }

    // 값 업데이트
    elements.price.innerText = current.toLocaleString();
    elements.high.innerText = high.toLocaleString();
    elements.low.innerText = low.toLocaleString();
    elements.volume.innerText = Math.floor(volume).toLocaleString();

    // 등락률 처리
    const isUp = rate > 0;
    const isDown = rate < 0;

    updateRateUI(elements.rate, rate, true);

    const color = isUp ? '#d32f2f' : (isDown ? '#1976d2' : '#333');
    elements.price.classList.remove('rate-up', 'rate-down', 'rate-zero');
    elements.price.classList.add(isUp ? 'rate-up' : (isDown ? 'rate-down' : 'rate-zero'));

    if (window.ohlcv.marketPrice !== current) {
        window.ohlcv.marketPrice = current;

        // 시세가 변함에 따라 호가 사다리를 중앙에 맞춰 다시 그립니다.
        if (typeof renderOrderBook === 'function') {
            renderOrderBook(window.orderSellList, window.orderBuyList);
        }
    }

    tickerState.lastPrice = current;
}

// 강황증권의 백엔드에서 과거 캔들 데이터 가져오기
async function initTradingChart(tokenId) {
    const chartContainer = document.querySelector('.token-chart');

    if (!chartContainer) {
        return;
    }

    // 1. 차트 인스턴스 생성
    const chart = LightweightCharts.createChart(chartContainer, {
        layout: { backgroundColor: '#fff', textColor: '#333' },
        grid: { vertLines: { color: '#f0f0f0' }, horzLines: { color: '#f0f0f0' } },
        // grid: { vertLines: { visible: false }, horzLines: { visible: false } },
        timeScale: { timeVisible: true, secondsVisible: false },
        localization: { locale: 'ko-KR' },
    });

    // 캔들 디자인
    candleSeries = chart.addSeries(LightweightCharts.CandlestickSeries, {
        upColor: '#d32f2f',
        downColor: '#1976d2',
        borderUpColor: '#d32f2f',
        borderDownColor: '#1976d2',
        wickUpColor: '#d32f2f',
        wickDownColor: '#1976d2',
    });

    // 거래량 시리즈
    volumeSeries = chart.addSeries(LightweightCharts.HistogramSeries, {
        priceFormat: { type: 'volume' },
        priceScaleId: '',
    });

    // 거래량이 차트 하단 20%만 차지하도록 설정
    volumeSeries.priceScale().applyOptions({
        scaleMargins: {
            top: 0.8,
            bottom: 0,
        },
    });

    try {
        const response = await TokenApi.getCandles(tokenId);

        console.log("캔들 로드 성공!", response);

        // 지정가 주문 가격을 페이지 들어왔을 때의 시가로 고정
        const curPrice = response[response.length - 1].closingPrice; // 가장 최근 캔들 데이터의 종가
        const priceInputs = document.querySelectorAll('.price-group .input-box');

        priceInputs.forEach(input => {
            input.value = Number(curPrice).toFixed(4)
            handleNumericInput(input);
        });

        const chartData = transformCandleData(response);
        const volumeData = transformVolumeData(response);

        console.log(chartData);

        candleSeries.setData(chartData); // 캔들 데이터 주입
        volumeSeries.setData(volumeData); // 거래량 데이터 맵핑

        isChartReady = true;

    } catch (e) {
        console.error("차트 로딩 중 오류 발생", e);
    }
}

export const Icons = {
    trashCan: `
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path fill="var(--gray-800)" d="M7.5 4.8C7.5 4.32261 7.68964 3.86477 8.02721 3.52721C8.36477 3.18964 8.82261 3 9.3 3H14.7C15.1774 3 15.6352 3.18964 15.9728 3.52721C16.3104 3.86477 16.5 4.32261 16.5 4.8V6.6H20.1C20.3387 6.6 20.5676 6.69482 20.7364 6.8636C20.9052 7.03239 21 7.26131 21 7.5C21 7.73869 20.9052 7.96761 20.7364 8.1364C20.5676 8.30518 20.3387 8.4 20.1 8.4H19.1379L18.3576 19.3278C18.3253 19.7819 18.1221 20.2069 17.7889 20.5172C17.4557 20.8275 17.0174 21 16.5621 21H7.437C6.98173 21 6.54336 20.8275 6.2102 20.5172C5.87703 20.2069 5.67382 19.7819 5.6415 19.3278L4.863 8.4H3.9C3.66131 8.4 3.43239 8.30518 3.2636 8.1364C3.09482 7.96761 3 7.73869 3 7.5C3 7.26131 3.09482 7.03239 3.2636 6.8636C3.43239 6.69482 3.66131 6.6 3.9 6.6H7.5V4.8ZM9.3 6.6H14.7V4.8H9.3V6.6ZM6.6666 8.4L7.4379 19.2H16.563L17.3343 8.4H6.6666ZM10.2 10.2C10.4387 10.2 10.6676 10.2948 10.8364 10.4636C11.0052 10.6324 11.1 10.8613 11.1 11.1V16.5C11.1 16.7387 11.0052 16.9676 10.8364 17.1364C10.6676 17.3052 10.4387 17.4 10.2 17.4C9.96131 17.4 9.73239 17.3052 9.5636 17.1364C9.39482 16.9676 9.3 16.7387 9.3 16.5V11.1C9.3 10.8613 9.39482 10.6324 9.5636 10.4636C9.73239 10.2948 9.96131 10.2 10.2 10.2ZM13.8 10.2C14.0387 10.2 14.2676 10.2948 14.4364 10.4636C14.6052 10.6324 14.7 10.8613 14.7 11.1V16.5C14.7 16.7387 14.6052 16.9676 14.4364 17.1364C14.2676 17.3052 14.0387 17.4 13.8 17.4C13.5613 17.4 13.3324 17.3052 13.1636 17.1364C12.9948 16.9676 12.9 16.7387 12.9 16.5V11.1C12.9 10.8613 12.9948 10.6324 13.1636 10.4636C13.3324 10.2948 13.5613 10.2 13.8 10.2Z"/>
            </svg>`
};

// 등락률에 따른 클래스 및 텍스트 처리 유틸리티
function updateRateUI(element, rate, isTicker = false) {
    if (!element) return;

    const isUp = rate > 0;
    const isDown = rate < 0;

    // 1. 클래스 교체
    element.classList.remove('rate-up', 'rate-down', 'rate-zero');
    element.classList.add(isUp ? 'rate-up' : (isDown ? 'rate-down' : 'rate-zero'));

    // 2. 텍스트 포맷 (상단 시세는 '전일대비' 문구 포함)
    const sign = isUp ? '+ ' : '';
    const suffix = isTicker ? '% 전일대비' : '%';
    element.innerText = `${sign}${rate.toFixed(2)}${suffix}`;
}

// 숫자 포맷팅 및 입력 제어 통합 로직
function handleNumericInput(input) {
    let value = input.value.replace(/[^0-9.]/g, ''); // 숫자와 소수점만 허용

    // 소수점이 여러 개 입력되는 것 방지 (첫 번째 것만 남김)
    const parts = value.split('.');
    let result = "";

    if (parts.length > 1) {
        const integerPart = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
        const decimalPart = parts.slice(1).join('');

        result = integerPart + "." + decimalPart;
    } else {
        result = value.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }

    input.value = result;
}

function getTickSize(price) {
    if (price < 10) return 0.01;
    if (price < 100) return 0.1;
    if (price < 1000) return 1;
    if (price < 10000) return 5;
    if (price < 100000) return 10;
    if (price < 500000) return 50;
    return 100;
}

window.Icons = Icons;

window.cancelOrder = cancelOrder;

// 초기 로딩 시, 호가창 렌더링
if (window.orderSellList && window.orderBuyList) {
    renderOrderBook(window.orderSellList, window.orderBuyList);
}

// 초기 로딩 시, 체결창 렌더링
if (window.tradeList) {
    renderTradeHistory(window.tradeList);
}

// 상세 페이지의 웹소켓 구독 해제
window.addEventListener('beforeunload', () => {
    if (typeof candle !== 'undefined' && candle) {
        candle.unsubscribe();
    }
});

// 초기 로딩 시, 차트 렌더링
if (window.tokenId) {
    initTradingChart(window.tokenId);
}

window.selectPrice = selectPrice;
