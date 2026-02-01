/**
 * 마이페이지 전자지갑 관련 통합 스크립트
 */
let currentPage = 1;

document.addEventListener("DOMContentLoaded", function() {
    // 페이지 로드 시 초기 데이터 호출
    initWalletPage();
    
    // 계좌 연동 버튼 이벤트 바인딩
    const linkBtn = document.querySelector(".btn-link-account");
    if (linkBtn) {
        linkBtn.addEventListener("click", handleLinkAccount);
    }

    // 더보기 버튼 이벤트 바인딩
    const btnMore = document.getElementById("btn-more-tokens");
    if (btnMore) {
        btnMore.addEventListener("click", function() {
            loadMoreTokens(); // 클릭 시 다음 페이지 로드
        });
    }
});

/**
 * 페이지 초기화: 지갑 및 토큰 정보 호출
 */
function initWalletPage() {
    loadWalletInfo();
    loadTokenHoldings(1);
}

/**
 * 1. 지갑 요약 정보 로드
 */
function loadWalletInfo() {
    const token = localStorage.getItem("accessToken");
    fetch(ctx + "/api/mypage/wallet-info", {
        method: "GET",
        headers: { "Authorization": "Bearer " + token }
    })
    .then(res => res.json())
    .then(data => {
        console.log("서버 응답 데이터:", data.payload); // 여기서 숫자가 어떻게 오는지 확인!
        const wallet = data.payload;

        // 연동 내역이 없는 경우 처리
        if (!wallet) {
            renderEmptyWallet();
            return;
        }

        // 연동 내역이 있는 경우 데이터 바인딩
        bindWalletData(wallet);
    })
    .catch(err => console.error("Wallet Info Error:", err));
}

/**
 * 2. 보유 토큰 리스트 로드
 */
function loadTokenHoldings(page) {
    const token = localStorage.getItem("accessToken");
    fetch(ctx +"/api/mypage/holdings?page=" + page, {
        method: "GET",
        headers: {
            "Accept": "application/json",
            "Authorization": "Bearer " + token
        }
    })
    .then(res => res.json())
    .then(data => {
        const tbody = document.getElementById("token-data-list");
        const btnMore = document.getElementById("btn-more-tokens");
        const list = data.payload;

        if (page === 1) tbody.innerHTML = "";

        

        // 연동 내역이 없거나 보유 토큰이 없는 경우
        if (!list || list.length === 0) {
            if (page === 1) {
                tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; padding: 60px; color: var(--gray-400);">보유 중인 토큰이 없습니다.</td></tr>';
            }
            if (btnMore) {
                btnMore.style.display = "none";
                return;
            }
        }

        renderTokenList(list);
        // 더보기 버튼 로직: 10개 단위로 데이터가 온다면 다음 페이지가 있다고 판단
        // [수정] 더보기 버튼 로직: 10개 이상이면 무조건 표시
        if (btnMore) {
            // list.length가 10개면 다음 페이지가 있다고 가정합니다.
            if (list.length >= 10) {
                btnMore.style.display = "block";
                console.log("더보기 버튼 활성화");
            } else {
                btnMore.style.display = "none";
                console.log("데이터가 10개 미만이므로 더보기 버튼 숨김");
            }
        }
    })
    .catch(err => console.error("Token Holdings Error:", err));
}

/**
 * UI 렌더링: 미연동 상태 처리
 */
function renderEmptyWallet() {
    document.querySelector(".bank-name").textContent = "연동된 계좌가 없습니다.";
    document.querySelector(".account-number").textContent = "-";
    document.querySelector(".total-amount").childNodes[0].textContent = "0 ";

    const emptyIds = ["totalAsset", "deposit", "purchaseAmount", "marketValue", "profitLoss", "profitLossRate"];
    emptyIds.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.textContent = "-";
    });
}

/**
 * UI 렌더링: 지갑 데이터 바인딩
 */
