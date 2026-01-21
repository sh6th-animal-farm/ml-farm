<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/profile.css">
<style>
    /* 탄소 배출권 전용 추가 스타일 */
    .carbon-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 32px; }
    
    .btn-market { 
        background: #1A1D23; color: #fff; border: none; padding: 12px 24px; 
        border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer;
        display: flex; align-items: center; gap: 8px;
    }

    .carbon-table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 16px; overflow: hidden; border: 1px solid #F1F1F1; }
    .carbon-table th { background: #FAFAFA; padding: 16px 24px; text-align: left; font-size: 13px; color: var(--gray-500); font-weight: 600; border-bottom: 1px solid #F1F1F1; }
    .carbon-table td { padding: 20px 24px; border-bottom: 1px solid #F1F1F1; font-size: 14px; vertical-align: middle; }

    /* 배지 스타일 */
    .c-badge { padding: 4px 10px; border-radius: 4px; font-size: 12px; font-weight: 700; display: inline-block; }
    .badge-reduction { background: #E3F2FD; color: #1E88E5; } /* 감축형 */
    .badge-removal { background: #FFF3E0; color: #FB8C00; }   /* 제거형 */
    .badge-expired { background: #F5F5F5; color: #9E9E9E; }   /* 기간 만료 */

    .project-name { font-weight: 700; color: var(--gray-900); }
    .carbon-unit { font-weight: 700; color: var(--gray-900); }
    .carbon-price { font-weight: 700; color: var(--gray-900); text-align: right; }

    .btn-more-carbon { 
        width: 100%; padding: 16px; background: #fff; border: 1px solid #F1F1F1; 
        border-radius: 12px; color: var(--gray-900); font-weight: 600; margin-top: 24px; cursor: pointer;
    }
</style>

<div class="mypage-container">
    <div class="sidebar-wrapper">
        <jsp:include page="/WEB-INF/views/common/mypage_sidebar.jsp" />
    </div>

    <div class="content-wrapper">
        <div class="carbon-header">
            <div class="page-header" style="margin-bottom: 0;">
                <h1>탄소 배출권 구매 내역</h1>
                <p>회원님이 구매하신 탄소 배출권의 상세 내역을 확인하세요.</p>
            </div>
            <button class="btn-market" onclick="location.href='${pageContext.request.contextPath}/market'">마켓으로 이동 ❯</button>
        </div>

        <table class="carbon-table">
            <thead>
                <tr>
                    <th>구분</th>
                    <th>프로젝트 정보</th>
                    <th>구매일</th>
                    <th>만료일</th>
                    <th>구매량</th>
                    <th style="text-align: right;">구매 금액</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><span class="c-badge badge-reduction">감축형</span></td>
                    <td class="project-name">강원 평창 저탄소 배추</td>
                    <td>2025. 12. 24</td>
                    <td>2026. 12. 23</td>
                    <td class="carbon-unit">5 tCO2e</td>
                    <td class="carbon-price">175,000 원</td>
                </tr>
                <tr>
                    <td><span class="c-badge badge-reduction">감축형</span></td>
                    <td class="project-name">충남 논산 킹스베리 딸기</td>
                    <td>2025. 11. 10</td>
                    <td>2026. 11. 09</td>
                    <td class="carbon-unit">10 tCO2e</td>
                    <td class="carbon-price">420,000 원</td>
                </tr>

                <tr>
                    <td><span class="c-badge badge-removal">제거형</span></td>
                    <td class="project-name">제주 감귤 바이오매스</td>
                    <td>2025. 10. 05</td>
                    <td>2026. 10. 04</td>
                    <td class="carbon-unit">2 tCO2e</td>
                    <td class="carbon-price">77,000 원</td>
                </tr>
                <tr>
                    <td><span class="c-badge badge-removal">제거형</span></td>
                    <td class="project-name">전남 해남 고구마 바이오차</td>
                    <td>2025. 09. 15</td>
                    <td>2026. 09. 14</td>
                    <td class="carbon-unit">8 tCO2e</td>
                    <td class="carbon-price">320,000 원</td>
                </tr>

                <tr>
                    <td><span class="c-badge badge-expired">기간 만료</span></td>
                    <td class="project-name">경북 의성 마늘 저탄소 비료</td>
                    <td>2023. 08. 20</td>
                    <td>2024. 08. 19</td>
                    <td class="carbon-unit">3 tCO2e</td>
                    <td class="carbon-price">99,000 원</td>
                </tr>
                <tr>
                    <td><span class="c-badge badge-expired">기간 만료</span></td>
                    <td class="project-name">경기 이천 벼 논물 관리</td>
                    <td>2023. 07. 11</td>
                    <td>2024. 07. 10</td>
                    <td class="carbon-unit">15 tCO2e</td>
                    <td class="carbon-price">450,000 원</td>
                </tr>
            </tbody>
        </table>

        <button class="btn-more-carbon">+ 더보기</button>
    </div>
</div>