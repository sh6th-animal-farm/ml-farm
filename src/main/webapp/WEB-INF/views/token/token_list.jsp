<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/token_list.css" />

<div class="container">
  <div class="left-column">
    <div class="card">
      <h2 style="margin:0 0 10px 0; font-size: 18px;">김포 스마트팜A <small style="color:#888; font-weight:normal;">HSSJ01</small></h2>
      <div class="price-header">
        <span class="current-price">128,000</span>
        <span style="font-size: 14px; color:#888;">KRW</span>
        <span class="change-rate">+0.76% 전일대비</span>
      </div>
      <div class="info-row">
        <span>고가 <b style="color:#f04438;">128,300</b></span>
        <span>저가 <b style="color:#0047bb;">126,900</b></span>
        <span>거래량 <b>2,323</b></span>
      </div>
    </div>

    <div class="card">
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
          <td><b>김포 스마트팜 A</b> HSSJ01</td>
          <td style="text-align: right;">128,000</td>
          <td style="text-align: right; color:#f04438;">+0.76%</td>
        </tr>
        <tr>
          <td><b>평택 스마트팜 B</b> HSSJ02</td>
          <td style="text-align: right;">84,500</td>
          <td style="text-align: right; color:#0047bb;">-0.42%</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>

  <div class="right-column">
    <div class="card">
      <div class="tab-menu">
        <button class="tab-btn buy active" onclick="openTab(event, 'buy-tab')">매수</button>
        <button class="tab-btn sell" onclick="openTab(event, 'sell-tab')">매도</button>
        <button class="tab-btn" onclick="openTab(event, 'history-tab')">거래내역</button>
      </div>

      <div id="buy-tab" class="tab-content active">
        <div class="order-input-group">
          <label>주문유형</label>
          <select class="input-box">
            <option>시장가</option>
            <option>지정가</option>
          </select>
        </div>
        <div class="order-input-group">
          <label>총액</label>
          <input type="text" class="input-box" placeholder="예: 128000 KRW">
        </div>
        <div class="percentage-group">
          <button class="perc-btn">25%</button>
          <button class="perc-btn">50%</button>
          <button class="perc-btn">75%</button>
          <button class="perc-btn">100%</button>
        </div>
        <div class="summary-row">
          <span>주문 가능 금액</span>
          <span style="color:#333; font-weight:600;">1,400,000원</span>
        </div>
        <button class="btn-order btn-buy">매수 주문</button>
      </div>

      <div id="sell-tab" class="tab-content">
        <p style="text-align:center; color:#888; padding: 40px 0;">준비중입니다.</p>
      </div>

      <div id="history-tab" class="tab-content">
        <p style="text-align:center; color:#888; padding: 40px 0;">거래 내역이 없습니다.</p>
      </div>
    </div>

    <div class="card">
      <div class="tab-menu">
        <button class="tab-btn active">호가</button>
        <button class="tab-btn">체결</button>
      </div>
      <table class="hoga-table">
        <thead>
        <tr>
          <th>구분</th>
          <th style="text-align: right;">가격</th>
          <th style="text-align: right;">수량</th>
        </tr>
        </thead>
        <tbody>
        <tr><td style="color:#888;">매도</td><td class="hoga-sell" style="text-align: right;">128,800</td><td style="text-align: right;">1.22</td></tr>
        <tr><td style="color:#888;">매도</td><td class="hoga-sell" style="text-align: right;">128,700</td><td style="text-align: right;">1.88</td></tr>
        <tr><td style="color:#888;">매수</td><td class="hoga-buy" style="text-align: right;">127,900</td><td style="text-align: right;">2.21</td></tr>
        </tbody>
      </table>
    </div>
  </div>
</div>