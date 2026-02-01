<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="mp" tagdir="/WEB-INF/tags/mypage"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ attribute name="projectList" type="java.util.List" required="true" %>

<div class="project-list">
	<c:choose>
        <c:when test="${not empty projectList}">
            <c:forEach var="project" items="${projectList}">
                <mp:project_history_list_item 
                    startDate="${project.startDate}" 
                    name="${project.projectName}" 
                    status1="${project.status1}" 
                    status2="${project.status2}" 
                    endDate="${project.endDate}" 
                    onclick="location.href='${pageContext.request.contextPath}/project/detail?id=${project.projectId}'"
                />
            </c:forEach>
        </c:when>
        <c:otherwise>
            <%-- 리스트가 비어있을 때 디자인 가이드에 맞춘 안내 문구 --%>
            <div class="empty-list" style="padding: 80px 0; text-align: center; color: var(--gray-400);">
                참여한 프로젝트 내역이 없습니다.
            </div>
        </c:otherwise>
    </c:choose>
</div>