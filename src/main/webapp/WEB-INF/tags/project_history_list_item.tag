<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ attribute name="name" required="true" %>
<%@ attribute name="startDate" required="true" %>
<%@ attribute name="endDate" required="true" %>
<%@ attribute name="status1" required="true" %>
<%@ attribute name="status2" required="false" %>

<div class="project-item">
    <div class="project-info">
        <p class="p-title">${name}</p>
        <p class="p-date">${startDate} - ${endDate}</p>
    </div>
    <div class="status-group">
        <t:status_badge status="inProgress" label="${status1}"/>
        <c:if test="${status2!=null}">
        	<t:status_badge status="inProgress" label="${status2}"/>
        </c:if>
    </div>
    <button class="btn-trade">토큰 거래</button>
</div>

<style>
.project-item { 
    display: flex; align-items: center; gap:24px;
    padding-block: 24px; background: #fff; 
}
.project-info { width:100%; flex:1; }
.project-info .p-title { font:var(--font-body-03); margin-bottom: 2px;}
.project-info .p-date { font:var(--font-caption-01); color: var(--gray-400); }
.status-group { width:150px; display: flex; align-items: center; justify-content:center; gap: 12px; }

.btn-trade { 
    background: #1A1D23; color: #fff; border: none; padding: 10px 24px; 
    border-radius: var(--radius-m); font-size: 14px; font-weight: 600; cursor: pointer;
}
.btn-more { 
    width: 100%; padding: 12px; background: #fff; box-shadow: var(--shadow); border:none;
    border-radius: 12px; color: var(--gray-900); font-weight: 600; margin-top: 24px; cursor: pointer;
}
</style>