<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>

<%@ attribute name="totalAsset" %>
<%@ attribute name="deposit" %>
<%@ attribute name="purchaseAmount" %>
<%@ attribute name="marketValue" %>
<%@ attribute name="unrealizedGain" %>
<%@ attribute name="returnPct" %>

<div class="investment-grid">
    <div class="grid-item">
        <span class="grid-label">총 자산 현황</span>
        <span class="grid-value" id="totalAsset">${totalAsset }<span class="grid-unit">원</span></span>
    </div>
    <div class="grid-item">
        <span class="grid-label">예수금</span>
        <span class="grid-value" id="deposit">${deposit } <span class="grid-unit">원</span></span>
    </div>
    <div class="grid-item">
        <span class="grid-label">매입금액</span>
        <span class="grid-value" id="purchaseAmount">${purchaseAmount } <span class="grid-unit">원</span></span>
    </div>
    <div class="grid-item">
        <span class="grid-label">평가금액</span>
        <span class="grid-value" id="marketValue">${marketValue } <span class="grid-unit">원</span></span>
    </div>
    <div class="grid-item">
        <span class="grid-label">평가손익</span>
        <span class="grid-value text-plus" id="profitLoss">${unrealizedGain } <span class="grid-unit">원</span></span>
    </div>
    <div class="grid-item">
        <span class="grid-label">수익률</span>
        <span class="grid-value text-plus" id="profitLossRate">${returnPct }</span>
    </div>
</div>

<style>
/* 투자 현황 그리드 */
.investment-grid {
    display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px;
    background: #fff;  border-radius: var(--radius-l);
    padding: 24px; margin-bottom: 48px; box-shadow:var(--shadow);
}
.grid-item { display: flex; flex-direction: column; gap: 4px; }
.grid-label { font:var(--font-caption-01); color: var(--gray-400); }
.grid-value { font:var(--font-subtitle-01); }
.grid-unit { font:var(--font-caption-01); color: var(--gray-400); }
</style>