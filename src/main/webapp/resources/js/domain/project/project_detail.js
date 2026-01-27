function openSubscriptionModal(modalId) {
  document.getElementById(modalId).style.display = "flex";
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
  alert("청약 신청이 완료되었습니다.");
  closeSubscriptionModal(modalId);
}
