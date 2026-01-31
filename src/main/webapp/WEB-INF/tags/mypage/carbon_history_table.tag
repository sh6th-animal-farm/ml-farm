<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ attribute name="carbonList" type="java.util.List" required="true" %>

<table class="carbon-table">
    <thead>
        <tr>
            <th class="th-type head-first">유형</th>
            <th>프로젝트 정보</th>
            <th class="th-buy-date">구매일</th>
            <th class="th-expire-date">만료일</th>
            <th class="th-amount">구매량</th>
            <th class="head-last" style="text-align: right;">구매 금액</th>
        </tr>
    </thead>
    <tbody id="carbon-data-list">
        <c:choose>
            <c:when test="${not empty carbonList}">
                <c:forEach var="item" items="${carbonList}">
                    <tr>
                        <%-- 상태에 따른 뱃지 로직 --%>
                        <c:set var="badgeStatus" value="others"/>
                        <c:choose>
                            <c:when test="${item.type == 'REDUCTION'}"><c:set var="badgeStatus" value="announcement"/></c:when>
                            <c:when test="${item.type == 'REMOVAL'}"><c:set var="badgeStatus" value="subscription"/></c:when>
                            <c:when test="${item.type == 'EXPIRED'}"><c:set var="badgeStatus" value="others"/></c:when>
                        </c:choose>
                        
                        <td>
                            <t:status_badge status="${badgeStatus}" label="${item.typeLabel}"/>
                        </td>
                        <td class="project-name">${item.projectName}</td>
                        <td class="buy-date">
                            <fmt:formatDate value="${item.buyDate}" pattern="yyyy. MM. dd"/>
                        </td>
                        <td class="expire-date">
                            <fmt:formatDate value="${item.expireDate}" pattern="yyyy. MM. dd"/>
                        </td>
                        <td class="carbon-unit">
                            <fmt:formatNumber value="${item.amount}" type="number"/> tCO2e
                        </td>
                        <td class="carbon-price">
                            <fmt:formatNumber value="${item.price}" type="number"/> 원
                        </td>
                    </tr>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <tr>
                    <td colspan="6" style="text-align: center; padding: 60px; color: var(--gray-400);">
                        구매하신 탄소 배출권 내역이 없습니다.
                    </td>
                </tr>
            </c:otherwise>
        </c:choose>
    </tbody>
</table>

<style>
.carbon-table { width: 100%; table-layout: fixed; border-collapse: collapse; background: #fff; border-radius: var(--radius-l); overflow: hidden; box-shadow: var(--shadow); box-sizing: border-box; }
.carbon-table th { background: var(--gray-50); padding: 16px 8px; text-align: left; color: var(--gray-500); font: var(--font-button-02); border-bottom: 1px solid #F1F1F1; }
.th-type {text-align: center !important; width: 110px}
.th-buy-date {width: 135px;}
.th-expire-date {width: 135px;}
.th-amount {width: 145px;}
.carbon-table td { padding: 20px 8px; border-bottom: 1px solid #F1F1F1; font-size: 14px; vertical-align: middle; }
.head-first {padding-left: 24px !important;}
.head-last {padding-right: 24px !important;}
.carbon-table td:first-child { padding-left: 24px; text-align: center; }
.carbon-table td:last-child { padding-right: 24px; }

.project-name { font:var(--font-body-01); color: var(--gray-900); }
.buy-date {font:var(--font-body-01);}
.expire-date {font:var(--font-body-01);}
.carbon-unit { font:var(--font-body-01); color: var(--gray-900); }
.carbon-price { font:var(--font-body-01); color: var(--gray-900); text-align: right; }
</style>