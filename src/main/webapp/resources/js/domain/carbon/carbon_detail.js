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

        if (result && result.payload) {
            renderPage(result.payload); 
        } else {
            console.warn("데이터가 비어있습니다: ", result.message);
            //[테스트용, 실제로는 아래 삭제]
            renderPage(null); // payload가 없으면 null을 보내 가상값 유도
        }
    } catch (error) {
        console.error("데이터 로드 중 오류 발생:", error);
        //[테스트용, 실제로는 아래 삭제]
        renderPage(null); // payload가 없으면 null을 보내 가상값 유도
    }
}

function renderPage(data) {
    // const info = data.carbonInfo;
    // const benefit = data.userBenefit;

    // // 상단 태그 및 제목 바인딩
    // document.getElementById("tagText").innerText = info.cpType + " 프로젝트 | " + info.vintageYear + " 빈티지";
    // document.getElementById("titleText").innerText = info.cpTitle;
    
    // // 위치 조립 (컬럼 사이 공백 추가)
    // const locationStr = "위치: " + (data.addressSido || "") + " " + (data.addressSigungu || "") + " " + 
    //                     (data.addressStreet || "") + " " + (data.addressDetails || "") + " " + (data.farmName || "") + " 일대";
    // document.getElementById("locationText").innerText = locationStr;

    // // 주요 정보 카드 매핑
    // document.getElementById("valCertificate").innerText = info.productCertificate; //인증기관
    // document.getElementById("valType").innerText = info.cpType; //상품유형
    // document.getElementById("valInitAmount").innerText = info.initAmount.toLocaleString() + " tCO2e"; //초기수량(발급수량)
    // document.getElementById("valCpAmount").innerText = info.cpAmount.toLocaleString() + " tCO2e"; //남은 수량(재고수량)
    // document.getElementById("valDetail").innerText = info.cpDetail; //설명

    // // 사이드바 가격 정보 (팀장님 invest-card 스타일)
    // document.getElementById("sideOriginalPrice").innerText = info.cpPrice.toLocaleString() + " KRW";
    // document.getElementById("sideDiscount").innerText = benefit.discountRate + "% 할인";
    // document.getElementById("sideCurrentPrice").innerText = benefit.currentPrice.toLocaleString() + "원";

    // 1. [가상 데이터 정의] 데이터가 null일 때 사용할 기본값들입니다.
    // data가 null이면 이 객체들이 화면을 채웁니다.
    const info = data?.carbonInfo || {
        cpTitle: "[전주] 스마트팜 바이오차 VER 1호",
        cpType: "감축형",
        vintageYear: "2025",
        productCertificate: "VER (Voluntary Emission Reduction)",
        initAmount: 5000,
        cpAmount: 2500,
        cpPrice: 140000,
        cpDetail: "토양 비옥도 향상 및 비료 사용량 절감, 지역 농업 용수 수질 개선, 농가 부가 소득 증대 및 탄소 중립 실현 기여"
    };

    const benefit = data?.userBenefit || {
        discountRate: 15,
        currentPrice: 119000,
        myTokenBalance: 3250000
    };

    // 2. 상단 태그 및 제목 바인딩
    document.getElementById("tagText").innerText = (info.cpType || "감축형") + " 프로젝트 | " + (info.vintageYear || "2025") + " 빈티지";
    document.getElementById("titleText").innerText = info.cpTitle || "프로젝트 정보를 불러올 수 없습니다.";
    
    // 3. [핵심 수정] 위치 조립 (data가 null일 때 안전하게 처리)
    // data.addressSido 대신 data?.addressSido 를 사용하여 null 에러를 방지합니다.
    const locationStr = "위치: " + (data?.addressSido || "전북") + " " + (data?.addressSigungu || "전주시") + " " + 
                        (data?.addressStreet || "덕진구") + " " + (data?.addressDetails || "B농장") + " 일대";
    document.getElementById("locationText").innerText = locationStr;

    // 4. 주요 정보 카드 매핑 (toLocaleString 호출 전 널 체크 필수!)
    document.getElementById("valCertificate").innerText = info.productCertificate || "-";
    document.getElementById("valType").innerText = (info.cpType || "감축형") + " 프로젝트";
    
    // (값 || 0) 처리를 해야만 toLocaleString()에서 에러가 나지 않습니다.
    document.getElementById("valInitAmount").innerText = (info.initAmount || 0).toLocaleString() + " tCO2e";
    document.getElementById("valCpAmount").innerText = (info.cpAmount || 0).toLocaleString() + " tCO2e";
    document.getElementById("valDetail").innerText = info.cpDetail || "상세 설명이 없습니다.";

    // 5. 사이드바 가격 정보 (팀장님 invest-card 스타일)
    document.getElementById("sideOriginalPrice").innerText = (info.cpPrice || 0).toLocaleString() + " KRW";
    document.getElementById("sideDiscount").innerText = (benefit.discountRate || 0) + "% 할인";
    document.getElementById("sideCurrentPrice").innerText = (benefit.currentPrice || 0).toLocaleString() + "원";
}