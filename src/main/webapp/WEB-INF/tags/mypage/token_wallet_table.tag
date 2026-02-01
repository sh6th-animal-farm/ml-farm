<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ attribute name="tokenList" type="java.util.List" required="true" %>

<table class="token-table">
	<thead>
	    <tr>
	        <th class="table-head-name">종목</th>
	        <th class="table-head-others">평가손익 / 수익률</th>
	        <th class="table-head-others">평가금액 / 매입금액</th>
	        <th class="table-head-others table-head-last">보유수량</th>
	    </tr>
	</thead>
	<tbody id="token-data-list">
	    <c:choose>
            <%-- 연동 내역이 있더라도 보유한 토큰이 0개일 수 있으므로 null 체크 병행 --%>
            <c:when test="${not empty tokenList && tokenList.size() > 0}">
                <c:forEach var="token" items="${tokenList}">
                    <tr>
                        <td>
                        	<div class="token-name">${token.tokenName}</div>
                        	<div class="token-code">${token.tickerSymbol}</div>
                    	</td>
                        <td style="text-align: right;">
                            <%-- 수익률이 0보다 크면 text-plus, 작으면 text-minus 적용 --%>
                            <div class="token-name ${token.profitLoss >= 0 ? 'text-plus' : 'text-minus'}">
                                ${token.profitLoss >= 0 ? '+' : ''}<fmt:formatNumber value="${token.profitLoss}" type="number"/> 원
                            </div>
                            <div class="token-code ${token.profitLossRate >= 0 ? 'text-plus' : 'text-minus'}">
                                ${token.profitLossRate >= 0 ? '+' : ''}<fmt:formatNumber value="${token.profitLossRate}" pattern="0.00"/> %
                            </div>
                        </td>
                        <td style="text-align: right;">
                            <div class="token-name"><fmt:formatNumber value="${token.marketValue}" type="number"/> 원</div>
                            <div class="token-code"><fmt:formatNumber value="${token.purchasedValue}" type="number"/> 원</div>
                        </td>
                        <td class="token-amount">
                            <fmt:formatNumber value="${token.tokenBalance}" type="number"/> st
                        </td>
                    </tr>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <%-- 데이터가 없을 경우 --%>
                <tr>
                    <td colspan="4" style="text-align: center; padding: 60px; color: var(--gray-400);">
                        보유 중인 토큰이 없습니다.
                    </td>
                </tr>
            </c:otherwise>
        </c:choose>
	</tbody>
</table>

<style>
/* 보유 토큰 테이블 */
.token-section-title { font-size: 20px; font-weight: 700; margin-bottom: 20px; display: flex; align-items: center; gap: 8px; }
.token-count { color: var(--green-600); }

.token-table { width: 100%; table-layout: fixed; border-collapse: collapse; background: #fff; border-radius: var(--radius-l); overflow: hidden; box-shadow: var(--shadow); }
.token-table th { background: var(--gray-50); padding: 20px 8px; color: var(--gray-500); font:var(--font-button-02); border-bottom: 1px solid #F1F1F1; box-sizing: border-box; }
.table-head-name { width: auto; padding-left:24px !important; text-align: left; }
.table-head-code { width: 120px; text-align: left; padding-left: 8px;}
.table-head-others { width: 176px; text-align: right; }
.table-head-last { padding-right: 24px !important; }
.token-table td { padding: 24px 8px; border-bottom: 1px solid #F1F1F1; vertical-align: middle; }
   
.token-table td:first-child { padding-left: 24px; }
.token-table td:last-child { padding-right: 24px; }
   
.token-name { font: var(--font-body-03); color: var(--gray-900); margin-bottom: 2px; }
.token-code { font: var(--font-caption-01); color: var(--gray-400); }
.token-amount { font: var(--font-body-03); text-align: right; }
.text-plus { color: var(--error); }
.text-minus { color: var(--info); }
</style>