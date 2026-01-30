<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="card chart-card">
    <div class="token-header">
        <div class="left-header">
            <div class="token-title">
                <h2>설향 딸기 1호</h2>
                <p>HSSJ01</p>
            </div>
            <div class="token-summary">
                <div class="price-header">
                    <div class="current-price">
                        <span class="price">128,000</span>
                        <span class="unit">KRW</span>
                    </div>
                    <span class="change-rate">+0.76% 전일대비</span>
                </div>
                <div class="info-row">
                    <span>고가 <b style="color:var(--error);">128,300</b></span>
                    <span>저가 <b style="color:var(--info);">126,900</b></span>
                    <span>거래량 <b>2,323</b></span>
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
