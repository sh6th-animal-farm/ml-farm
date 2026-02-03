<%@ tag language="java" pageEncoding="UTF-8" body-content="scriptless" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ attribute name="status" type="com.animalfarm.mlf.constants.ProjectStatus" required="true" %> <%-- 청약중, 공고중, 진행중 --%>
<%@ attribute name="title" required="true" %>
<%@ attribute name="id" required="true"%>
<%@ attribute name="isStarred" required="true" type="java.lang.Boolean" %>
<%@ attribute name="thumbnailUrl" required="false" %>
<%@ attribute name="endTime" required="true" %>
<%@ attribute name="upperDate" required="true" %>
<%@ attribute name="lowerDate" required="true" %>
<%@ attribute name="percent" required="false" %>

<c:set var="label" value="${status.label}" />
<c:set var="contentGap" value="${status.name() == 'SUBSCRIPTION' ? '24px' : (status.name() == 'ANNOUNCEMENT' ? '20px' : '32px')}" />

<div class="project-card" onclick="location.href='${pageContext.request.contextPath}/project/${id}'">
    <div class="card-image">
    	<img class="thumbnail" src="${thumbnailUrl}" alt="project thumnail" />
        <t:status_badge className="badge" label="${label}" status="${status.badgeStatus}"/>
        <button class="interest-btn" data-starred="${isStarred}" onclick="event.stopPropagation(); toggleStarred(this, null); starProject('2', '${id}', this);">
        	<t:icon name="heart_filled" size="26" color="white" className="heart-icon-${id}" />
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
		                <span class="card-dday text-error timer-display" data-end-time="${endTime}" data-status="${status.name()}" >마감까지</span>
		            </c:when>
		            <c:when test="${status.name() == 'ANNOUNCEMENT'}">
		                <span class="card-dday text-error timer-display" data-end-time="${endTime}" data-status="${status.name()}" >시작까지</span>
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
                    <c:when test="${status.name() == 'SUBSCRIPTION'}">청약 하기</c:when>
                    <c:when test="${status.name() == 'ANNOUNCEMENT'}">공고 보기</c:when>
                    <c:otherwise>토큰 구매</c:otherwise>
                </c:choose>
            </button>
        </div>
    </div>
</div>