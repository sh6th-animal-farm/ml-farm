<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="mp" tagdir="/WEB-INF/tags/mypage"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mypage.css">
<style>
    /* 탄소 배출권 전용 추가 스타일 */    
    .header-with-btn { display: flex; justify-content: space-between; align-items: flex-end; }
    .btn-market { 
        background: var(--gray-900); color: #fff; border: none; padding: 12px 20px; 
        border-radius: var(--radius-m); font:var(--font-caption-03); cursor: pointer;
        display: flex; align-items: center; gap: 8px; margin-bottom: 24px;
    }
</style>

<div class="mypage-container">
    <div class="sidebar-wrapper">
        <jsp:include page="/WEB-INF/views/common/mypage_sidebar.jsp" />
    </div>

    <div class="content-wrapper">
    
    	<div class="header-with-btn">
	    	<t:section_header title="탄소 배출권 구매 내역" subtitle="회원님이 구매하신 탄소 배출권의 상세 내역을 확인하세요." />
            <button class="btn-market" onclick="location.href='${pageContext.request.contextPath}/carbon/list'">마켓으로 이동 ❯</button>
    	</div>
    	
        <mp:carbon_history_table carbonList="${carbonList}"/>

		<c:if test="${not empty carbonList && carbonList.size() > 0}">
        	<button class="btn-more"  onclick="">+ 더보기</button>
		</c:if>
    </div>
</div>

<script>
document.addEventListener("DOMContentLoaded", function() {
    loadCarbonHistory();
});

function loadCarbonHistory() {
    // 로컬 스토리지에서 직접 토큰을 가져옵니다.
    const token = localStorage.getItem("accessToken");


    // fetch 요청 시 headers에 직접Authorization을 넣습니다.
    fetch(ctx + "/api/mypage/carbon-history", { 
        method: "GET",
        headers: {
            "Accept": "application/json",
            "Authorization": "Bearer " + token
        }
    })
    .then(res => {
        // 서버 컨트롤러에서 401이나 500을 던지면 여기서 캐치됩니다.
        if (!res.ok) throw new Error("데이터 로드 실패 (상태: " + res.status + ")");
        return res.json();
    })
    .then(data => {
        const tbody = document.getElementById("carbon-data-list");
        if (!tbody) return;

        tbody.innerHTML = ""; 

        if (!data || data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; padding: 60px; color: var(--gray-400);">구매하신 탄소 배출권 내역이 없습니다.</td></tr>';
            return;
        }

        let html = "";
        data.forEach(item => {
        	
        	let badgeStatus = (item.cpType === 'REDUCTION') ? "announcement" : "subscription";
            let label = (item.cpType === 'REDUCTION') ? "감축" : "제거";
            
            html += `
                <tr>
                    <td><span class="badge-\${badgeStatus}">\${label}</span></td>
                    <td class="project-name">\${item.projectName}</td>
                    <td class="buy-date">\${item.dateStr}</td>
                    <td class="expire-date">\${item.endDateStr}</td>
                    <td class="carbon-unit">\${item.amount.toLocaleString()} tCO2e</td>
                    <td class="carbon-price">\${item.price.toLocaleString()} 원</td>
                </tr>
            `;
        });
        tbody.innerHTML = html;
    })
    .catch(err => {
        console.error("Fetch Error:", err);
    });
}
</script>