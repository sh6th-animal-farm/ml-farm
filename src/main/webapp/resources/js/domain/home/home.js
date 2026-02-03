import { ProjectApi } from "../project/project_api.js";
import { TokenApi } from "../token/token_api.js";

async function initStarredStatus() {
    // 화면에 있는 모든 프로젝트 카드에서 ID 추출
    const cards = document.querySelectorAll('.project-card');
    
    // 각 카드별로 서버에 관심 여부 확인 (비동기)
    cards.forEach(async (card) => {
        const projectId = card.getAttribute('onclick').match(/\d+/)[0]; 
        
        try {
            const isStarred = await ProjectApi.getIsStarred(projectId);
            
            if (isStarred) {
                // 관심 프로젝트라면 하트 아이콘의 색상을 빨간색으로 변경
                const heartIcon = card.querySelector(`.heart-icon-${projectId}`);
                if (heartIcon) {
                    heartIcon.classList.add('active')
                    card.querySelector('.interest-btn').setAttribute('data-starred', 'true');
                }
            }
        } catch (e) {
            // 로그인 안 한 경우 false가 오거나 에러가 날 수 있으나 무시 (기본 흰하트 유지)
        }
    });
}


document.addEventListener('DOMContentLoaded', async function() {
    initStarredStatus();

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