<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/common.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/toast.css" />
<script src="${ctx}/resources/js/util/pending_manager.js"></script>
<script src="${ctx}/resources/js/util/modal_toast_manager.js"></script>
<script>
    const ctx = "${pageContext.request.contextPath}";
</script>
<nav class="sidebar">
	<div class="logo">
		마이리틀 스마트팜<br>Admin
	</div>
	<a href="${pageContext.request.contextPath}/admin/project/new" class="menu-item ${activeMenu == 'project' ? 'active' : ''}">프로젝트 등록/수정</a>
	<a href="${pageContext.request.contextPath}/admin/farm/new" class="menu-item ${activeMenu == 'farm' ? 'active' : ''}">농장 등록/수정</a>
	<a href="${pageContext.request.contextPath}/admin/cultivation/new" class="menu-item ${activeMenu == 'cultivation' ? 'active' : ''}">재배 정보 입력</a>
	<a href="${pageContext.request.contextPath}/admin/revenue/new" class="menu-item ${activeMenu == 'income' ? 'active' : ''}">수익 정보 입력</a>
	<a href="${pageContext.request.contextPath}/admin/expense/new" class="menu-item ${activeMenu == 'expense' ? 'active' : ''}">지출 정보 입력</a>
</nav>
<script>
document.addEventListener("DOMContentLoaded", () => {
    PendingManager.checkAndRun();
});
</script>