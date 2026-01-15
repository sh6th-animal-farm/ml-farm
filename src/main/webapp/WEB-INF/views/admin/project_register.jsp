<%@ page language="java" contentType="text/html; charset=UTF-8"
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
                <h1>프로젝트 등록/수정</h1>
                <button type="button" class="btn-load" onclick="openModal()">프로젝트 정보 불러오기</button>
            </div>

            <form action="/admin/project/insert" method="post">
                
                <div class="form-card">
                    <div class="section-title">기본 정보</div>
                    <div class="grid-3">
                        <div class="form-group">
                            <label>농장 선택 (Farm ID)</label>
                            <select name="farm_id" required>
                                <option value="">농장을 선택하세요</option>
                                <option value="1">청라 스마트팜</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>프로젝트 명</label>
                            <input type="text" name="project_name" required>
                        </div>
                        <div class="form-group">
                            <label>프로젝트 차수 (Round)</label>
                            <input type="number" name="project_round" value="1" required>
                        </div>
                        <div class="form-group full">
                            <label>프로젝트 상세 설명 <span class="optional">(선택)</span></label>
                            <textarea name="project_description" rows="3"></textarea>
                        </div>
                    </div>
                </div>

                <div class="form-card">
                    <div class="section-title">투자 설정</div>
                    <div class="grid-3">
                        <div class="form-group">
                            <label>토큰 명칭</label>
                            <input type="text" name="token_name" required>
                        </div>
                        <div class="form-group">
                            <label>종목 코드 (Ticker)</label>
                            <input type="text" name="ticker_symbol" required>
                        </div>
                        <div class="form-group">
                            <label>목표 금액 (Target)</label>
                            <input type="number" name="target_amount" required>
                        </div>
                        <div class="form-group">
                            <label>1인당 최소 투자금</label>
                            <input type="number" name="min_amount_per_investor" required>
                        </div>
                        <div class="form-group">
                            <label>현재 모집 금액 <span class="optional">(선택)</span></label>
                            <input type="number" name="actual_amount" value="0">
                        </div>
                    </div>
                </div>

                <div class="form-card">
                    <div class="section-title">일정 및 기타</div>
                    <div class="grid-2">
                        <div class="form-group"><label>공고 시작일</label><input type="datetime-local" name="announcement_start_date" required></div>
                        <div class="form-group"><label>공고 종료일</label><input type="datetime-local" name="announcement_end_date" required></div>
                        <div class="form-group"><label>청약 시작일</label><input type="datetime-local" name="subscription_start_date" required></div>
                        <div class="form-group"><label>청약 종료일</label><input type="datetime-local" name="subscription_end_date" required></div>
                        <div class="form-group"><label>결과 발표일</label><input type="datetime-local" name="result_announcement_date" required></div>
                        <div class="form-group">
                            <label>예상 수익률 (ROI)</label>
                            <input type="number" step="0.1" name="expected_return" required>
                        </div>
                        <div class="form-group"><label>사업 시작일</label><input type="datetime-local" name="project_start_date" required></div>
                        <div class="form-group"><label>사업 종료일</label><input type="datetime-local" name="project_end_date" required></div>
                        <div class="form-group">
                            <label>현장 관리자 수</label>
                            <input type="number" name="manager_count" required>
                        </div>
                    </div>
                </div>
                
                <div class="button-group">
                    <button type="button" class="btn-submit register-btn" onclick="insertProject()">프로젝트 등록</button>
                    <button type="button" class="btn-submit edit-btn" onclick="updateProject()">프로젝트 수정</button>
                </div>
            </form>
        </div>
    </main>

    <div id="projectModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <span>지난 프로젝트 목록 선택</span>
            <span style="cursor:pointer" onclick="closeModal()">&times;</span>
        </div>
        
        <div id="loadingSpinner" style="display: none; text-align: center; padding: 20px;">
            <div class="spinner"></div> <p>프로젝트 정보를 불러오는 중...</p>
        </div>

        <ul id="projectListContainer" class="project-list">
            </ul>
    </div>
</div>
    
    <script src="${pageContext.request.contextPath}/resources/js/admin.js"></script>
</body>
</html>