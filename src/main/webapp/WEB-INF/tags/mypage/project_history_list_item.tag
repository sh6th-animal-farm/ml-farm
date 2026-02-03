<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ attribute name="name" required="true" %>
<%@ attribute name="startDate" required="true" %>
<%@ attribute name="endDate" required="true" %>
<%@ attribute name="status1" required="true" %>
<%@ attribute name="status2" required="false" %>
<%@ attribute name="href" required="true" %>

<div class="project-item">
    <div class="project-info">
        <p class="p-title">${name}</p>
        <p class="p-date">${startDate} - ${endDate}</p>
    </div>
    <div class="status-group">
        <c:choose>
		  <c:when test="${filterStatus == 'SUBSCRIPTION'}">
		    <span class="badge sub">청약중</span>
		  </c:when>
		  <c:when test="${filterStatus == 'ANNOUNCEMENT'}">
		    <span class="badge ann">공고중</span>
		  </c:when>
		  <c:when test="${filterStatus == 'ENDED'}">
		    <span class="badge end">종료됨</span>
		  </c:when>
		  <c:otherwise>
		    <span class="badge sub">청약중</span>
		  </c:otherwise>
		</c:choose>
    </div>
    <a class="detail-link" href="${href}">
        <t:icon name="chevron_right" color="var(--gray-400)" size="20"/>
    </a>
</div>

<style>
.project-item { 
    display: flex; align-items: center; gap:24px;
    padding-block: 24px; background: #fff; cursor:pointer;
}
.project-info { width:100%; flex:1; }
.project-info .p-title { font:var(--font-body-03); margin-bottom: 2px;}
.project-info .p-date { font:var(--font-caption-01); color: var(--gray-400); }
.status-group { width:150px; display: flex; align-items: center; justify-content:center; gap: 12px; }

.detail-link {display:flex; align-items: center; text-decoration: none;}
</style>