<%@ tag language="java" pageEncoding="UTF-8" body-content="scriptless" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- 속성 정의 --%>
<%@ attribute name="id" required="true" %> <%-- 모달 고유 ID --%>
<%@ attribute name="title" required="true" %> <%-- 프로젝트 제목 --%>
<%@ attribute name="projectId" required="true" %> <%-- 프로젝트 제목 --%>
<%@ attribute name="price" required="true" type="java.lang.Long" %> <%-- 1토큰 당 가격 --%>
<%@ attribute name="thumbnail" required="false" %> <%-- 이미지 경로 --%>
<%@ attribute name="userLimit" required="true" type="java.lang.Long" %> <%-- 투자 한도 잔여 --%>
<%@ attribute name="walletBalance" required="true" type="java.lang.Long" %> <%-- 지갑 잔액 --%>
<%@ attribute name="tokenId" required="true" type="java.lang.Long" %> <%-- 토큰Id --%>
<%@ attribute name="minAmountPerInvestor" required="true" type="java.lang.Long" %> <%-- 토큰Id --%>

<div id="${id}" class="subscription-modal-overlay">
    <div class="subscription-modal-content" onclick="event.stopPropagation()">
        <%-- 헤더 --%>
        <div class="subscription-modal-header">
            <h3>청약 신청하기</h3>
            <button class="subscription-close-btn" onclick="closeSubscriptionModal('${id}')">&times;</button>
        </div>

        <%-- 프로젝트 요약 카드 --%>
        <div class="project-summary-card">
            <img src="${thumbnail}" alt="project-img" class="summary-img">
            <div class="summary-info">
                <div class="summary-title">${title}</div>
                <div class="summary-price">1 토큰 당 <fmt:formatNumber value="${price}" pattern="#,###"/>원</div>
            </div>
        </div>

        <%-- 투자 한도 섹션 --%>
        <div class="limit-section">
            <div class="limit-header">
                <span>나의 연간 투자 한도 잔여</span>
                <span class="limit-percent" id="limit-percent-text">0% 사용</span>
            </div>
            <div class="limit-progress">
                <div class="progress-fill" id="limit-bar" style="width: 0%;"></div>
            </div>
            <div class="limit-value"><fmt:formatNumber value="${userLimit}" pattern="#,###"/>원</div>
        </div>

        <%-- 수량 입력 섹션 --%>
        <div class="input-section">
            <label>청약 수량 입력</label>
            <div class="token-input-wrapper">
                <input type="number" id="sub-quantity" value="1" min="0.0001" step="0.0001" oninput="calculateTotal(this.value, ${price})">
                <span class="unit">토큰</span>
            </div>
            <div class="error-container">
            	<p class="min-info-msg" id="min-info">* 최소 청약 금액: <fmt:formatNumber value="${minAmountPerInvestor}" pattern="#,###"/>원</p>
                <p class="error-msg" id="sub-error">신청 가능한 최대 수량을 초과할 수 없습니다.</p>
            </div>
        </div>

        <div class="wallet-info">
            <span>나의 지갑(Wallet) 잔액</span>
            <strong id="modal-wallet-balance"><fmt:formatNumber value="${walletBalance}" pattern="#,###"/>원</strong>
        </div>

        <%-- 최종 결제 정보 --%>
        <div class="final-info-box">
            <div class="info-row">
                <span>청약 수량</span>
                <span id="display-quantity">1 토큰</span>
            </div>
            <div class="info-row total">
                <span class="total-text">총 청약 금액</span>
                <span class="total-price"><span id="display-total-price"><fmt:formatNumber value="${price}" pattern="#,###"/></span> 원</span>
            </div>
        </div>

        <%-- 신청 버튼 --%>
        <button class="submit-btn" onclick="submitSubscription('${projectId}')">청약 신청 완료</button>
    </div>
</div>

