/* carbon_detail.js */

document.addEventListener("DOMContentLoaded", function() {
    // 페이지 로드 시 바로 데이터 호출
    if (CURRENT_CP_ID) {
        fetchCarbonDetail(CURRENT_CP_ID);
    }

    // 주문 버튼 클릭 리스너 (나중에 모달 연결)
    document.getElementById("btnOrderOpen").addEventListener("click", function() {
        alert("주문 신청 모달은 준비 중입니다! (한도: " + this.dataset.maxLimit + ")");
    });
});

/**
 * 서버 API를 호출하여 상세 데이터를 가져옵니다.
 */
function fetchCarbonDetail(cpId) {
    fetch(`/api/carbon/${cpId}`)
        .then(response => {
            if (!response.ok) throw new Error("데이터를 불러올 수 없습니다.");
            return response.json();
        })
        .then(data => {
            renderDetail(data);
        })
        .catch(error => {
            console.error(error);
            alert("상세 정보를 가져오는 데 실패했습니다.");
        });
}

/**
 * 가져온 데이터를 HTML 요소에 바인딩합니다.
 */
function renderDetail(data) {
    const info = data.carbonInfo;
    const benefit = data.userBenefit; // 부서장님이 제안하신 구조

    // 1. 프로젝트 기본 정보
    document.getElementById("projectName").innerText = info.projectName;
    document.getElementById("projectLoc").innerText = info.projectLoc || "전국 일대";
    document.getElementById("vintageYear").innerText = info.vintageYear + " 빈티지";
    document.getElementById("cpType").innerText = (info.cpType === 'REMOVAL') ? "제거형 프로젝트" : "감축형 프로젝트";
    
    // 2. 카드 상세 정보
    document.getElementById("productCertificate").innerText = info.productCertificate;
    document.getElementById("cpDetail").innerText = info.cpDetail;
    document.getElementById("initAmount").innerText = formatNumber(info.initAmount) + " tCO2e";
    document.getElementById("cpAmount").innerText = formatNumber(info.cpAmount) + " tCO2e";

    // 3. 가격 사이드바 (UserBenefitDTO 활용)
    document.getElementById("originalPrice").innerText = formatNumber(info.cpPrice) + " KRW";
    document.getElementById("discountRate").innerText = benefit.discountRate + "% 할인";
    document.getElementById("finalPrice").innerText = formatNumber(benefit.currentPrice);
    
    // 4. 구매 버튼에 한도 데이터 저장 (나중에 모달에서 쓰기 위함)
    document.getElementById("btnOrderOpen").dataset.maxLimit = benefit.userMaxLimit;
}

/**
 * 숫자에 콤마를 찍어주는 유틸 함수
 */
function formatNumber(num) {
    return new Intl.NumberFormat().format(num);
}