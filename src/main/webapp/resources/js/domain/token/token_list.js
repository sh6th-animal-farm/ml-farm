import { TokenApi } from "./token_api.js";

async function fetchTokens() {

    const url = new URL(window.location.href);
    window.history.pushState({}, "", url);

    try {
        const data = await TokenApi.getTokenList();

        if(data && data.length > 0) {
            renderTokenTable(data);
        }
    } catch (error) {
        console.error("데이터 로드 실패: ", error);
    }
}

// 일단 이렇게 그릴게요....
function renderTokenTable(tokens) {
    const tbody = document.querySelector('.token_table_main tbody');
    if (!tbody) {
        return;
    }

    const html = tokens.map((token, index) => {

        const hasPrice = token.marketPrice != null;
        const hasGain = token.gain != null;

        const colorClass = hasGain ? (token.gain > 0 ? 'text-plus' : token.gain < 0 ? 'text-minus' : '') : '';
        const sign = hasGain && token.gain > 0 ? '+' : '';

        return `
            <tr>
                <td class="td-rank">${index + 1}</td>
                
                <td>
                    <div class="token-name">${token.tokenName || '-'}</div>
                    <div class="token-code">${token.tickerSymbol || '-'}</div>
                </td>
                
                <td class="td-price">
                    <div class="token-name">
                        ${hasPrice ? Number(token.marketPrice).toLocaleString() + " 원" : "-"}
                    </div>
                </td>
                
                <td class="td-change">
                    <div class="token-name ${colorClass}">
                        ${hasGain ? sign + Number(token.gain).toLocaleString() : "-"}
                    </div>
                </td>
                
                <td class="token-amount">
                    <div class="token-name">
                        ${token.dailyTradeVolume ? Number(token.dailyTradeVolume).toLocaleString() : "-"}
                    </div>
                </td>
            </tr>
        `;
    }).join('');

    tbody.innerHTML = html;
}


function openTab(evt, tabName) {
    var i, tabcontent, tablinks;
    tabcontent = document.getElementsByClassName("tab-content");
    for (i = 0; i < tabcontent.length; i++) {
    tabcontent[i].style.display = "none";
    tabcontent[i].classList.remove("active");
}
    tablinks = document.getElementsByClassName("tab-btn");
    for (i = 0; i < tablinks.length; i++) {
    tablinks[i].classList.remove("active");
}
    document.getElementById(tabName).style.display = "block";
    document.getElementById(tabName).classList.add("active");
    evt.currentTarget.classList.add("active");
}
// 제 파일이에요



// 소켓 연결 설정
function connectExchange(ec2Ip, tokenPath) {
    const socket = new SockJS('http://localhost:9090/ws-stomp');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        // 실시간 시세 채널 구독
        stompClient.subscribe('/topic/trades/**', function (response) {
            console.log('실시간 데이터 수신:', response.body);
            const updatedData = JSON.parse(response.body);
            updateTokenRow(updatedData);
        });
    }, function (error) {
        console.error('STOMP Error: ' + error);
    });
}

// 특정 행(row)만 찾아서 업데이트
function updateTokenRow(data) {
    const row = document.querySelector(`tr[data-token-id="${data.tokenId}"]`);
    if (!row) {
        return;
    }

    const priceDiv = row.querySelector('.td-price .token-name');
    const pctDiv = row.querySelector('.td-pct .token-name');

    // 기존 가격과 비교해서 상승/하락 판단 (반짝임 효과용)
    const oldPrice = parseFloat(priceDiv.innerText.replace(/[^0-9.-]+/g, ""));
    const newPrice = data.marketPrice;

    // 가격 및 등락률 텍스트 업데이트
    priceDiv.innerText = `${newPrice.toLocaleString()} 원`;
    pctDiv.innerText = `${data.gain > 0 ? '+' : ''}${data.gainPct}%`;

    // 색상 클래스 갱신
    const colorClass = data.gain > 0 ? 'text-plus' : data.gain < 0 ? 'text-minus' : '';
    pctDiv.className = `token-code ${colorClass}`;

    // 가격 셀 업데이트
    const priceCell = row.querySelector('.td-price .token-name');
    if (priceCell) {
        priceCell.innerText = Number(data.marketPrice).toLocaleString() + " 원";
    }

    // 반짝임 효과 적용
    if (newPrice > oldPrice) {
        priceDiv.classList.remove('up-flash');
        void priceDiv.offsetWidth; // 리플로우 강제 발생 (애니메이션 재시작)
        priceDiv.classList.add('up-flash');
    } else if (newPrice < oldPrice) {
        priceDiv.classList.remove('down-flash');
        void priceDiv.offsetWidth;
        priceDiv.classList.add('down-flash');
    }

    // // 등락률 및 색상 업데이트
    // const pctCell = row.querySelector('.td-pct .token-code');
    // if (pctCell) {
    //     const isPlus = data.gain > 0;
    //     pctCell.className = `token-code ${isPlus ? 'text-plus' : 'text-minus'}`;
    //     pctCell.innerText = `${isPlus ? '+' : ''}${data.gainPct}%`;
    // }
}

// document.addEventListener('DOMContentLoaded', fetchStocks);
window.fetchTokens = fetchTokens;
// window.connectExchange = connectExchange;
document.addEventListener('DOMContentLoaded', async () => {
    await fetchTokens();
    connectExchange();
});
