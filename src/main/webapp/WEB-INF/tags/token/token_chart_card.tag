<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="card order-card">
    <div class="tab-menu">
        <button class="order-btn buy active" onclick="openTab(event, 'buy-tab')">매수</button>
        <button class="order-btn sell" onclick="openTab(event, 'sell-tab')">매도</button>
        <button class="order-btn" onclick="openTab(event, 'history-tab')">거래내역</button>
    </div>

    <div id="buy-tab" class="tab-content">
        <div class="order-input-group">
            <span class="order-label">주문유형</span>
            <select class="input-box">
                <option>시장가</option>
                <option>지정가</option>
            </select>
        </div>
        <div class="order-input-group">
            <span class="order-label">총액</span>
            <input type="text" class="input-box" placeholder="예: 128000 KRW">
        </div>
        <div class="percentage-group">
            <button class="perc-btn">25%</button>
            <button class="perc-btn">50%</button>
            <button class="perc-btn">75%</button>
            <button class="perc-btn">100%</button>
        </div>
        <div class="summary-row">
            <span>총 주문 금액</span>
            <span style="color:#333; font-weight:600;">1,400,000원</span>
        </div>
        <button class="btn-order btn-buy">매수</button>
    </div>

    <%--        <div id="sell-tab" class="tab-content">--%>
    <%--          <p style="text-align:center; color:#888; padding: 40px 0;">준비중입니다.</p>--%>
    <%--        </div>--%>

    <%--        <div id="history-tab" class="tab-content">--%>
    <%--          <p style="text-align:center; color:#888; padding: 40px 0;">거래 내역이 없습니다.</p>--%>
    <%--        </div>--%>
</div>
