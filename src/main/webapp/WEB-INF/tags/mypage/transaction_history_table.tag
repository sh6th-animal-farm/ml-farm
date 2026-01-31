<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ attribute name="transactionList" type="java.util.List" required="true"  %>

<table class="transaction-table">
    <colgroup>
        <col width="180px" /> <%-- 거래일시 --%>
        <col width="80px" />  <%-- 구분 --%>
        <col width="*" />     <%-- 종목명 (나머지 공간 차지) --%>
        <col width="120px" /> <%-- 체결단가 --%>
        <col width="100px" /> <%-- 거래수량 --%>
        <col width="200px" /> <%-- 거래금액/잔액 --%>
    </colgroup>
    <thead>
        <tr>
            <th class="head-first th-date">거래일시</th>
            <th class="th-type" style="text-align: center;">구분</th>
            <th>종목명</th>
            <th class="th-price" style="text-align: right;">체결단가</th>
            <th class="th-amount" style="text-align: right;">거래수량</th>
            <th class="head-last th-balance" style="text-align: right;">거래금액 / 거래후잔액</th>
        </tr>
    </thead>
    <tbody>
        <c:choose>
            <c:when test="${not empty transactionList}">
                <c:forEach var="tx" items="${transactionList}">
                    <tr>
                        <td class="t-date">
                            <%-- 날짜와 시간이 한 줄에 나오도록 공백 처리 --%>
                            <fmt:formatDate value="${tx.transactionAt}" pattern="yyyy-MM-dd HH:mm"/>
                        </td>
                        <%-- 거래 타입별 클래스 지정 --%>
                        <c:set var="typeClass" value=""/>
                        <c:choose>
                            <c:when test="${tx.type == 'BUY'}"><c:set var="typeClass" value="type-buy"/></c:when>
                            <c:when test="${tx.type == 'SELL'}"><c:set var="typeClass" value="type-sell"/></c:when>
                            <c:when test="${tx.type == 'DIVIDEND'}"><c:set var="typeClass" value="type-div"/></c:when>
                            <c:when test="${tx.type == 'SUBSCRIPTION'}"><c:set var="typeClass" value="type-sub"/></c:when>
                            <c:when test="${tx.type == 'REFUND'}"><c:set var="typeClass" value="type-ref"/></c:when>
                        </c:choose>
                        
                        <td class="t-type ${typeClass}">${tx.typeLabel}</td>
                        
                        <td>
                            <div class="t-name">${tx.projectName}</div>
                            <div class="t-code">${tx.projectCode}</div>
                        </td>
                        
                        <td style="text-align: right; font-weight: 700;">
                            <c:choose>
                                <c:when test="${not empty tx.unitPrice}">
                                    <fmt:formatNumber value="${tx.unitPrice}" type="number"/> 원
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                        
                        <td style="text-align: right; font-weight: 700;">
                            <c:choose>
                                <c:when test="${not empty tx.quantity && tx.quantity != 0}">
                                    ${tx.quantity > 0 ? '+' : ''}<fmt:formatNumber value="${tx.quantity}" type="number"/> st
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                        
                        <td>
                            <div class="t-amount">
                                ${tx.amount > 0 ? '+' : ''}<fmt:formatNumber value="${tx.amount}" type="number"/> 원
                            </div>
                            <div class="t-balance">
                                <fmt:formatNumber value="${tx.balanceAfter}" type="number"/> 원
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <tr>
                    <td colspan="6" style="text-align: center; padding: 60px; color: var(--gray-400);">
                        거래 내역이 존재하지 않습니다.
                    </td>
                </tr>
            </c:otherwise>
        </c:choose>
    </tbody>
</table>

<style>
/* 거래 내역 테이블 */
.transaction-table { 
    width: 100%; 
    table-layout: fixed; /* 컬럼 너비 고정 */
    border-collapse: collapse; 
    background: #fff; 
    border-radius: var(--radius-l); 
    overflow: hidden; 
    box-shadow: var(--shadow); 
}

/* 헤더 스타일 */
.transaction-table th { 
    background: var(--gray-50); 
    padding: 20px 8px; 
    text-align: left; 
    color: var(--gray-500); 
    font: var(--font-button-02); 
    border-bottom: 1px solid #F1F1F1; 
    box-sizing: border-box; 
}

/* 첫 번째, 마지막 컬럼 패딩 */
.head-first { padding-left: 24px !important; }
.head-last { padding-right: 24px !important; }

/* 데이터 셀 스타일 */
.transaction-table td { 
    padding: 20px 8px; 
    border-bottom: 1px solid #F1F1F1; 
    vertical-align: middle; 
}
.transaction-table td:first-child { padding-left: 24px; }
.transaction-table td:last-child { padding-right: 24px; }

/* [수정] 날짜: 너비를 늘리고 줄바꿈 방지 */
.t-date { 
    font: var(--font-body-01); 
    color: var(--gray-900);
    white-space: nowrap; /* 시간 떨어짐 방지 */
}

/* [수정] 구분: 중앙 정렬 */
.t-type { 
    font: var(--font-body-03); 
    text-align: center; 
}

/* 구분 색상 */
.type-buy { color: var(--error); }    /* 매수 */
.type-sell { color: var(--info); }   /* 매도 */
.type-div { color: var(--gray-900); } /* 배당 */
.type-sub { color: var(--green-600); }    /* 청약 */
.type-ref { color: var(--gray-900); } /* 환불 */

/* 종목명 */
.t-name { font: var(--font-body-03); color: var(--gray-900); margin-bottom: 2px; font-weight: 600; }
.t-code { font: var(--font-caption-01); color: var(--gray-400); text-transform: uppercase; }

/* 금액 */
.t-amount { font: var(--font-body-03); text-align: right; font-weight: 700; }
.t-balance { font: var(--font-caption-01); color: var(--gray-400); text-align: right; margin-top: 4px; }
</style>