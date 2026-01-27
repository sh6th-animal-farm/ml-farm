<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script src="https://cdn.iamport.kr/v1/iamport.js"></script>

<script>
  window.PORTONE_IMP_CODE = "${portoneImpCode}";
  window.PORTONE_CHANNEL_KEY = "${portoneChannelKey}";
</script>

<!-- Carbon Order Modal -->
<div id="carbonOrderModal" class="co-modal" aria-hidden="true">
  <div class="co-backdrop" onclick="closeOrderModal()"></div>

  <div class="co-dialog" role="dialog" aria-modal="true" aria-labelledby="coTitle">
    <div class="co-header">
      <h3 id="coTitle" class="co-title">주문 신청하기</h3>
      <button type="button" class="co-close" onclick="closeOrderModal()" aria-label="닫기">×</button>
    </div>

    <div class="co-body">
      <!-- 상품 요약 카드 -->
      <div class="co-product">
        <div class="co-badge">
          <span class="co-badge-top">탄소</span>
          <span class="co-badge-bottom">CREDIT</span>
        </div>

        <div class="co-product-info">
          <div id="coProductName" class="co-product-name">-</div>
          <div class="co-product-sub">
            단가 <b id="coUnitPrice">-</b>원 <span class="co-muted">(VAT 포함)</span>
          </div>
        </div>
      </div>

      <!-- 예치금(요청대로 제거) -->

      <!-- 최대 구매 가능 수량 -->
      <div class="co-row">
        <div class="co-row-left">최대 구매 가능 수량</div>
        <div class="co-row-right">
          <span id="coMaxQty" class="co-strong">-</span>
          <span class="co-unit">tCO2e</span>
        </div>
      </div>

      <!-- 주문 수량 -->
      <div class="co-section-title">주문 수량 입력</div>
      <div class="co-qty-wrap">
        <input id="coQtyInput" class="co-qty-input" type="number" min="1" step="1" value="1" />
        <span class="co-qty-unit">tCO2e</span>
      </div>
      <div id="coQtyHint" class="co-hint"></div>

      <!-- 금액 요약 -->
      <div class="co-summary">
        <div class="co-sum-row">
          <span>총 공급가액</span>
          <b id="coSupply">-</b>
        </div>
        <div class="co-sum-row">
          <span>부가세 (VAT 10%)</span>
          <b id="coVat">-</b>
        </div>

        <div class="co-divider"></div>

        <div class="co-sum-row co-total">
          <span>총 결제 금액 (VAT포함)</span>
          <b id="coTotal">-</b>
        </div>
      </div>

      <!-- 동의 -->
      <label class="co-agree">
        <input id="coAgree" type="checkbox" />
        <span>본 주문 건의 이용약관 및 탄소거래규정에 동의하며, 자산 매입 확약에 따른 결제를 진행합니다.</span>
      </label>

      <!-- 버튼 -->
      <button id="coSubmitBtn" type="button" class="co-submit" disabled onclick="submitCarbonOrder()">
        주문 완료하기
      </button>
    </div>
  </div>
</div>