<script>
	window.unitPrice = ${price}; 
	window.myBalance = ${walletBalance}; // 처음엔 0
	window.currentRemainLimit = ${userLimit};
	window.minAmount = ${minAmountPerInvestor};
    
    const myTotalLimit = 50000000; // 예시: 총 한도 5천만원
    const alreadyInvested = myTotalLimit - currentRemainLimit; // 이미 사용한 금액 계산
    
    function calculateTotal(val) {
        const input = document.getElementById('sub-quantity');
        const wrapper = input.closest('.token-input-wrapper');
        const errorMsg = document.getElementById('sub-error');
        const minInfo = document.getElementById('min-info'); // 최소 금액 안내 문구
        const submitBtn = document.querySelector('.submit-btn');
        
    	// 1. 소수점 4자리까지만 입력 가능하도록 제어 (정규식)
        if (val.includes('.')) {
            const parts = val.split('.');
            if (parts[1].length > 4) {
                input.value = parseFloat(val).toFixed(4); // 4자리 넘어가면 자름
                val = input.value;
            }
        }
        
        // 입력값 정제
        let quantity = parseFloat(val);
        if (isNaN(quantity) || quantity <= 0) {
            updateModalUI(0);
            submitBtn.disabled = true;
            return;
        }
        
        // 현재 총액 계산
        const currentTotalPrice = Math.round(quantity * unitPrice);
        
        // 최대 가능 수량 계산 (한도 vs 잔액)
        const maxAvailableAmount = Math.min(currentRemainLimit, myBalance);
        const maxQuantity = maxAvailableAmount / unitPrice;

        let isError = false;
        let msg = "";

        // 1. 최소 금액 검증
        if (currentTotalPrice < minAmount) {
            isError = true;
            msg = "최소 청약 금액은 " + minAmount.toLocaleString() + "원 입니다.";
        } 
        // 2. 최대 수량 검증
        else if (quantity > maxQuantity) {
            isError = true;
            msg = "신청 가능한 최대 수량을 초과할 수 없습니다.";
            // 사용자 경험을 위해 강제로 깎지는 않고 메시지만 보여주거나, 필요시 아래 주석 해제
            // input.value = maxQuantity; 
            // quantity = maxQuantity; 
        }

        // UI 반영
        if (isError) {
            errorMsg.innerText = msg;
            errorMsg.style.display = 'block';   // 에러 메시지 표시
            minInfo.style.display = 'none';     // 평상시 안내 문구 숨김
            wrapper.classList.add('is-error');
            submitBtn.disabled = true;          // 버튼 비활성화
        } else {
            errorMsg.style.display = 'none';
            minInfo.style.display = 'block';    // 평상시 안내 문구 다시 표시
            wrapper.classList.remove('is-error');
            submitBtn.disabled = false;         // 버튼 활성화
        }

        updateModalUI(quantity);
    }

    function updateModalUI(quantity) {
        // 1. 이번 청약 금액 계산
        const totalAmount = Math.round(quantity * unitPrice);
        
        // 2. 콤마 포맷팅 처리
        document.getElementById('display-quantity').innerText = quantity.toLocaleString(undefined, { minimumFractionDigits: 0, maximumFractionDigits: 4 }) + ' 토큰';
        document.getElementById('display-total-price').innerText = totalAmount.toLocaleString();
        
     	// 2. 퍼센트 계산 로직
        // 사용자가 '남은 한도' 중에서 이번에 '얼마나 채우는지' 보여줍니다.
        let usagePercent = 0;
        if (currentRemainLimit > 0) {
            usagePercent = (totalAmount / currentRemainLimit) * 100;
        }
        
        // 0보다 작으면 0, 100보다 크면 100으로 고정 (Clamping)
        usagePercent = Math.min(Math.max(usagePercent, 0), 100);
        
        // 5. UI 업데이트
        const bar = document.getElementById('limit-bar');
        const text = document.getElementById('limit-percent-text');
        
        bar.style.width = usagePercent + '%';
        text.innerText = Math.floor(usagePercent) + '% 사용';
    }

    function closeSubscriptionModal(id) {
        document.getElementById(id).style.display = 'none';
        // 닫을 때 값 초기화
        const input = document.getElementById('sub-quantity');
        input.value = 1;
        updateModalUI(1);
    }

    function submitSubscription(id) {
        const qty = document.getElementById('sub-quantity').value;
        // 실제 서버 전송 로직 (Ajax 등)을 여기에 구현
        console.log("청약 신청 수량:", qty);
        alert(qty + "토큰 청약 신청이 완료되었습니다.");
        closeSubscriptionModal(id);
    }
    
 // '청약 신청 완료' 버튼을 눌렀을 때 실행되는 함수
    function submitSubscription(projectId) {
        const quantity = document.getElementById('sub-quantity').value; // 입력한 숫자
        const totalPrice = Math.round(quantity * unitPrice);
        
     	// 1. 로컬 스토리지에서 토큰을 가져옵니다.
        const token = localStorage.getItem("accessToken");
        
        const payload = {
        	tokenId: ${projectData.tokenId},
        	projectId: projectId,         // 프로젝트 ID
        	subscriptionAmount: totalPrice, // 수량
            walletId: window.myWalletId,
            projectId: projectId
            
        };
        
        fetch(ctx + "/api/subscription/application", {
            method: "POST",
            headers: { 
            	"Content-Type": "application/json",
            	"Authorization": token ? "Bearer " + token : ""
            			},
            body: JSON.stringify(payload) // 박스를 테이프로 감싸서 전송!
        })
        .then(response => {
            // 서버가 200 OK를 던졌는지 확인
            if (!response.ok) {
                return response.text().then(err => { throw new Error(err) });
            }
            return response.text(); // 서버가 ResponseEntity<String>으로 주니까 text로 받기
        })
        .then(result => {
            // 앞뒤 공백 제거 (혹시 모를 줄바꿈 방지)
            const status = result.trim();
            console.log("서버 응답 결과:", status);

            if (status === "success") {
                alert("✅ 청약 신청이 완료되었습니다!");
                location.href = ctx + "/project/" + projectId;
            } else if (status === "empty_payload") {
                alert("❌ 청약 신청이 실패되었습니다!.");
            } else {
                alert("⚠️ 신청 실패: " + status);
            }
        })
        .catch(error => {
            console.error("Fetch 에러:", error);
            alert("⚠️ 서버 연결 오류: " + error.message);
        });
    }
    
    // 초기 실행
    window.onload = () => updateModalUI(1);
