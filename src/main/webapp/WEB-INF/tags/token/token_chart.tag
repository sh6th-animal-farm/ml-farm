<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="card chart-card">
    <div class="token-header">
        <div class="left-header">
            <div class="token-title">
                <h2>${ohlcv.tokenName}</h2>
                <p>${ohlcv.tickerSymbol}</p>
            </div>
            <div class="token-summary">
                <div class="price-header">
                    <div class="current-price">
                        <span class="price">
                            <fmt:formatNumber value="${ohlcv != null && ohlcv.marketPrice != null ? ohlcv.marketPrice : 0}" pattern="#,###"/>
                        </span>
                        <span class="unit">KRW</span>
                    </div>
                    <span class="change-rate" style="color: ${ohlcv != null && ohlcv.changeRate > 0 ? 'var(--error)' : 'var(--info)'}">
                        <c:if test="${ohlcv != null && ohlcv.changeRate > 0}">+</c:if>
                        <fmt:formatNumber value="${ohlcv != null ? ohlcv.changeRate : 0}" pattern="0.00"/>% 전일대비
                    </span>
                </div>
                <div class="info-row">
                    <span>고가 <fmt:formatNumber value="${ohlcv != null ? ohlcv.highPrice : 0}" pattern="#,###"/></span>
                    <span>저가 <fmt:formatNumber value="${ohlcv != null ? ohlcv.lowPrice : 0}" pattern="#,###"/></span>
                    <span>거래량 <fmt:formatNumber value="${ohlcv != null ? ohlcv.dailyTradeVolume : 0}" pattern="#,###"/></span>
                </div>
            </div>
        </div>
        <div class="right-header">
            <div class="period-tab">
                <button class="period-btn active">1M</button>
                <button class="period-btn">1H</button>
                <button class="period-btn">3H</button>
                <button class="period-btn">24H</button>
            </div>
        </div>
    </div>
    <div class="token-chart"></div>
</div>
