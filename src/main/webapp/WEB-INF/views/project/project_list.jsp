<%@page import="com.animalfarm.mlf.constants.ProjectStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/project_list.css" />
<script type="module"
	src="${pageContext.request.contextPath}/resources/js/domain/project/project_list.js"></script>
<script
	src="${pageContext.request.contextPath}/resources/js/util/timer.js"></script>
<script type="text/javascript"
	src="//dapi.kakao.com/v2/maps/sdk.js?appkey=${kakaoMapKey}&libraries=services,clusterer&autoload=false"></script>

<div class="project-list-container">
	<t:section_header title="프로젝트 지도" subtitle="진행중인 프로젝트를 지도에서 확인하세요" />

	<div class="map-area">
		<t:region_accordian />
		<div id="map"
			style="width: 100%; height: 100%; background-color: var(--gray-100); border-radius: var(--radius-l); box-shadow: var(--shadow);"></div>
	</div>

	<t:section_header title="프로젝트 목록"  subtitle="프로젝트를 선택하여 자세한 정보를 확인하세요" />

	<div class="list-controls">
		<div class="filter-group">
			<t:menu_button label="전체보기" active="${empty param.projectStatus}"
				onClick="filterCategory('', this)" />
			<t:menu_button label="청약중"
				active="${param.projectStatus == 'SUBSCRIPTION'}"
				onClick="filterCategory('SUBSCRIPTION', this)" />
			<t:menu_button label="공고중"
				active="${param.projectStatus == 'ANNOUNCEMENT'}"
				onClick="filterCategory('ANNOUNCEMENT', this)" />
			<t:menu_button label="진행중"
				active="${param.projectStatus == 'INPROGRESS'}"
				onClick="filterCategory('INPROGRESS', this)" />
		</div>
		<t:search_bar />
	</div>

	<div class="row" id="projectCardContainer">
		<jsp:include page="/WEB-INF/views/project/project_card_list.jsp" />
	</div>
</div>

<script
	src="${pageContext.request.contextPath}/resources/js/constants/region_coords.js"></script>
<script
	src="${pageContext.request.contextPath}/resources/js/util/kakao_map.js"></script>
<script>
	const projectList = ${projectListJson};
	console.log(projectList);
	// 2. DOM이 로드된 후 지도 초기화
	document.addEventListener("DOMContentLoaded", function() {
		initMap(projectList);
	});
</script>