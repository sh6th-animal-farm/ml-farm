<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/profile.css">
<style>
    /* 전자지갑 전용 스타일 */
    .wallet-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 32px; }
    .btn-link-account { 
        background: #1A1D23; color: #fff; border: none; padding: 10px 20px; 
        border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer;
        display: flex; align-items: center; gap: 8px;
    }

    /* 상단 계좌 카드 */
    .account-card {
        display: flex; justify-content: space-between; align-items: center;
        padding: 24px 32px; background: #fff; border: 1px solid #F1F1F1;
        border-radius: 16px; margin-bottom: 24px; box-shadow: var(--shadow);
    }
    .bank-info { display: flex; align-items: center; gap: 16px; }
    .bank-icon { width: 40px; height: 40px; background: #F5F5F5; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 20px; }
    .bank-name { font-size: 13px; color: var(--gray-400); margin-bottom: 4px; }
    .account-number { font-size: 18px; font-weight: 700; color: var(--gray-900); }
    .total-amount { font-size: 24px; font-weight: 800; }

    /* 투자 현황 그리드 */
    .investment-grid {
        display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px;
        background: #fff; border: 1px solid #F1F1F1; border-radius: 16px;
        padding: 32px; margin-bottom: 40px;
    }
    .stat-item { display: flex; flex-direction: column; gap: 8px; }
    .stat-label { font-size: 13px; color: var(--gray-400); }
    .stat-value { font-size: 18px; font-weight: 700; }
    .text-plus { color: #E53935; }
    .text-minus { color: #1E88E5; }

    /* 보유 토큰 테이블 */
    .token-section-title { font-size: 20px; font-weight: 700; margin-bottom: 20px; display: flex; align-items: center; gap: 8px; }
    .token-count { color: var(--green-600); }
    
    .token-table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 16px; overflow: hidden; border: 1px solid #F1F1F1; }
    .token-table th { background: #FAFAFA; padding: 16px 24px; text-align: left; font-size: 13px; color: var(--gray-500); font-weight: 600; border-bottom: 1px solid #F1F1F1; }
    .token-table td { padding: 24px; border-bottom: 1px solid #F1F1F1; vertical-align: middle; }
    
    .token-name { font-size: 15px; font-weight: 700; color: var(--gray-900); margin-bottom: 4px; }
    .token-code { font-size: 12px; color: var(--gray-400); }
    .price-sub { font-size: 12px; color: var(--gray-400); margin-top: 4px; }
    .token-amount { font-size: 15px; font-weight: 700; text-align: right; }

    .btn-more-wallet { 
        width: 100%; padding: 16px; background: #fff; border: 1px solid #F1F1F1; 
        border-radius: 12px; color: var(--gray-900); font-weight: 600; margin-top: 24px; cursor: pointer;
    }
</style>

<div class="mypage-container">
    <div class="sidebar-wrapper">
        <jsp:include page="/WEB-INF/views/common/mypage_sidebar.jsp" />
    </div>

    <div class="content-wrapper">
        <div class="wallet-header">
            <div class="page-header" style="margin-bottom: 0;">
                <h1>나의 전자지갑</h1>
                <p>연동된 증권 계좌와 실시간 투자 현황을 확인하세요.</p>
            </div>
            <button class="btn-link-account">🔗 계좌 연동</button>
        </div>

        <div class="account-card">
            <div class="bank-info">
                <div class="bank-icon">🏦</div>
                <div>
                    <p class="bank-name">kh증권</p>
                    <p class="account-number">570802-04-021849</p>
                </div>
            </div>
            <div class="total-amount">12,450,000 원</div>
        </div>

        <div class="investment-grid">
            <div class="stat-item">
                <span class="stat-label">총 자산 현황</span>
                <span class="stat-value">62,520,000 원</span>
            </div>
            <div class="stat-item">
                <span class="stat-label">예수금</span>
                <span class="stat-value">12,450,000 원</span>
            </div>
            <div class="stat-item">
                <span class="stat-label">매입금액</span>
                <span class="stat-value">46,500,000 원</span>
            </div>
            <div class="stat-item" style="margin-top: 16px;">
                <span class="stat-label">평가금액</span>
                <span class="stat-value">50,070,000 원</span>
            </div>
            <div class="stat-item" style="margin-top: 16px;">
                <span class="stat-label">평가손익</span>
                <span class="stat-value text-plus">+3,570,000 원</span>
            </div>
            <div class="stat-item" style="margin-top: 16px;">
                <span class="stat-label">수익률</span>
                <span class="stat-value text-plus">+7.67%</span>
            </div>
        </div>

        <h2 class="token-section-title">보유 토큰 <span class="token-count">6</span></h2>
        
        <table class="token-table">
            <thead>
                <tr>
                    <th>종목명</th>
                    <th style="text-align: right;">평가손익 / 수익률</th>
                    <th style="text-align: right;">평가금액 / 매입금액</th>
                    <th style="text-align: right;">보유수량</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>
                        <div class="token-name">연천 킹스베리 딸기 01호</div>
                        <div class="token-code">Farm-st-001</div>
                    </td>
                    <td style="text-align: right;">
                        <div class="text-plus" style="font-weight: 700;">+710,000 원</div>
                        <div class="text-plus" style="font-size: 12px;">+14.20 %</div>
                    </td>
                    <td style="text-align: right;">
                        <div style="font-weight: 700;">5,710,000 원</div>
                        <div class="price-sub">5,000,000 원</div>
                    </td>
                    <td class="token-amount">5,000 st</td>
                </tr>
                <tr>
                    <td>
                        <div class="token-name">영암 바이오매스 열분해 01호</div>
                        <div class="token-code">Eco-st-024</div>
                    </td>
                    <td style="text-align: right;">
                        <div class="text-minus" style="font-weight: 700;">-38,400 원</div>
                        <div class="text-minus" style="font-size: 12px;">-1.20 %</div>
                    </td>
                    <td style="text-align: right;">
                        <div style="font-weight: 700;">3,161,600 원</div>
                        <div class="price-sub">2,000,000 원</div>
                    </td>
                    <td class="token-amount">3,200 st</td>
                </tr>
            </tbody>
        </table>

        <button class="btn-more-wallet">+ 더보기</button>
    </div>
</div>