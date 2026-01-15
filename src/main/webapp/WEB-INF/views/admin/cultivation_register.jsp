<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>관리자 모드 | 마이리틀스마트팜</title>
    <link rel="stylesheet" as="style" crossorigin href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/admin.css">
</head>
<body>
	<jsp:include page="../../includes/admin_sidebar.jsp" />

    <main class="main-content">
        <div class="container-1200">
            <div class="page-header">
                <h1>재배 정보 등록</h1>
            </div>

            <form action="${pageContext.request.contextPath}/admin/cultivation/insert" method="post">
                
                <div class="form-card">
                    <div class="section-title">연관 프로젝트 설정</div>
                    <div class="grid-2">
                        <div class="form-group">
                            <label>대상 프로젝트 선택 (Project ID)</label>
                            <select name="project_id" required>
                                <option value="">재배 정보를 등록할 프로젝트를 선택하세요</option>
                                <c:forEach var="proj" items="${projectList}">
                                    <option value="${proj.project_id}">${proj.project_name} (${proj.project_round}차)</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>현장 관리자 수 (Manager Count)</label>
                            <input type="number" name="manager_count" placeholder="0" required>
                        </div>
                    </div>
                </div>

                <div class="form-card">
                    <div class="section-title">작물 및 생산 정보</div>
                    <div class="grid-3">
                        <div class="form-group">
                            <label>재배 작물 (Crop)</label>
                            <input type="text" name="crop" placeholder="예: 유러피안 상추, 방울토마토" required>
                        </div>
                        <div class="form-group">
                            <label>재배 공법 (Method)</label>
                            <input type="text" name="method" placeholder="예: 수경재배, 에어로포닉스" required>
                        </div>
                        <div class="form-group">
                            <label>식재 수량 (Amount)</label>
                            <input type="number" name="amount" placeholder="단위: 본/포기" required>
                        </div>
                    </div>
                </div>

                <div class="form-card">
                    <div class="section-title">재배 일정 및 결과</div>
                    <div class="grid-3">
                        <div class="form-group">
                            <label>식재일 (Planting Date)</label>
                            <input type="date" name="planting_date" required>
                        </div>
                        <div class="form-group">
                            <label>수확 예정일 (Harvest Date)</label>
                            <input type="date" name="harvest_date" required>
                        </div>
                        <div class="form-group">
                            <label>실제 수확량 (Yield) <span class="optional">(선택)</span></label>
                            <input type="number" step="0.01" name="yield" placeholder="단위: kg">
                            <span style="font-size: 12px; color: var(--green-600);">* 수확 완료 후 입력 가능</span>
                        </div>
                    </div>
                </div>

                <button type="submit" class="btn-submit">재배 정보 저장하기</button>
            </form>
        </div>
    </main>


    <div id="projectModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <span>지난 프로젝트 목록 선택</span>
                <span style="cursor:pointer" onclick="closeModal()">&times;</span>
            </div>
            <ul class="project-list">
                <li class="project-item" onclick="selectProject({id: 1, name: '청라 1호 토마토', round: 1, farm: 1, target: 500000000, min: 100000, max: 10000000, roi: 12.5, mgr: 2})">
                    <strong>청라 1호 토마토 (1차)</strong><br>
                    <small>농장 ID: 1 | 목표액: 5억 | 수익률: 12.5%</small>
                </li>
                <li class="project-item" onclick="selectProject({id: 2, name: '김제 오이 공모', round: 2, farm: 2, target: 300000000, min: 50000, max: 5000000, roi: 10.2, mgr: 1})">
                    <strong>김제 오이 공모 (2차)</strong><br>
                    <small>농장 ID: 2 | 목표액: 3억 | 수익률: 10.2%</small>
                </li>
            </ul>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/resources/js/admin.js"></script>
</body>
</html>