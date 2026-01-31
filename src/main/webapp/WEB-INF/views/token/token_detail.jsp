<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="mp" tagdir="/WEB-INF/tags/token" %>

<script>
    window.tokenId = ${tokenId};
    window.orderBuyList = ${orderBuyList};
    window.orderSellList = ${orderSellList};
    window.tradeList = ${tradeList};

    console.log("tokenId: ", window.tokenId);
    console.log("호가(매수): ", window.orderBuyList);
    console.log("호가(매도): ", window.orderSellList);
    console.log("체결: ", window.tradeList);
</script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/token_detail.css"/>
<script src="${pageContext.request.contextPath}/resources/js/util/lightweight-charts.js"></script>
<script type="module" src="${pageContext.request.contextPath}/resources/js/domain/token/token_detail.js"></script>

<div class="token-container">
    <div class="content-wrapper">
        <div class="left-column">
            <mp:token_chart/>
            <div class="card list-card">
                <div class="scroll active">
                    <table class="token-list-table">
                        <thead>
                        <tr>
                            <th>종목</th>
                            <th style="text-align: right;">현재가</th>
                            <th style="text-align: right;">등락률</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td>김포 스마트팜 A<span class="token-code">HSSJ01</span></td>
                            <td style="text-align: right;">128,000</td>
                            <td style="text-align: right; color: var(--error);">+0.76%</td>
                        </tr>
                        <tr>
                            <td>평택 스마트팜 B<span class="token-code">HSSJ02</span></td>
                            <td style="text-align: right;">14,500</td>
                            <td style="text-align: right; color: var(--info);">-0.42%</td>
                        </tr>
                        <tr>
                            <td>김포 스마트팜 A<span class="token-code">HSSJ03</span></td>
                            <td style="text-align: right;">128,000</td>
                            <td style="text-align: right; color: var(--error);">+0.76%</td>
                        </tr>
                        <tr>
                            <td>평택 스마트팜 B<span class="token-code">HSSJ04</span></td>
                            <td style="text-align: right;">14,500</td>
                            <td style="text-align: right; color: var(--info);">-0.42%</td>
                        </tr>
                        <tr>
                            <td>김포 스마트팜 A<span class="token-code">HSSJ01</span></td>
                            <td style="text-align: right;">128,000</td>
                            <td style="text-align: right; color: var(--error);">+0.76%</td>
                        </tr>
                        <tr>
                            <td>평택 스마트팜 B<span class="token-code">HSSJ02</span></td>
                            <td style="text-align: right;">14,500</td>
                            <td style="text-align: right; color: var(--info);">-0.42%</td>
                        </tr>
                        <tr>
                            <td>김포 스마트팜 A<span class="token-code">HSSJ03</span></td>
                            <td style="text-align: right;">128,000</td>
                            <td style="text-align: right; color: var(--error);">+0.76%</td>
                        </tr>
                        <tr>
                            <td>평택 스마트팜 B<span class="token-code">HSSJ04</span></td>
                            <td style="text-align: right;">14,500</td>
                            <td style="text-align: right; color: var(--info);">-0.42%</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="right-column">
            <div class="card order-card">
                <div class="tab-menu">
                    <button class="order-btn buy active" data-tab="buy-tab">매수</button>
                    <button class="order-btn sell" data-tab="sell-tab">매도</button>
                    <button class="order-btn" data-tab="history-tab">미체결</button>
                </div>

                <div id="buy-tab" class="tab-content-wrapper active">
                    <div class="tab-content">
                        <div class="order-input-group">
                            <span class="order-label">주문 유형</span>
                            <select class="input-box order-type">
                                <option value="LIMIT">지정가</option>
                                <option value="MARKET">시장가</option>
                            </select>
                        </div>
                        <div class="order-input-group price-group">
                            <span class="order-label">매수 가격</span>
                            <input type="text" class="input-box" placeholder="예: 128000">
                        </div>
                        <div class="order-input-group volume-group">
                            <span class="order-label">주문 수량</span>
                            <input type="text" class="input-box" placeholder="예: 1.3875">
                        </div>
                        <div class="order-input-group amount-group" style="display: none;">
                            <span class="order-label">주문 총액</span>
                            <input type="text" class="input-box" placeholder="예: 473200">
                        </div>
                        <div class="percentage-group">
                            <button class="perc-btn buy-perc">25%</button>
                            <button class="perc-btn buy-perc">50%</button>
                            <button class="perc-btn buy-perc">75%</button>
                            <button class="perc-btn buy-perc">100%</button>
                        </div>
                        <div class="summary-row">
                            <span>총 주문 금액</span>
                            <span class="total-amount" style="color:var(--error)">0원</span>
                        </div>
                        <button class="btn-order btn-buy">매수</button>
                    </div>
                </div>

                <div id="sell-tab" class="tab-content-wrapper">
                    <div class="tab-content">
                        <div class="order-input-group">
                            <span class="order-label">주문 유형</span>
                            <select class="input-box order-type">
                                <option value="LIMIT">지정가</option>
                                <option value="MARKET">시장가</option>
                            </select>
                        </div>
                        <div class="order-input-group price-group">
                            <span class="order-label">매도 가격</span>
                            <input type="text" class="input-box" placeholder="예: 128000">
                        </div>
                        <div class="order-input-group volume-group">
                            <span class="order-label">주문 수량</span>
                            <input type="text" class="input-box" placeholder="예: 1.3875">
                        </div>
                        <div class="percentage-group">
                            <button class="perc-btn sell-perc">25%</button>
                            <button class="perc-btn sell-perc">50%</button>
                            <button class="perc-btn sell-perc">75%</button>
                            <button class="perc-btn sell-perc">100%</button>
                        </div>
                        <div class="summary-row">
                            <span>총 주문 수량</span>
                            <span class="total-amount" style="color:var(--info)">0</span>
                        </div>
                        <button class="btn-order btn-sell">매도</button>
                    </div>
                </div>

                <div id="history-tab" class="tab-content-wrapper">
                    <div class="history-summary">
                        <div>총 0건</div>
                        <div>최신순</div>
                    </div>
                    <div class="scroll active">
                        <ul class="transaction-list">
                        </ul>
                    </div>
                </div>
            </div>

            <div class="card trade-card">
                <div class="tab-menu">
                    <button class="trade-btn active" data-tab="order-tab">호가</button>
                    <button class="trade-btn" data-tab="trade-tab">체결</button>
                </div>
                <div id="order-tab" class="scroll active">
                    <table class="hoga-table">
                        <thead>
                        <tbody id="order-hist-body">
                        </tbody>
                    </table>
                </div>

                <div id="trade-tab" class="scroll">
                    <table class="hoga-table">
                        <thead>
                        <tbody id="trade-hist-body">
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
