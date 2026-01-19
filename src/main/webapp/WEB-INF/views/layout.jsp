<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.title != null ? param.title : '마이리틀스마트팜'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/common.css">
</head>
<body>

    <jsp:include page="/WEB-INF/views/common/header.jsp" />

    <main class="content-wrapper">
        <div class="container">
            <%-- 전달받은 contentPage 경로의 파일을 삽입함 --%>
            <jsp:include page="${contentPage}" />
        </div>
    </main>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>