</script>

<style>
/* 입력창 화살표 및 초록 테두리 제거 */
input[type="number"]::-webkit-outer-spin-button,
input[type="number"]::-webkit-inner-spin-button {
    -webkit-appearance: none;
    margin: 0;
}
input[type="number"] {
    -moz-appearance: textfield; /* Firefox 화살표 제거 */
}

.subscription-modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: none; align-items: center; justify-content: center; z-index: 1000; }
.subscription-modal-content { background: white; width: 440px; border-radius: 20px; padding: 24px; box-sizing: border-box; }
.subscription-modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.subscription-modal-header h3 { font: var(--font-subtitle-01); }
.subscription-close-btn { background: none; border: none; font-size: 24px; cursor: pointer; color: var(--gray-900); }

.project-summary-card { display: flex; gap: 14px; background: #F8F9FA; border-radius: var(--radius-m); padding: 14px; margin-bottom: 20px; align-items: center; }
.summary-img { width: 64px; height: 64px; border-radius: 8px; object-fit: cover; }
.summary-title { font: var(--font-body-04);  margin-bottom: 2px; }
.summary-price { color: var(--green-600); font: var(--font-button-02);  }

.limit-section { margin-bottom: 24px; }
.limit-header { display: flex; justify-content: space-between; font: var(--font-caption-01); color: var(--gray-500); margin-bottom: 8px; }
.limit-progress { height: 6px; background: #EEE; border-radius: 3px; overflow: hidden; }
.progress-fill { height: 100%; background: var(--green-600); transition: width 0.3s ease-out; }
.limit-value { text-align: right; margin-top: 8px; font: var(--font-caption-02); }

.input-section label { display: block; font: var(--font-body-04); margin-bottom: 10px; text-align:left; }
.token-input-wrapper { border: 1.5px solid var(--gray-900); height: 60px; border-radius: var(--radius-m); padding: 0 16px; display: flex; align-items: center; gap: 8px; transition: all 0.2s ease;}
.token-input-wrapper:focus-within {
    border-color: var(--gray-900) !important;
    box-shadow: 0 0 0 1px var(--gray-900); /* 살짝 두꺼워지는 효과 */
}
/* 한도 초과 시 빨간색 테두리 */
.token-input-wrapper.is-error {
    border-color: var(--error) !important;
    background-color: var(--error-light); /* 연한 빨강 배경 추가 */
    box-shadow: 0 0 0 1px var(--error);;
}
/* input 요소 자체의 포커스 효과 강제 제거 */
.token-input-wrapper input:focus {
    outline: none !important;
    border: none !important;
    box-shadow: none !important;
}
.token-input-wrapper input { -webkit-tap-highlight-color: transparent; border: none; font: var(--font-header-04); text-align: right; flex: 1; outline: none; background: transparent; padding: 16 16 16 0 }
.token-input-wrapper .unit { font: var(--font-body-04); white-space: nowrap; flex-shrink: 0;  }

.error-container {
    height: 20px; /* 에러 메시지 높이만큼 확보 */
    margin-top: 4px;
    text-align: right;
}
.error-msg { color: var(--error); font: var(--font-caption-01); text-align: right; margin-top: 4px; display: none; }

.wallet-info { display: flex; justify-content: space-between; font-size: 13px; margin: 2px 0 20px 0; }

.final-info-box { background: #F8F9FA; border-radius: var(--radius-m); padding: 16px; margin-bottom: 20px; }
.info-row { display: flex; justify-content: space-between; margin-bottom: 12px; font: var(--font-caption-01); color: var(--gray-500); }
.info-row.total { margin-bottom: 0; padding-top: 12px; border-top: 1px solid var(--gray-200); color: var(--gray-900); font-weight: 700; }
.total-text { font: var(--font-caption-02); }
.total-price { color: var(--green-600); font: var(--font-subtitle-01); }

.submit-btn:disabled {
    background: var(--gray-200);
    color: var(--gray-500);
    cursor: not-allowed;
}
.submit-btn { width: 100%; padding: 16px; background: var(--green-600); color: white; border: none; border-radius: 12px; font-size: 16px; font-weight: 700; cursor: pointer; }
.min-info-msg {
    font: var(--font-caption-01);
    color: var(--gray-400);
    text-align: right;
    margin-top: 4px;
}
</style>