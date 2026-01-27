<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="items" type="java.util.List" required="true" %>
<%@ attribute name="activeValue" type="java.lang.String" %>

<div class="project-tabs">
    <c:forEach var="item" items="${items}" varStatus="status">
        <div class="tab-item ${item.value == activeValue ? 'active' : ''}" 
             onclick="handleTabClick('${item.value}')">
            ${item.title}
            <%-- 숫자가 0보다 크거나 null이 아닐 때만 노출 --%>
            <c:if test="${not empty item.count}">
                <span class="tab-count">${item.count}</span>
            </c:if>
        </div>
    </c:forEach>
</div>

<style>
.project-tabs { display: flex; gap: 24px; border-bottom: 1px solid var(--gray-200); margin-bottom: 24px; }
.tab-item { padding: 12px 4px; font: var(--font-subtitle-01); color: var(--gray-400); cursor: pointer; position: relative; }
.tab-item.active { color: var(--green-600); }
.tab-item.active::after { content: ''; position: absolute; bottom: -2px; left: 0; right: 0; height: 3px; border-radius:4px; background: var(--green-600); }
.tab-count { font:var(--font-subtitle-01); margin-left: 4px; color: var(--gray-400); }

</style>