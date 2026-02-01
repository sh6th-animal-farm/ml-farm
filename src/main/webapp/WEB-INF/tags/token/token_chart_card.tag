<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="card chart-view-card">
    <div class="chart-header">
        <div class="header-left">
            <h3 class="token-id" id="panel-ticker-symbol">KPYN01</h3>
            <p class="token-desc" id="panel-token-name">김포 스마트팜 A · 딸기</p></div>
        <div class="header-right">
            <div class="current-price" id="panel-market-price">0</div>
            <div class="price-change" id="panel-change-rate">0.00%</div>
        </div>
    </div>

    <div class="divider"></div>

    <div class="chart-section">
        <div class="chart-meta">
            <span class="label-badge">1분봉</span>
            <span class="time-info">최근 1시간</span>
        </div>
        <div class="chart-container" id="tokenDetailChart"></div>
    </div>

    <div class="info-grid">
        <div class="info-item">
            <span class="info-label">시가</span>
            <span class="info-value" id="panel-open-price">0</span>
        </div>
        <div class="info-item">
            <span class="info-label">고가</span>
            <span class="info-value" id="panel-high-price">0</span>
        </div>
        <div class="info-item">
            <span class="info-label">저가</span>
            <span class="info-value" id="panel-low-price">0</span>
        </div>
        <div class="info-item">
            <span class="info-label">거래대금</span>
            <span class="info-value" id="panel-daily-volume">0</span>
        </div>
    </div>
</div>

<style>
    /* 디자인 가이드 기반 스타일링 */
    :root {
        --primary-green: #4A9F2E; /* Green 600 */
        --gray-900: #191919; /* Title Text */
        --gray-600: #404040; /* Body Text */
        --gray-100: #F2F2F2; /* Border */
        --gray-0: #FCFCFC; /* Card Background */
        --semantic-red: #E53935; /* Error/Danger/Price Up */
        --radius-l: 12px; /* Standard Card */
    }

    .chart-view-card {
        border-radius: var(--radius-l);
        padding: 24px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
        font-family: 'Pretendard', sans-serif;
        letter-spacing: -0.02em;
    }

    /* 헤더 섹션 */
    .chart-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 16px;
    }

    .token-id {
        font-size: 22px;
        font-weight: 700;
        color: var(--gray-900);
        margin: 0 0 4px 0;
    }

    .token-desc {
        font-size: 14px;
        color: var(--gray-600);
        margin: 0;
    }

    .header-right {
        text-align: right;
    }

    .current-price {
        font-size: 24px;
        font-weight: 700;
        color: var(--gray-900);
    }

    .price-change.positive {
        color: var(--semantic-red);
        font-size: 16px;
        font-weight: 600;
        margin-top: 2px;
    }

    .divider {
        height: 1px;
        background-color: var(--gray-100);
        margin: 16px 0;
    }

    /* 차트 섹션 */
    .chart-section {
        margin-bottom: 24px;
    }

    .chart-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;
    }

    .label-badge {
        background: var(--gray-100);
        padding: 4px 8px;
        border-radius: 4px;
        font-size: 12px;
        font-weight: 600;
        color: var(--gray-600);
    }

    .time-info {
        font-size: 12px;
        color: var(--gray-400);
    }

    .chart-container {
        width: 100%;
        height: 180px;
        background: transparent;
        /*background: linear-gradient(180deg, rgba(211, 245, 187, 0.1) 0%, rgba(255, 255, 255, 0) 100%); !* Green 0 보조색 활용 *!*/
        border-radius: 8px;
        position: relative;
    }

    /* 하단 상세 지표 그리드 */
    .info-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 12px;
        padding-top: 16px;
        border-top: 1px solid var(--gray-100);
    }

    .info-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .info-label {
        font-size: 14px;
        color: var(--gray-400);
    }

    .info-value {
        font-size: 14px;
        color: var(--gray-900);
        font-weight: 500;
    }
</style>
