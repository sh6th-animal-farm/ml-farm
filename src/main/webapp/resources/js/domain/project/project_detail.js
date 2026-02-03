async function openSubscriptionModal(modalId) {
    const token = localStorage.getItem("accessToken");
    if (!token) {
        ToastManager.show("로그인이 필요한 서비스입니다.");
        return;
    }

    try {
        // 1. 서버에 지갑 잔액 요청 (이 요청이 발생해야 서버에서 SecurityUtil -> uclId 조회 -> 외부 API 호출이 일어남)
        const response = await fetch(window.location.href, {
            headers: { "Authorization": "Bearer " + token,
            	"X-Requested-With": "fetch"
             }
        });

        if (response.ok) {
            // 서버 서비스 로직이 리턴한 Double(잔액) 값
            const data = await response.json();
            const balance = data.balance;
            
            // 2. 모달 내 잔액 텍스트 업데이트
            const balanceEl = document.getElementById("modal-wallet-balance");
            if (balanceEl) {
                balanceEl.innerText = balance.toLocaleString() + "원";
            }
            
            // 3. (중요) JS 내부 변수 myBalance 업데이트 (한도 계산용)
            // tag 내 script에 선언된 전역 변수가 있다면 업데이트 해줘야 한도 체크가 정상 작동합니다.
            window.myBalance = balance; 
            
            calculateTotal(document.getElementById('sub-quantity').value);
            
            // 4. 이제 모달을 띄움
            document.getElementById(modalId).style.display = "flex";
            
            // 5. 모달 UI 초기화 (수량 1개 기준 한도 바 업데이트 등)
            if(typeof updateModalUI === 'function') updateModalUI(1);

        } else {
            ToastManager.show("지갑 정보를 불러올 수 없습니다. 다시 시도해주세요.");
        }
    } catch (error) {
        console.error("잔액 조회 중 오류:", error);
    }
}

function closeSubscriptionModal(modalId) {
  document.getElementById(modalId).style.display = "none";
}

function calculateTotal(quantity, unitPrice) {
  const qty = parseInt(quantity) || 0;
  const total = qty * unitPrice;

  // UI 업데이트
  document.getElementById("display-quantity").innerText = qty + " 토큰";
  document.getElementById("display-total-price").innerText =
    total.toLocaleString();

  // 예: 한도 초과 체크 로직 추가 가능
  const errorMsg = document.getElementById("sub-error");
  if (qty > 100) {
    // 예시 기준값
    errorMsg.style.display = "block";
  } else {
    errorMsg.style.display = "none";
  }
}

function submitSubscription(modalId) {
  // 실제 청약 API 호출 로직
  ToastManager.show("청약 신청이 완료되었습니다.");
  closeSubscriptionModal(modalId);
}
