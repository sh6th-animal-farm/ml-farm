<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>농장 등록 | 마이리틀스마트팜 관리자</title>
    
    <link rel="stylesheet" as="style" crossorigin href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.min.css" />
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/admin.css">
    <script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
</head>
<body>

	<jsp:include page="../common/admin_sidebar.jsp" />

    <main class="main-content">
        <div class="container-1200">
            <div class="page-header">
                <h1>농장 등록/수정</h1>
                <button type="button" class="btn-load" onclick="openModal()">농장 정보 불러오기</button>
            </div>

            <form method="post">
                
                <div class="form-card">
                    <div class="section-title">기본 정보</div>
                    <div class="grid-3">
                        <div class="form-group">
                            <label>농장 명칭</label>
                            <input type="text" name="farm_name" placeholder="예: 청라 토마토 1호 농장" required>
                        </div>
                        <div class="form-group">
                            <label>농장 유형</label>
                            <select name="farm_type" required>
                                <option value="">유형 선택</option>
                                <option value="VINYL">비닐하우스</option>
                                <option value="GLASS">유리온실</option>
                                <option value="VERTICAL">수직농장</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>재배 면적 (㎡)</label>
                            <input type="number" step="0.01" name="area" placeholder="0.00" required>
                        </div>
                        <div class="form-group">
                            <label>개설 일자</label>
                            <input type="date" name="open_at" required>
                        </div>
                        <div class="form-group full">
                            <label>농장 썸네일 URL <span class="optional">(선택)</span></label>
                            <input type="text" name="thumbnail_url" placeholder="https://image.path/example.jpg">
                        </div>
                    </div>
                </div>

                <div class="form-card">
                    <div class="section-title">위치 정보</div>
                    <div class="grid-3">
                        <div class="form-group">
                            <label>시/도 (Sido)</label>
                            <input type="text" name="address_sido" placeholder="예: 인천광역시" required>
                        </div>
                        <div class="form-group">
                            <label>시/군/구 (Sigungu)</label>
                            <input type="text" name="address_sigungu" placeholder="예: 서구" required>
                        </div>
                        <div class="form-group">
                            <label>도로명/지번 (Street)</label>
                            <div class="input-with-btn">
	                            <input type="text" name="address_street" placeholder="도로명 주소 입력" required>
	                            <button type="button" class="btn-search" onclick="searchAddress()">주소 검색</button>
                            </div>
                        </div>
                        <div class="form-group full">
                            <label>상세 주소 (Details) <span class="optional">(선택)</span></label>
                            <input type="text" name="address_details" placeholder="나머지 상세 주소">
                        </div>
                        
                        <div class="form-group">
				            <label>위도 (Latitude)</label>
				            <input type="text" name="latitude" class="readonly-input" placeholder="주소 검색 시 자동 입력" readonly>
				        </div>
				        <div class="form-group">
				            <label>경도 (Longitude)</label>
				            <input type="text" name="longitude" class="readonly-input" placeholder="주소 검색 시 자동 입력" readonly>
				        </div>
				        <div class="form-group">
				            <label>고도 (Altitude)</label>
				            <input type="text" name="altitude" class="readonly-input" placeholder="0" readonly>
				        </div>
                    </div>
                </div>

                <div class="form-card">
                    <div class="section-title">상세 설명</div>
                    <div class="form-group full">
                        <label>농장 소개 <span class="optional">(선택)</span></label>
                        <textarea name="description" rows="5" placeholder="농장의 특징 및 시설 정보를 입력하세요."></textarea>
                    </div>
                </div>

                <button type="submit" class="btn-submit">농장 등록 완료</button>
            </form>
        </div>
    </main>

	<t:global_modal />
	<div id="toastContainer" class="toast-container"></div>
    <script type="module" src="${pageContext.request.contextPath}/resources/js/domain/admin/farm_register.js"></script>
</body>
</html>