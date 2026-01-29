<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ attribute name="tokenList" type="java.util.List" required="true" %>

<div class="token-table-scroll-container">
    <table class="token_table_main">
        <thead>
            <tr>
                <th class="table-head-name">순위</th>
                <th class="table-head-others">종목</th>
                <th class="table-head-others">현재가(KRW)</th>
                <th class="table-head-others">등락률</th>
                <th class="table-head-others table-head-last">거래대금</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${not empty tokenList}">
                    <c:forEach var="token" items="${tokenList}" varStatus="status">
                        <tr id="token-row-${token.tokenId}">
                            <td class="tx-center ranking-num">
                                ${status.count}
                            </td>
                            <td>
                                <div class="token-name">${token.tokenName}</div>
                                <div class="token-code">${token.tickerSymbol}</div>
                            </td>
                            <td class="tx-right">
                                <div class="token-name market-price">
                                    <fmt:formatNumber value="${token.marketPrice}" type="number"/>
                                </div>
                            </td>
                            <td class="tx-right change-rate ${token.changeRate > 0 ? 'text-plus' : (token.changeRate < 0 ? 'text-minus' : '')}">
                                <span class="rate-badge">
                                    <c:choose>
                                        <c:when test="${token.changeRate > 0}">+</c:when>
                                    </c:choose>
                                    <fmt:formatNumber value="${token.changeRate}" type="number" minFractionDigits="2" maxFractionDigits="2"/>&#37;
                                </span>
                            </td>
                            <td class="token-amount tx-right daily-volume" data-value="${token.dailyTradeVolume}">
                                <c:set var="vol" value="${token.dailyTradeVolume}" />
                                <c:choose>
                                    <c:when test="${vol >= 1000000}">
                                        <fmt:parseNumber var="millionPart" value="${vol / 1000000}" integerOnly="true" />
                                        <fmt:parseNumber var="tenThousandPart" value="${(vol % 1000000) / 10000}" integerOnly="true" />
                                        <fmt:formatNumber value="${millionPart}" pattern="#,###"/>백
                                        <c:if test="${tenThousandPart > 0}">
                                            ${tenThousandPart}만
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:formatNumber value="${vol}" pattern="#,###"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <%-- 데이터가 없을 경우 --%>
                    <tr>
                        <td colspan="5" style="text-align: center; padding: 60px; color: var(--gray-400);">
                            토큰 목록이 없습니다.
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</div>
