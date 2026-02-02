import { http } from "../../api/http_client.js";
import { TokenApi } from "../token/token_api.js";

document.addEventListener('DOMContentLoaded', async function() {
    const cards = await http.get(`${ctx}/project/list/fragment/main`);
    let projectArea = document.querySelector(".project-card-list");
    projectArea.innerHTML = cards;

    const chartCtx = document.getElementById('kocChart').getContext('2d');
    new Chart(chartCtx, {
        type : 'line',
        data : {
            labels : [ '1월', '2월', '3월', '4월', '5월', '6월' ],
            datasets : [ {
                label : 'KOC 거래가 (원)',
                data : [ 28500, 29200, 27800, 31000, 33500, 35000 ],
                borderColor : '#4a9f2e',
                backgroundColor : 'rgba(74, 159, 46, 0.1)',
                borderWidth : 3,
                fill : true,
                tension : 0.4,
                pointRadius : 0,
                pointHoverRadius : 6
            } ]
        },
        options : {
            responsive : true,
            maintainAspectRatio : false,
            plugins : {
                legend : {
                    display : false
                }
            },
            scales : {
                y : {
                    beginAtZero : false,
                    grid : {
                        color : '#f0f0f0'
                    },
                    ticks : {
                        font : {
                            size : 11
                        }
                    }
                },
                x : {
                    grid : {
                        display : false
                    },
                    ticks : {
                        font : {
                            size : 11
                        }
                    }
                }
            }
        }
    });
});

// 토큰 거래소 TOP 10 실시간 변경
WebSocketManager.connect(TokenApi.WS_CONN, function() {

    // 토큰 목록 토픽 구독
    WebSocketManager.subscribe('tokenList', `/topic/tokenList`, function (data) {
        if (!data || !Array.isArray(data)) return;

        window.tokenList = data;

        const topListContainer = document.querySelector('.top-list');
        if (!topListContainer) return;

        // 거래대금 기준 내림차순 정렬 후 상위 10개만 추출
        const topTen = [...data]
            .sort((a, b) => b.dailyTradeVolume - a.dailyTradeVolume)
            .slice(0, 10);

        let html = '';
        topTen.forEach((token, index) => {
            const changeRate = parseFloat(token.changeRate || 0);

            const colorStyle = changeRate > 0 ? 'color: var(--error)' : 'color: var(--info)';
            const sign = changeRate > 0 ? '+' : '';

            html += `
                <div class="top-item">
                    <div class="top-item-left">
                        <span class="top-rank">${index + 1}</span>
                        <span class="top-name">${token.tokenName}</span>
                    </div>
                    <div class="top-item-right">
                        <div class="price">
                            ${Number(token.marketPrice).toLocaleString()}원
                        </div>
                        <div class="change" style="${colorStyle}">
                            ${sign}${changeRate.toFixed(2)}%
                        </div>
                    </div>
                </div>
            `;
        });

        topListContainer.innerHTML = html;
    });
})