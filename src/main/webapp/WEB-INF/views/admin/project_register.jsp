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

            <form action="${pageContext.request.contextPath}/api/project/insert" method="post">
                
                <div class="form-card">
                    <div class="section-title">기본 정보</div>
                    <div class="grid-3">
                        <div class="form-group">
                            <label>농장 선택 (Farm ID)</label>
                            <select name="farmId" required>
                            	<option value="">농장을 선택하세요</option>
                            	<c:forEach items="${farmlist}" var="farmdata">
                            		<option value="${farmdata.farmId}">${farmdata.farmName}</option>
                            	</c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>프로젝트 명</label>
                            <input type="text" name="projectName" required>
                        </div>
                        <div class="form-group">
                            <label>프로젝트 차수 (Round)</label>
                            <input type="number" name="projectRound" value="1" required>
                        </div>
                        <div class="form-group full">
                            <label>프로젝트 상세 설명 <span class="optional">(선택)</span></label>
                            <textarea name="projectDescription" rows="3"></textarea>
                        </div>
                    </div>
                </div>

                <div class="form-card">
                    <div class="section-title">투자 설정</div>
                    <div class="grid-3">
                        <div class="form-group">
                            <label>토큰 명칭</label>
                            <input type="text" name="tokenName" required>
                        </div>
                        <div class="form-group">
                            <label>종목 코드 (Ticker)</label>
                            <input type="text" name="tickerSymbol" required>
                        </div>
                        <div class="form-group">
                            <label>목표 금액 (Target)</label>
                            <input type="number" name="targetAmount" required>
                        </div>
                        <div class="form-group">
                            <label>1인당 최소 투자금</label>
                            <input type="number" name="minAmountPerInvestor" required>
                        </div>
                        <div class="form-group">
                            <label>현재 모집 금액 <span class="optional">(선택)</span></label>
                            <input type="number" name="actualAmount" value="0">
                        </div>
                        <div class="form-group">
                            <label>토큰 발행량 <span class="optional"></span></label>
                            <input type="number" name="totalSupply" value="0">
                        </div>
                    </div>
                </div>

                <div class="form-card">
                    <div class="section-title">일정 및 기타</div>
                    <div class="grid-2">
                        <div class="form-group"><label>공고 시작일</label><input type="datetime-local" name="announcementStartDate" ></div>
                        <div class="form-group"><label>공고 종료일</label><input type="datetime-local" name="announcementEndDate" ></div>
                        <div class="form-group"><label>청약 시작일</label><input type="datetime-local" name="subscriptionStartDate" ></div>
                        <div class="form-group"><label>청약 종료일</label><input type="datetime-local" name="subscriptionEndDate" ></div>
                        <div class="form-group"><label>결과 발표일</label><input type="datetime-local" name="resultAnnouncementDate" ></div>
                        <div class="form-group">
                            <label>예상 수익률 (ROI)</label>
                            <input type="number" step="0.1" name="expectedReturn" required>
                        </div>
                        <div class="form-group"><label>사업 시작일</label><input type="datetime-local" name="projectStartDate" ></div>
                        <div class="form-group"><label>사업 종료일</label><input type="datetime-local" name="projectEndDate" ></div>
                        <div class="form-group">
                            <label>현장 관리자 수</label>
                            <input type="number" name="managerCount" required>
                        </div>
                    </div>
                </div>

                <button type="submit" class="btn-submit">새 프로젝트 등록 완료</button>
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