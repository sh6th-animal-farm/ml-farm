<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mypage.css">
<style>
    /* 거래 내역 전용 스타일 */
    .filter-container { display: flex; justify-content: space-between; align-items: center; margin-bottom: 32px; }
    .type-filters { display: flex; gap: 8px; }
    .btn-type { padding: 8px 16px; border-radius: 20px; border: 1px solid #F1F1F1; background: #fff; font-size: 13px; font-weight: 600; cursor: pointer; }
    .btn-type.active { background: var(--green-600); color: #fff; border-color: var(--green-600); }

    .period-select { 
        padding: 8px 12px; border: 1px solid #F1F1F1; border-radius: 8px; 
        font-size: 13px; color: var(--gray-700); cursor: pointer; outline: none;
    }

    /* 거래 내역 테이블 */
    .transaction-table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 16px; overflow: hidden; border: 1px solid #F1F1F1; }
    .transaction-table th { background: #FAFAFA; padding: 16px 24px; text-align: left; font-size: 13px; color: var(--gray-500); font-weight: 600; border-bottom: 1px solid #F1F1F1; }
    .transaction-table td { padding: 24px; border-bottom: 1px solid #F1F1F1; font-size: 14px; vertical-align: middle; }
    
    .t-date { color: var(--gray-900); white-space: nowrap; }
    .t-type { font-weight: 700; text-align: center; width: 80px; }
    .type-buy { color: #E53935; }    /* 매수 */
    .type-sell { color: #1E88E5; }   /* 매도 */
    .type-div { color: var(--gray-900); } /* 배당 */
    .type-sub { color: #43A047; }    /* 청약 */
    .type-ref { color: var(--gray-500); } /* 환불 */

    .t-name { font-weight: 700; color: var(--gray-900); margin-bottom: 4px; }
    .t-code { font-size: 12px; color: var(--gray-400); text-transform: uppercase; }
    
    .t-amount { font-weight: 700; text-align: right; }
    .t-balance { font-size: 12px; color: var(--gray-400); text-align: right; margin-top: 4px; }

    .btn-more-history { 
        width: 100%; padding: 16px; background: #fff; border: 1px solid #F1F1F1; 
        border-radius: 12px; color: var(--gray-900); font-weight: 600; margin-top: 24px; cursor: pointer;
    }
</style>

<div class="mypage-container">
    <div class="sidebar-wrapper">
        <jsp:include page="/WEB-INF/views/common/mypage_sidebar.jsp" />
    </div>

    <div class="content-wrapper">
        <div class="page-header">
            <h1>거래 내역</h1>
            <p>투자, 충전, 정산 등 모든 거래 기록을 확인하세요.</p>
        </div>

        <div class="filter-container">
            <div class="type-filters">
                <button class="btn-type active">전체보기</button>
                <button class="btn-type">토큰 매매</button>
                <button class="btn-type">배당</button>
                <button class="btn-type">청약</button>
                <button class="btn-type">환불</button>
            </div>
            <select class="period-select">
                <option>전체 기간</option>
                <option>최근 1개월</option>
                <option>최근 3개월</option>
                <option>최근 6개월</option>
            </select>
        </div>

        <table class="transaction-table">
            <thead>
                <tr>
                    <th>거래일시</th>
                    <th style="text-align: center;">구분</th>
                    <th>종목명</th>
                    <th style="text-align: right;">체결단가</th>
                    <th style="text-align: right;">거래수량</th>
                    <th style="text-align: right;">거래금액 / 거래후잔액</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td class="t-date">2023-12-24 14:22</td>
                    <td class="t-type type-buy">매수</td>
                    <td>
                        <div class="t-name">연천 킹스베리 딸기 01호</div>
                        <div class="t-code">YCKS01</div>
                    </td>
                    <td style="text-align: right; font-weight: 700;">45,000 원</td>
                    <td style="text-align: right; font-weight: 700;">+10,000 st</td>
                    <td>
                        <div class="t-amount">-87,545,000,000 원</div>
                        <div class="t-balance">12,450,000 원</div>
                    </td>
                </tr>
                <tr>
                    <td class="t-date">2023-12-24 14:22</td>
                    <td class="t-type type-div">배당</td>
                    <td>
                        <div class="t-name">연천 킹스베리 딸기 01호</div>
                        <div class="t-code">YCKS01</div>
                    </td>
                    <td style="text-align: right;">-</td>
                    <td style="text-align: right;">-</td>
                    <td>
                        <div class="t-amount">-87,545,000,000 원</div>
                        <div class="t-balance">12,450,000 원</div>
                    </td>
                </tr>
                <tr>
                    <td class="t-date">2023-12-24 14:22</td>
                    <td class="t-type type-sell">매도</td>
                    <td>
                        <div class="t-name">연천 킹스베리 딸기 01호</div>
                        <div class="t-code">YCKS01</div>
                    </td>
                    <td style="text-align: right; font-weight: 700;">45,000 원</td>
                    <td style="text-align: right; font-weight: 700;">+10,000 st</td>
                    <td>
                        <div class="t-amount">-87,545,000,000 원</div>
                        <div class="t-balance">12,450,000 원</div>
                    </td>
                </tr>
                <tr>
                    <td class="t-date">2023-12-24 14:22</td>
                    <td class="t-type type-sub">청약</td>
                    <td>
                        <div class="t-name">연천 킹스베리 딸기 01호</div>
                        <div class="t-code">YCKS01</div>
                    </td>
                    <td style="text-align: right; font-weight: 700;">45,000 원</td>
                    <td style="text-align: right;">-</td>
                    <td>
                        <div class="t-amount">-87,545,000,000 원</div>
                        <div class="t-balance">12,450,000 원</div>
                    </td>
                </tr>
            </tbody>
        </table>

        <button class="btn-more-history">+ 더보기</button>
    </div>
</div>