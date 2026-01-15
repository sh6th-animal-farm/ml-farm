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
                <h1>지출 정보 등록</h1>
            </div>

            <form action="${pageContext.request.contextPath}/admin/expense/insert" method="post">
                
                <div class="form-card">
                    <div class="section-title">지출 발생 프로젝트</div>
                    <div class="grid-2">
                        <div class="form-group">
                            <label>프로젝트 선택 (Project ID)</label>
                            <select name="project_id" required>
                                <option value="">지출이 발생한 프로젝트를 선택하세요</option>
                                <c:forEach var="proj" items="${projectList}">
                                    <option value="${proj.project_id}">${proj.project_name} (${proj.project_round}차)</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>기록자 (Recorded By)</label>
                            <input type="text" name="recorded_by" value="${adminName}" readonly style="background-color: var(--gray-50); cursor: not-allowed;">
                        </div>
                    </div>
                </div>

                <div class="form-card">
                    <div class="section-title">지출 상세 정보</div>
                    <div class="grid-3">
                        <div class="form-group">
                            <label>지출 카테고리</label>
                            <select name="category" required>
                                <option value="">대분류 선택</option>
                                <option value="OPEX">운영비 (OPEX)</option>
                                <option value="CAPEX">자본지출 (CAPEX)</option>
                                <option value="LABOR">인건비</option>
                                <option value="ETC">기타</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>세부 항목 (Sub Category)</label>
                            <input type="text" name="sub_category" placeholder="예: 전기료, 종자구입비" required>
                        </div>
                        <div class="form-group">
                            <label>지출 금액 (Amount)</label>
                            <input type="number" name="amount" placeholder="단위: KRW" required>
                        </div>
                        <div class="form-group">
                            <label>지출 시작일</label>
                            <input type="date" name="start_date" required>
                        </div>
                        <div class="form-group">
                            <label>지출 종료일</label>
                            <input type="date" name="end_date" required>
                        </div>
                        <div class="form-group">
                            <label>결제처/거래처 (Vendor)</label>
                            <input type="text" name="vendor" placeholder="예: 한국전력, OO종묘" required>
                        </div>
                        <div class="form-group full">
                            <label>지출 상세 설명 <span class="optional">(선택)</span></label>
                            <textarea name="description" rows="3" placeholder="지출 증빙 번호나 구체적인 사유를 입력하세요."></textarea>
                        </div>
                    </div>
                </div>

                <button type="submit" class="btn-submit">지출 데이터 기록 완료</button>
            </form>
        </div>
    </main>


    <script src="${pageContext.request.contextPath}/resources/js/admin.js"></script>
</body>
</html>