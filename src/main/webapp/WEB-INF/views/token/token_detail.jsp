<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/token_detail.css" />
<script type="module" src="${pageContext.request.contextPath}/resources/js/domain/token/token_detail.js"></script>

<div class="token-container">
  <div class="content-wrapper">
    <div class="left-column">
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
          <button class="order-btn buy active" data-tab="buy-tab">매수</button>
          <button class="order-btn sell" data-tab="sell-tab">매도</button>
          <button class="order-btn" data-tab="history-tab">거래내역</button>
        </div>

        <div id="buy-tab" class="tab-content-wrapper active">
          <div class="tab-content">
            <div class="order-input-group">
              <span class="order-label">주문유형</span>
              <select class="input-box">
                <option>시장가</option>
                <option>지정가</option>
              </select>
            </div>
            <div class="order-input-group">
              <span class="order-label">매수 가격</span>
              <input type="text" class="input-box" placeholder="예: 128000 KRW">
            </div>
            <div class="order-input-group">
              <span class="order-label">주문 수량</span>
              <input type="text" class="input-box" placeholder="예: 1.3875 개">
            </div>
            <div class="percentage-group">
              <button class="perc-btn buy-perc">25%</button>
              <button class="perc-btn buy-perc">50%</button>
              <button class="perc-btn buy-perc">75%</button>
              <button class="perc-btn buy-perc">100%</button>
            </div>
            <div class="summary-row">
              <span>총 주문 금액</span>
              <span style="color:var(--error)">1,400,000원</span>
            </div>
            <button class="btn-order btn-buy">매수</button>
          </div>
        </div>

        <div id="sell-tab" class="tab-content-wrapper">
          <div class="tab-content">
            <div class="order-input-group">
              <span class="order-label">주문유형</span>
              <select class="input-box">
                <option>시장가</option>
                <option>지정가</option>
              </select>
            </div>
            <div class="order-input-group">
              <span class="order-label">매도 가격</span>
              <input type="text" class="input-box" placeholder="예: 128000 KRW">
            </div>
            <div class="order-input-group">
              <span class="order-label">주문 수량</span>
              <input type="text" class="input-box" placeholder="예: 1.3875 개">
            </div>
            <div class="percentage-group">
              <button class="perc-btn sell-perc">25%</button>
              <button class="perc-btn sell-perc">50%</button>
              <button class="perc-btn sell-perc">75%</button>
              <button class="perc-btn sell-perc">100%</button>
            </div>
            <div class="summary-row">
              <span>총 주문 금액</span>
              <span style="color:var(--info)">1,400,000원</span>
            </div>
            <button class="btn-order btn-sell">매도</button>
          </div>
        </div>

        <div id="history-tab" class="tab-content-wrapper">
          <div class="history-summary">
            <div>총 2건</div>
            <div>최신순</div>
          </div>
          <div class="scroll">
            <ul class="transaction-list">
              <li class="transaction-item">
                <div class="item-hover-layer">
                  <div class="trashcan-box">
                    <t:icon name="trashcan" size="48" className="trashcan"></t:icon>
                  </div>
                </div>

                <div class="trade-title">
                  <div class="item-header">
                    <span class="asset-name">HSSJ01/KRW</span>
                    <span class="trade-type sell">매도</span>
                  </div>
                  <div class="trade-date">2026-01-25 19:09:55</div>
                </div>

                <div class="trade-info">
                  <div class="trade-info-row">
                    <span class="label">주문가격</span>
                    <span class="value">5,000.0</span>
                  </div>
                  <div class="trade-info-row">
                    <span class="label">주문수량</span>
                    <span class="value">1.00000</span>
                  </div>
                  <div class="trade-info-row">
                    <span class="label">미체결량</span>
                    <span class="value">0.4390</span>
                  </div>
                </div>
              </li>
              <li class="transaction-item">
                <div class="item-hover-layer">
                  <div class="trashcan-box">
                    <t:icon name="trashcan" size="48" className="trashcan"></t:icon>
                  </div>
                </div>

                <div class="trade-title">
                  <div class="item-header">
                    <span class="asset-name">HSSJ01/KRW</span>
                    <span class="trade-type buy">매수</span>
                  </div>
                  <div class="trade-date">2026-01-25 19:09:55</div>
                </div>

                <div class="trade-info">
                  <div class="trade-info-row">
                    <span class="label">주문가격</span>
                    <span class="value">5,000.0</span>
                  </div>
                  <div class="trade-info-row">
                    <span class="label">주문수량</span>
                    <span class="value">1.00000</span>
                  </div>
                  <div class="trade-info-row">
                    <span class="label">미체결량</span>
                    <span class="value">0.9021</span>
                  </div>
                </div>
              </li>
              <li class="transaction-item">
                <div class="item-hover-layer">
                  <div class="trashcan-box">
                    <t:icon name="trashcan" size="48" className="trashcan"></t:icon>
                  </div>
                </div>

                <div class="trade-title">
                  <div class="item-header">
                    <span class="asset-name">HSSJ01/KRW</span>
                    <span class="trade-type sell">매도</span>
                  </div>
                  <div class="trade-date">2026-01-25 19:09:55</div>
                </div>

                <div class="trade-info">
                  <div class="trade-info-row">
                    <span class="label">주문가격</span>
                    <span class="value">5,000.0</span>
                  </div>
                  <div class="trade-info-row">
                    <span class="label">주문수량</span>
                    <span class="value">1.0000</span>
                  </div>
                  <div class="trade-info-row">
                    <span class="label">미체결량</span>
                    <span class="value">0.3786</span>
                  </div>
                </div>
              </li>
            </ul>
          </div>
        </div>
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