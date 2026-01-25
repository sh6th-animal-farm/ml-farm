<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>

<%@ attribute name="totalAsset" %>
<%@ attribute name="deposit" %>
<%@ attribute name="purchaseAmount" %>
<%@ attribute name="marketValue" %>
<%@ attribute name="unrealizedGain" %>
<%@ attribute name="returnPct" %>

<div class="investment-grid">
    <div class="stat-item">
        <span class="stat-label">총 자산 현황</span>
        <span class="stat-value">${totalAsset }<span class="stat-unit">원</span></span>
    </div>
    <div class="stat-item">
        <span class="stat-label">예수금</span>
        <span class="stat-value">${deposit } <span class="stat-unit">원</span></span>
    </div>
    <div class="stat-item">
        <span class="stat-label">매입금액</span>
        <span class="stat-value">${purchaseAmount } <span class="stat-unit">원</span></span>
    </div>
    <div class="stat-item">
        <span class="stat-label">평가금액</span>
        <span class="stat-value">${marketValue } <span class="stat-unit">원</span></span>
    </div>
    <div class="stat-item">
        <span class="stat-label">평가손익</span>
        <span class="stat-value text-plus">${unrealizedGain } <span class="stat-unit">원</span></span>
    </div>
    <div class="stat-item">
        <span class="stat-label">수익률</span>
        <span class="stat-value text-plus">${returnPct }</span>
    </div>
</div>

<style>
/* 투자 현황 그리드 */
.investment-grid {
    display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px;
    background: #fff;  border-radius: var(--radius-l);
    padding: 24px; margin-bottom: 48px; box-shadow:var(--shadow);
}
.stat-item { display: flex; flex-direction: column; gap: 4px; }
.stat-label { font:var(--font-caption-01); color: var(--gray-400); }
.stat-value { font:var(--font-subtitle-01); }
.stat-unit { font:var(--font-caption-01); color: var(--gray-400); }
</style>