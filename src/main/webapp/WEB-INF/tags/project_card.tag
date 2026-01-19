<%@ tag language="java" pageEncoding="UTF-8" body-content="scriptless" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ attribute name="status" type="com.animalfarm.mlf.constants.ProjectStatus" required="true" %> <%-- 청약중, 공고중, 진행중 --%>
<%@ attribute name="title" required="true" %>
<%@ attribute name="upperDate" required="true" %>
<%@ attribute name="lowerDate" required="true" %>
<%@ attribute name="percent" required="false" %>

<c:set var="label" value="${status.label}" />
<c:set var="contentGap" value="${status.name() == 'SUBSCRIPTION' ? '24px' : (status.name() == 'ANNOUNCEMENT' ? '20px' : '32px')}" />

<div class="project-card">
    <div class="card-image">
        <t:status_badge className="badge" label="${label}" status="${status.badgeStatus}"/>
        <button class="interest-btn">
        	<t:icon name="seedling"/>
        </button>
    </div>
    
    <div class="card-content" style="gap:${contentGap};">
        <div class="card-info">
        	<h4 class="card-title">${title}</h4>
        	<c:if test="${status.name() != 'INPROGRESS'}"> 
	        	<div class="card-info-row">
		            <span class="card-date">${upperDate}</span>
		            <c:choose>
		            <c:when test="${status.name() == 'SUBSCRIPTION'}">
		                <span class="card-dday text-error">마감까지 46:07:20</span>
		            </c:when>
		            <c:when test="${status.name() == 'ANNOUNCEMENT'}">
		                <span class="card-dday text-error">D-5</span>
		            </c:when>
		            </c:choose>
	        	</div>
        	</c:if>
        </div>

        <div class="card-details">
            <c:choose>
                <c:when test="${status.name() == 'SUBSCRIPTION'}">
                    <div class="progress-container">
                    	<div class="progress-text"> 
	                        <div class="progress-percent">${percent}%</div>
	                        <div class="progress-percent-text">모집</div>
                    	</div>
                        <div class="progress-bar"><div class="bar" style="width: ${percent}%"></div></div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="info-list">
                        <p><span>${status.name() == 'ANNOUNCEMENT' ? '청약 예정일' : '운영 기간'}</span>
                           <strong>${lowerDate}</strong></p>
                        <p><span>${status.name() == 'ANNOUNCEMENT' ? '예상 수익률' : '현재 수익률'}</span>
                           <strong>연 14.2%</strong></p>
                    </div>
                </c:otherwise>
            </c:choose>
		</div>
        <div class="card-buttons">
            <%-- 버튼 클래스도 Enum에서 가져옴 --%>
            <button class="btn-action ${status.btnClass}">
                <c:choose>
                    <c:when test="${status.name() == 'SUBSCRIPTION'}">청약하기</c:when>
                    <c:when test="${status.name() == 'ANNOUNCEMENT'}">공고보기</c:when>
                    <c:otherwise>토큰구매</c:otherwise>
                </c:choose>
            </button>
        </div>
    </div>
</div>

<style>
.project-card { background: white; border-radius: var(--radius-l); overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.05); margin-bottom: 24px; }
.card-image { height: 200px; background: #e5e5e5; position: relative; }
.badge { position: absolute; top: 16px; left: 16px;}
.interest-btn {position: absolute; top: 18px; right: 16px; border:none; background-color: transparent; cursor:pointer;} 
.card-content { display: flex; flex-direction:column; padding: 24px; }
.card-title { font: var(--font-subtitle-01); color: var(--gray-900);}
.card-info { display:flex; flex-direction:column; gap:4px}
.card-info-row { display: flex; justify-content: space-between; font: var(--font-caption-01); color: var(--gray-400); }
.card-date { font: var(--font-caption-01); }
.card-dday { font: var(--font-button-02); }
.progress-text { display:flex; gap:4px; align-items: end; color: var(--green-600) }
.progress-percent { font:var(--font-body-03); }
.progress-percent-text { font:var(--font-button-02); }
.progress-bar { height: 6px; background: var(--gray-100); border-radius: 3px; margin-top: 4px; margin-bottom: 4px; }
.bar { height: 100%; background: var(--green-600); border-radius: 3px; }
.info-list {display:flex; flex-direction:column; gap:4px;}
.info-list p { display: flex; justify-content: space-between; font: var(--font-caption-01); }
.btn-action { width: 100%; padding: 12px; border: none; border-radius: var(--radius-s); background-color:var(--gray-900); color: white; font: var(--font-button-01); cursor: pointer; transition: background-color 0.3s ease, transform 0.2s ease; }
.bg-warning:hover { background: var(--warning); }
.bg-info:hover { background: var(--info); }
.bg-primary:hover { background: var(--green-600); }
</style>