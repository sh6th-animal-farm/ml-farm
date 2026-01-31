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
    <link rel="icon" href="data:;base64,iVBORw0KGgo=">
    <script>
        // 전역 변수로 선언 (보통 'ctx' 또는 'contextPath'라고 명명)
        //const ctx = "${pageContext.request.contextPath}";
        const ctx = "https://mlfarm.3jun.store";
    </script>
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
	
    <%-- 인증 관리 매니저 스크립트 로드 --%>
    <script src="${pageContext.request.contextPath}/resources/js/domain/auth/auth_manager.js"></script>
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            // 모든 페이지에서 AuthManager를 초기화합니다.
            if (typeof AuthManager !== 'undefined') {
                AuthManager.init("${pageContext.request.contextPath}");
            }
        });
    </script>

    <%-- SockJS, STOMP, WebSocket manager --%>
    <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
    <script src="${ctx}/resources/js/util/websocket_manager.js"></script>
    
</body>
</html>