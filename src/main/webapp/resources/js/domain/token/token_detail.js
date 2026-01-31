import {TokenApi} from "./token_api.js";

let candleSeries; // 캔들
let volumeSeries; // 거래량
let isChartReady = false; // 차트 초기화

// 캔들 데이터 변환 (과거 데이터용, 배열)
const transformCandleData = (data) => {
    return data.map(data => transformSingleCandle(data));
}

// 캔들 데이터 변환 (실시간 데이터용, 객체 하나)
const transformSingleCandle = (data) => ({
    time: Number(data.candleTime),
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
    time: Number(data.candleTime),
    value: Number(data.tradeVolume || 0),
    color: Number(data.closingPrice) >= Number(data.openingPrice)
        ? 'rgba(242, 54, 69, 0.3)'
        : 'rgba(8, 153, 129, 0.3)'
});

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

    // 3. 캔들 토픽 구독
    WebSocketManager.subscribe('candle', `/topic/candles/${tokenId}`, function (data) {
        console.log('[WebSocket - 캔들]', data);
        // 실시간 캔들 데이터 갱신
        if (!isChartReady || !candleSeries) {
            return;
        }
        candleSeries.update(transformSingleCandle(data));
        volumeSeries.update(transformSingleVolume(data));
        // syncTicker(data);
    });

    // 4. OHLCV 구독
    WebSocketManager.subscribe('list', `/topic/tokenList/${tokenId}`, function (data) {
        console.log('[WebSocket - OHLCV]', data);
        syncTicker(data);
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
    select.addEventListener('change', function() {
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
                if(amountGroup) amountGroup.style.display = 'flex';
            } else {
                // 시장가 매도: 가격, 총액 X / 수량 O
                priceGroup.style.display = 'none';
                volumeGroup.style.display = 'flex';
                if(amountGroup) amountGroup.style.display = 'none';
            }
        } else {
            // 지정가: 가격, 수량 O / 총액 X
            priceGroup.style.display = 'flex';
            volumeGroup.style.display = 'flex';
            amountGroup.style.display = 'none';
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

        let balance = 0;
        if (isBuyTab) {
            // 매수: 주문 가능 금액
            balance = await TokenApi.getCashBalance();
        } else {
            // 매도: 보유 토큰 수량
            balance = await TokenApi.getTokenBalance(window.tokenId);
        }
        console.log("잔액: ", balance);

        const calculatedAmount = balance * (perc / 100);

        const orderType = activeTab.querySelector('.order-type').value;

        const priceInput = activeTab.querySelector('.price-group .input-box');
        const volumeInput = activeTab.querySelector('.volume-group .input-box');
        const amountInput = activeTab.querySelector('.amount-group .input-box');

        if (orderType === 'MARKET') {
            if (isBuyTab) {
                // 시장가 매수: 주문 총액 초기화
                amountInput.value = Math.floor(calculatedAmount);
            } else {
                // 시장가 매도: 주문 수량 초기화
                volumeInput.value = calculatedAmount.toFixed(8);
            }
        } else {
            const price = parseFloat(priceInput.value);

            if (isBuyTab) {
                // 지정가 매수: 주문 수량 초기화
                if (price > 0) {
                    volumeInput.value = (calculatedAmount / price).toFixed(8);
                } else {
                    console.log("가격을 먼저 입력해주세요.");
                }
            } else {
                // 지정가 매도: 주문 수량 초기화
                if (volumeInput) volumeInput.value = calculatedAmount;
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
async function handleOrder(event, side){
    const container = event.target.closest('.tab-content-wrapper');
    const orderType = container.querySelector('.order-type').value; // LIMIT or MARKET

    const getValue = (selector) => {
        const element = container.querySelector(selector);
        return element ? Number(element.value.replace(/,/g, '')) : 0; // 기본값 0, 콤마 제거 후 숫자 변환
    };

    const orderDTO = {
        tokenId: window.tokenId,
        orderSide: side,
        orderType: orderType,
        orderPrice: orderType === 'LIMIT' ? getValue('.price-group input') : 0, // 시장가는 0
        orderVolume: (side === 'BUY' && orderType === 'MARKET') ? 0 : getValue('.volume-group input'), // 시장가 매수는 0
        totalPrice: (side === 'BUY') ? getValue('.amount-group input') : 0 // 매도는 0
    };

    // 검증 실행
    const check = validateOrder(orderDTO);
    if (!check.valid) {
        alert(check.msg);
        return;
    }

    // API 호출
    try {
        const result = await TokenApi.createOrder(orderDTO.tokenId, orderDTO);
        if (result) alert("주문이 완료되었습니다.");
    } catch(e) {
        alert("주문 처리 중 오류가 발생했습니다. 다시 시도해주세요.");
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
        return { valid: false, msg: "올바른 값을 입력해주세요." };
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
        listContainer.innerHTML = '<li class="transaction-item" style="justify-content:center;">미체결 내역이 없습니다.</li>';
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
                        <i class="icon-trashcan"></i>
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
                        <span class="value">${Number(item.orderVolume).toFixed(8)}</span>
                    </div>
                    <div class="trade-info-row">
                        <span class="label">미체결량</span>
                        <span class="value">${Number(item.remainingToken).toFixed(8)}</span>
                    </div>
                </div>
            </li>
        `;
}

/* 주문 취소 */
async function cancelOrder(orderId) {
    if(!confirm("주문을 취소하시겠습니까?")) return;

    try {
        await TokenApi.cancelOrder(window.tokenId, orderId);
        alert("주문이 취소되었습니다.");
        fetchPendingOrders();
    } catch (e) {
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

    elements.rate.innerText = `${isUp ? '+' : ''}${rate.toFixed(2)}% 전일대비`;

    const color = isUp ? '#f23645' : (isDown ? '#089981' : '#333');
    elements.price.style.color = color;
    elements.rate.style.color = color;

    tickerState.lastPrice = current;
}

// 강황증권의 백엔드에서 과거 캔들 데이터 가져오기
async function initTradingChart(tokenId) {
    const chartContainer = document.querySelector('.token-chart');
    if(!chartContainer) {
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
        upColor: '#f23645',
        downColor: '#089981',
        borderUpColor: '#f23645',
        borderDownColor: '#089981',
        wickUpColor: '#f23645',
        wickDownColor: '#089981',
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

window.cancelOrder = cancelOrder;

// 초기 로딩 시, 호가창 렌더링
if (window.orderSellList && window.orderBuyList) {
    renderOrderBook(window.orderSellList, window.orderBuyList);
}

// 초기 로딩 시, 체결창 렌더링
if (window.tradeList) {
    renderTradeHistory(window.tradeList);
}

// 초기 로딩 시, 차트 렌더링
if (window.tokenId) {
    initTradingChart(window.tokenId);
}
