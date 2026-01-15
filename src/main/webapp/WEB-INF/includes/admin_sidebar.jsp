<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
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