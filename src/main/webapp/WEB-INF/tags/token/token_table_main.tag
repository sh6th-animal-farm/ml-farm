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
                        <tr>
                            <td class="tx-center">
                                ${status.count}
                            </td>
                            <td>
                                <div class="token-name">${token.tokenName}</div>
                                <div class="token-code">${token.tickerSymbol}</div>
                            </td>
                            <td class="tx-right">
                                <div class="token-name ${token.marketPrice >= 0 ? 'text-plus' : 'text-minus'}">
                                    <fmt:formatNumber value="${token.marketPrice}" type="number"/> 원
                                </div>
                            </td>
                            <td class="tx-right">
                                <fmt:formatNumber value="${token.changeRate}" type="number"/> st
                            </td>
                            <td class="token-amount tx-right">
                                <fmt:formatNumber value="${token.dailyTradeVolume}" type="number"/> st
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

<style>
/* 보유 토큰 테이블 */
.token_list_table_main { font-size: 20px; font-weight: 700; margin-bottom: 20px; display: flex; align-items: center; gap: 8px; }
.token-count { color: var(--green-600); }

.token_table_main { width: 100%; table-layout: fixed; border-collapse: separate; background: #fff; border-radius: var(--radius-l); box-shadow: var(--shadow); }
.token_table_main th { position: sticky; background: var(--gray-50); padding: 14px 8px; color: var(--gray-500); font:var(--font-button-02); border-bottom: 1px solid #F1F1F1; box-sizing: border-box; }
.token_table_main th:nth-child(1) { width: 60px; text-align: center; padding-left: 20px; }
.token_table_main th:nth-child(2) { width: auto; text-align: left; }
.token_table_main .table-head-others { width: 180px; text-align: right; }
.token_table_main .table-head-last { padding-right: 24px !important; }

.table-head-name { width: auto; padding-left:24px !important; text-align: left; }
.token_table_main td { padding: 14px 8px; border-bottom: 1px solid #F1F1F1; vertical-align: middle; word-break: break-all;}

.token-table td:first-child { padding-left: 24px; }
.token-table td:last-child { padding-right: 24px; }

.token_table_main td.token-amount { text-align: right; padding-right: 24px;}
.token_table_main .token-name { font: var(--font-body-03); color: var(--gray-900); }
.token_table_main .token-code { font: var(--font-caption-01); color: var(--gray-400); }

/* 정렬 관련 */
.token_table_main td.tx-center { text-align: center; }
.token_table_main td.tx-right { text-align: right; }

/* 색상 관련 */
.text-plus { color: var(--error); } /* 상승: 빨강 */
.text-minus { color: var(--info); } /* 하락: 파랑 */

/* 1. 테이블 컨테이너: 높이 제한 및 스크롤 설정 */
.token-table-scroll-container {
    flex: 1;
    max-height: 100vh;
    overflow-y: auto;
    border-radius: var(--radius-l);
    box-shadow: var(--shadow);
    background: #fff;
    position: relative;
}

/* 4. 우측 여백 제거를 위한 마지막 열 처리 */
.token_table_main th:last-child,
.token_table_main td:last-child {
    padding-right: 24px !important;
    width: 180px; /* 고정폭을 주어 우측 끝까지 꽉 차게 설정 */
}

.token-table-scroll-container::-webkit-scrollbar {
    width: 6px;
}

.token-table-scroll-container::-webkit-scrollbar-thumb {
    background: var(--gray-300);
    border-radius: 10px;
}

.token-table-scroll-container::-webkit-scrollbar-thumb {
    background: var(--gray-300);
    border-radius: 10px;
}

/* 2. 테이블 헤더 고정: 스크롤해도 제목이 위에 붙어있게 함 */
.token_table_main thead th {
    position: sticky;
    top: 0;
    z-index: 10; /* 내용물보다 위에 오도록 */
    background: var(--gray-50); /* 고정될 때 배경색이 있어야 내용과 겹치지 않음 */
    border-bottom: 1px solid #F1F1F1;
    padding: 14px 8px;
    border: none;
}

/* 3. 기존 테이블 스타일 유지 및 수정 */
.token_table_main {
    width: 100%;
    table-layout: fixed;
    border-collapse: collapse;
    /* overflow: hidden; 삭제 (sticky 작동을 방해함) */
}

</style>
