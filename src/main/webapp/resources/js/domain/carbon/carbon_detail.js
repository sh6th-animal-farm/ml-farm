// carbon_detail.js
document.addEventListener("DOMContentLoaded", function() {
    const cpId = document.getElementById("targetCpId").value;
    if(cpId) {
        loadCarbonDetail(cpId);
    }
});

async function loadCarbonDetail(cpId) {
    try {
        // 1. 팀장님이 정하신 방식대로 localStorage에서 토큰을 가져옵니다.
        const token = localStorage.getItem("accessToken");

        // 2. fetch 요청 시 헤더에 신분증(JWT)을 첨부합니다.
        const response = await fetch(ctx + "/api/carbon/" + cpId, {
            method: "GET",
            headers: {
                "Authorization": token ? "Bearer " + token : "",
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            // 401(만료)이나 403(권한없음) 에러가 나면 팀장님이 만든 강제 로그아웃 실행
            if (response.status === 401 || response.status === 403) {
                console.warn("세션 만료 또는 권한 없음. 강제 로그아웃을 실행합니다.");
                if (typeof AuthManager !== 'undefined') {
                    AuthManager.forceLogout();
                }
                return;
            }
            throw new Error("서버 응답 에러: " + response.status);
        }

        const result = await response.json(); //규격 { message, payload } 처리

        // 1. 결과 데이터가 정상적으로 존재할 때만 렌더링 함수 호출
        if (result && result.payload) {
            renderPage(result.payload); 
        } else {
            console.error("데이터 로드 실패: payload가 없습니다.");
        }
    } catch (error) {
        console.error("데이터 로드 중 오류 발생:", error);
    }
}

function renderPage(data) {

    // 데이터 구조 분해 할당 (데이터가 없을 경우를 대비해 기본값 설정)
    if (!data || !data.carbonInfo) {
        console.error("렌더링할 데이터가 유효하지 않습니다.");
        return;
    }

    const info = data.carbonInfo;
    const benefit = data.userBenefit;

    // info(CarbonDTO)가 아니라 data(CarbonDetailDTO)에서 직접 꺼냅니다.
    const mainImg = document.getElementById("detailMainImg");
    if (mainImg) {
        mainImg.src = data.thumbnailUrl || (ctx + "/resources/img/carbon_sample.jpg");
    }


    // 상단 태그 및 제목 바인딩
    document.getElementById("tagText").innerText = info.cpType + " 프로젝트 | " + info.vintageYear + " 빈티지";
    document.getElementById("titleText").innerText = info.cpTitle;
    
    // 위치 조립 (컬럼 사이 공백 추가)
    const locationStr = "위치: " + (data.addressSido || "") + " " + (data.addressSigungu || "") + " " + 
                        (data.addressStreet || "") + " " + (data.addressDetails || "") + " " + (data.farmName || "") + " 일대";
    document.getElementById("locationText").innerText = locationStr;

    // 주요 정보 카드 매핑
    document.getElementById("valCertificate").innerText = info.productCertificate; //인증기관
    document.getElementById("valType").innerText = info.cpType; //상품유형
    document.getElementById("valInitAmount").innerText = Number(info.initAmount || 0).toLocaleString() + " tCO2e";//초기수량(발급수량)
    document.getElementById("valCpAmount").innerText = Number(info.cpAmount || 0).toLocaleString() + " tCO2e";//남은 수량(재고수량)
    document.getElementById("valDetail").innerText = info.cpDetail;//설명

    // 사이드바 가격 정보 (팀장님 invest-card 스타일)
    document.getElementById("sideOriginalPrice").innerText = Number(info.cpPrice || 0).toLocaleString() + " KRW";
    document.getElementById("sideDiscount").innerText = (benefit.discountRate || 0) + "% 할인";
    document.getElementById("sideCurrentPrice").innerText = Number(benefit.currentPrice || 0).toLocaleString() + "원";
}