function bindWalletData(wallet) {
    // textContent를 통째로 바꾸기보다 특정 span을 만드는 것이 안전합니다.
    const accountEl = document.querySelector(".total-amount");
    if (accountEl) {
        accountEl.innerHTML = `${(wallet.cashBalance || 0).toLocaleString()} <span class="amount-unit">원</span>`;
    }

    //은행명 바인딩 (클래스 선택자 확인)
    const bankNameEl = document.querySelector(".bank-name");
    if (bankNameEl) {
        bankNameEl.textContent = wallet.bankName || "연동된 은행 없음";
    }

    const accNoEl = document.querySelector(".account-number");
    if (accNoEl) {
        accNoEl.textContent = wallet.accountNo; // '110-222-000415'가 들어갑니다.
    }

    const updateValue = (id, value, isPrice = true) => {
        const el = document.getElementById(id);
        if (el) {
            // value가 String으로 넘어올 경우를 대비해 Number() 처리
            const numValue = Number(value);
            el.innerHTML = isPrice ? 
                `${numValue.toLocaleString()} <span class="grid-unit">원</span>` : 
                `${numValue.toFixed(2)}%`;
        }
    };

    updateValue("totalAsset", wallet.totalBalance);
    updateValue("deposit", wallet.cashBalance);
    updateValue("purchaseAmount", wallet.totalPurchasedValue);
    updateValue("marketValue", wallet.totalMarketValue);

    const profit = wallet.profitLoss || 0;
    const profitEl = document.getElementById("profitLoss");
    const rateEl = document.getElementById("profitLossRate");

    if (profitEl) {
        const prefix = profit >= 0 ? "+" : "";
        profitEl.textContent = `${prefix}${profit.toLocaleString()}`;
        profitEl.className = `grid-value ${profit >= 0 ? 'text-plus' : 'text-minus'}`;
    }

    if (rateEl) {
        const rate = wallet.profitLossRate || 0;
        rateEl.textContent = `${rate >= 0 ? "+" : ""}${rate.toFixed(2)}%`;
        rateEl.className = `grid-value ${rate >= 0 ? 'text-plus' : 'text-minus'}`;
    }
}

/**
 * UI 렌더링: 토큰 행 생성
 */
function renderTokenList(list) {
    const tbody = document.getElementById("token-data-list");
    let html = "";
    
    // 데이터가 배열인지 최종 확인
    if (!Array.isArray(list)) return;

    list.forEach(item => {
        try {
            // 모든 수치 데이터에 대해 Number() 변환 및 null 체크 수행
            const profitLoss = Number(item.profitLoss || 0);
            const profitLossRate = Number(item.profitLossRate || 0);
            const marketValue = Number(item.marketValue || 0);
            const purchasedValue = Number(item.purchasedValue || 0);
            const tokenBalance = Number(item.tokenBalance || 0);

            const plusMinusClass = profitLoss >= 0 ? "text-plus" : "text-minus";
            const prefix = profitLoss >= 0 ? "+" : "";

            html += `
                <tr>
                    <td>
                        <div class="token-name">${item.tokenName || '-'}</div>
                        <div class="token-code-cell">${item.tickerSymbol || '-'}</div>
                    </td>
                    <td style="text-align: right;">
                        <div class="token-val ${plusMinusClass}">${prefix}${profitLoss.toLocaleString()} 원</div>
                        <div class="token-sub ${plusMinusClass}">${prefix}${profitLossRate.toFixed(2)} %</div>
                    </td>
                    <td style="text-align: right;">
                        <div class="token-val">${marketValue.toLocaleString()} 원</div>
                        <div class="token-sub">${purchasedValue.toLocaleString()} 원</div>
                    </td>
                    <td class="token-amount">${tokenBalance.toLocaleString()} st</td>
                </tr>`;
        } catch (e) {
            console.error("특정 행 렌더링 중 오류 발생:", e, item);
            // 에러가 발생한 행은 건너뛰고 다음 데이터를 처리함
        }
    });
    
    tbody.insertAdjacentHTML('beforeend', html);
}

/**
 * 계좌 연동 처리 (POST 호출)
 */
function handleLinkAccount() {
    if (!confirm("증권사 계좌를 연동하시겠습니까?")) return;
    const token = localStorage.getItem("accessToken");

    fetch(ctx + "/api/mypage/account/link", {
        method: "GET",
        headers: { "Authorization": "Bearer " + token }
    })
    .then(res => {
        if (!res.ok) {
            return res.json().then(err => { 
                // 서버에서 보낸 메시지("이미 연동된 회원입니다" 등)를 에러로 던짐
                throw new Error(err.message || "연동 처리에 실패했습니다."); 
            });
        }
        return res.json();
    })
    .then(data => {
        alert(data.message); // "계좌 연동에 성공했습니다."
        location.reload();  // 성공하면 화면 새로고침해서 데이터 반영
    })
    .catch(err => {
        // "연동 가능한 강황증권 계좌를 찾을 수 없습니다." 알림 출력
        alert(err.message); 
    });
}

function loadMoreTokens() {
    currentPage++;
    loadTokenHoldings(currentPage);
}