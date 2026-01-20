<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="regions">
    전국 전체:;
    서울특별시:강남구,강동구,강북구,강서구,관악구,광진구,구로구,금천구,노원구,도봉구,동대문구,동작구,마포구,서대문구,서초구,성동구,성북구,송파구,양천구,영등포구,용산구,은평구,종로구,중구,중랑구;
    부산광역시:강서구,금정구,기장군,남구,동구,동래구,부산진구,북구,사상구,사하구,서구,수영구,연제구,영도구,중구,해운대구;
    대구광역시:남구,달서구,달성군,동구,북구,서구,수성구,중구,군위군;
    인천광역시:강화군,계양구,미추홀구,남동구,동구,부평구,서구,연수구,옹진군,중구;
    광주광역시:광산구,남구,동구,북구,서구;
    대전광역시:대덕구,동구,서구,유성구,중구;
    울산광역시:남구,동구,북구,울주군,중구;
    세종특별자치시:세종시;
    경기도:가평군,고양시,과천시,광명시,광주시,구리시,군포시,김포시,남양주시,동두천시,부천시,성남시,수원시,시흥시,안산시,안성시,안양시,양주시,양평군,여주시,연천군,오산시,용인시,의왕시,의정부시,이천시,파주시,평택시,포천시,하남시,화성시;
    강원도:강릉시,고성군,동해시,삼척시,속초시,양구군,양양군,영월군,원주시,인제군,정선군,철원군,춘천시,태백시,평창군,홍천군,화천군,횡성군;
    충청북도:괴산군,단양군,보은군,영동군,옥천군,음성군,제천시,증평군,진천군,청주시,충주시;
    충청남도:계룡시,공주시,금산군,논산시,당진시,보령시,부여군,서산시,서천군,아산시,예산군,천안시,청양군,태안군,홍성군;
    전라북도:고창군,군산시,김제시,남원시,무주군,부안군,순창군,완주군,익산시,임실군,장수군,전주시,진안군,정읍시;
    전라남도:강진군,고흥군,곡성군,광양시,구례군,나주시,담양군,목포시,무안군,보성군,순천시,신안군,여수시,영광군,영암군,완도군,장성군,장흥군,진도군,함평군,해남군,화순군;
    경상북도:경산시,경주시,고령군,구미시,김천시,문경시,봉화군,상주시,성주군,안동시,영덕군,영양군,영주시,영천시,예천군,울릉군,울진군,의성군,청도군,청송군,칠곡군,포항시;
    경상남도:거제시,거창군,고성군,김해시,남해군,밀양시,사천시,산청군,양산시,의령군,진주시,창녕군,창원시,통영시,하동군,함안군,함양군,합천군;
    제주특별자치도:제주시,서귀포시
</c:set>

<div class="region-accordion-container">
    <%-- 데이터 파싱 및 반복문 통합 --%>
    <c:forEach var="raw" items="${fn:split(regions, ';')}">
        <c:set var="parts" value="${fn:split(raw, ':')}" />
        <c:set var="mainRegion" value="${fn:trim(parts[0])}" />
        <c:set var="subRegions" value="${fn:trim(parts[1])}" />
        
        <div class="region-item ${mainRegion == '전국 전체' ? 'active' : ''}">
            <div class="region-header ${mainRegion == '전국 전체' ? 'no-arrow' : ''}" onclick="toggleAccordion(this)">
                ${mainRegion}
                <c:if test="${mainRegion != '전국 전체'}">
                    <span class="arrow">▼</span>
                </c:if>
            </div>
            
            <%-- 세부 지역이 있는 경우에만 영역 생성 --%>
            <c:if test="${not empty subRegions}">
                <div class="region-content">
                    <ul>
                        <c:forEach var="sub" items="${fn:split(subRegions, ',')}">
                            <li>${sub}</li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>
        </div>
    </c:forEach>
</div>

<script>
function toggleAccordion(header) {
    const item = header.parentElement;
    const isActive = item.classList.contains('open');
    
    // 다른 열려있는 메뉴 닫기 (선택 사항)
    document.querySelectorAll('.region-item').forEach(el => {el.classList.remove('open');el.classList.remove('active');});
    
    // 클릭한 메뉴 토글
    if (!isActive) {
        item.classList.add('open');
        item.classList.add('active');
    }
}
</script>

<style>
.region-accordion-container {
    width: 280px;
    height: 100%; /* 요청하신 고정 높이 */
    background: white;
    border: 1px solid var(--gray-100);
    border-radius: var(--radius-l);
    overflow-y: auto; /* 내용이 높이를 넘어가면 스크롤 */
    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

/* 스크롤바 커스텀 (선택) */
.region-accordion-container::-webkit-scrollbar { width: 4px; }
.region-accordion-container::-webkit-scrollbar-thumb { background: var(--gray-200); border-radius: 2px; }

.region-item {
    border-bottom: 1px solid var(--gray-50);
}

.region-header {
    padding: 16px 20px;
    font: var(--font-caption-02);
    color: var(--gray-700);
    display: flex;
    justify-content: space-between;
    align-items: center;
    cursor: pointer;
    background: white;
    transition: all 0.2s ease;
}

.region-header:hover { background: #f7fcf5;color: var(--green-600); /* 마우스만 올려도 글자색이 변하도록 설정 */ }

/* 3. 활성화된(선택된) 상태의 스타일 (Active/Active Item) */
.region-item.active .region-header {
    background: #f7fcf5; /* 이미지의 연한 녹색 배경 */
    color: var(--green-600);
    font-weight: 600;
}

.arrow {
    font-size: 6px;
    transition: transform 0.3s;
    color: var(--gray-400);
}

/* 펼쳐졌을 때 스타일 */
.region-item.open .region-content { display: block; }
.region-item.open .arrow { transform: rotate(180deg); }

.region-content {
    display: none;
    background: #fafafa;
    padding: 8px 0;
}

.region-content ul { list-style: none; padding: 0; margin: 0; }
.region-content li {
    padding: 10px 40px;
    font: var(--font-caption-01);
    color: var(--gray-500);
    cursor: pointer;
}

.region-content li:hover { color: var(--green-600); background: #f0f0f0; }
</style>