<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/token_detail.css" />
<script type="module" src="${pageContext.request.contextPath}/resources/js/domain/token/token_detail.js"></script>

<div class="token-container">
<%--  <div class="head-wrapper">--%>
<%--    <t:section_header title="김포 스마트팜A" subtitle="HSSJ01"/>--%>
<%--  </div>--%>
  <div class="content-wrapper">
    <div class="left-column">
      <div class="card chart-card">
        <div class="token-header">
          <div class="left-header">
            <div class="token-title">
              <h2>김포 스마트팜A</h2>
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
        <div class="token-chart">
          차트...
        </div>
      </div>

      <div class="card list-card">
        <div class="scroll">
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

      <div class="card trade-card">
        <div class="tab-menu">
          <button class="trade-btn active">호가</button>
          <button class="trade-btn">체결</button>
        </div>
        <div class="scroll">
          <table class="hoga-table">
            <thead>
              <tbody>
                <tr><td class="trade-dir">매도</td><td class="hoga-sell" style="text-align: right;">128,800</td><td style="text-align: right;">1.22</td></tr>
                <tr><td class="trade-dir">매도</td><td class="hoga-sell" style="text-align: right;">128,700</td><td style="text-align: right;">1.88</td></tr>
                <tr><td class="trade-dir">매수</td><td class="hoga-buy" style="text-align: right;">127,900</td><td style="text-align: right;">2.21</td></tr>
                <tr><td class="trade-dir">매도</td><td class="hoga-sell" style="text-align: right;">128,800</td><td style="text-align: right;">1.22</td></tr>
                <tr><td class="trade-dir">매도</td><td class="hoga-sell" style="text-align: right;">128,700</td><td style="text-align: right;">1.88</td></tr>
                <tr><td class="trade-dir">매수</td><td class="hoga-buy" style="text-align: right;">127,900</td><td style="text-align: right;">2.21</td></tr>
                <tr><td class="trade-dir">매도</td><td class="hoga-sell" style="text-align: right;">128,800</td><td style="text-align: right;">1.22</td></tr>
                <tr><td class="trade-dir">매도</td><td class="hoga-sell" style="text-align: right;">128,700</td><td style="text-align: right;">1.88</td></tr>
                <tr><td class="trade-dir">매수</td><td class="hoga-buy" style="text-align: right;">127,900</td><td style="text-align: right;">2.21</td></tr>
              </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</div>