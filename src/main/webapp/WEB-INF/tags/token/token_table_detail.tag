<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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
            <tbody id="token-list-body">
            <c:forEach var="token" items="${tokenList}">
                <tr id="token-row-${token.tokenId}" data-volume="${token.dailyTradeVolume}">
                    <td style="font: var(--font-body-03);">
                            ${token.tokenName}
                        <span style="margin-left: 4px; font: var(--font-caption-02); color: var(--gray-400);">${token.tickerSymbol}</span>
                    </td>
                    <td class="price" style="text-align: right; font: var(--font-body-04);">
                        <fmt:formatNumber value="${token.marketPrice}" pattern="#,###"/>
                    </td>
                    <td class="rate tx-right
                        <c:choose>
                            <c:when test='${token.changeRate > 0}'>rate-up</c:when>
                            <c:when test='${token.changeRate < 0}'>rate-down</c:when>
                            <c:otherwise>rate-zero</c:otherwise>
                        </c:choose>">
                        <c:if test="${token.changeRate > 0}">+</c:if> ${token.changeRate}%
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<style>
    /* 텍스트 정렬 공통 (내 스타일 형식에 맞춰) */
    .tx-right { text-align: right !important; }
    /* 등락 상태별 색상 */
    .rate-up { color: var(--error) !important; }    /* 빨간색 */
    .rate-down { color: var(--info) !important; }  /* 파란색 */
    .rate-zero { color: var(--gray-900) !important; } /* 검정색 (gray-900이 디자인 시스템에 있다면 추천) */
</style>