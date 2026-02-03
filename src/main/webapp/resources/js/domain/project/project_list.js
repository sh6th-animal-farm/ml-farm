import { ProjectApi } from "./project_api.js";

async function filterCategory(category, element) {
    // 1. 버튼 활성화 UI 즉시 변경 (UX 향상)
    document
        .querySelectorAll(".menu-btn")
        .forEach((btn) => btn.classList.remove("active"));
    if (element) element.classList.add("active");

    // 2. URL 파라미터 변경 (새로고침 없이 주소창만 변경)
    const url = new URL(window.location.href);
    if (category) url.searchParams.set("projectStatus", category);
    else url.searchParams.delete("projectStatus");
    window.history.pushState({}, "", url);
    // 3. Fetch로 HTML 조각 요청
    try {
        const html = await ProjectApi.searchProjects(url.search);

        // 4. 특정 영역만 교체
        document.getElementById("projectCardContainer").innerHTML = html;
    } catch (error) {
        console.error("데이터 로드 실패: ", error);
    }
}

async function searchKeyword() {
    let keyword = document.querySelector(".search-input").value;
    console.log(keyword);
    const url = new URL(window.location.href);
    keyword = keyword.trim();
    if (keyword) url.searchParams.set("keyword", keyword);
    else url.searchParams.delete("keyword");
    window.history.pushState({}, "", url);
    try {
        const html = await ProjectApi.searchProjects(url.search);

        document.getElementById("projectCardContainer").innerHTML = html;
    } catch (error) {
        console.error("데이터 로드 실패: ", error);
    }
}

function toggleStarred(element, curStarredStatus) {
    const icon = element.querySelector("svg path");
    const isStarred = element.getAttribute("data-starred") === "true";

    if (curStarredStatus!=null) {
        icon.style.fill = curStarredStatus ? "var(--error)" : "white";
        element.setAttribute("data-starred", curStarredStatus);
        return
    }

    icon.style.fill = !isStarred ? "var(--error)" : "white";
    element.setAttribute("data-starred", !isStarred);
}

async function handleHeartIcon (projectId, element) {
    let body = projectId;
    toggleStarred(element, null);

    try {
        const curStarredStatus = await ProjectApi.starProject(body);
        console.log(curStarredStatus);
        toggleStarred(element, curStarredStatus);
    } catch (error) {
        console.error("즐겨찾기 요청 실패: ", error);
    }

}

async function initStarredStatus() {
    // 화면에 있는 모든 프로젝트 카드에서 ID 추출
    const cards = document.querySelectorAll('.project-card');
    
    // 각 카드별로 서버에 관심 여부 확인 (비동기)
    cards.forEach(async (card) => {
        const projectId = card.getAttribute('onclick').match(/\d+/)[0]; 
        
        try {
            const isStarred = await ProjectApi.getIsStarred(projectId);
            
            if (isStarred) {
                // 관심 프로젝트라면 하트 아이콘의 색상을 빨간색으로 변경
                const heartIcon = card.querySelector(`.heart-icon-${projectId}`);
                if (heartIcon) {
                    heartIcon.classList.add('active')
                    card.querySelector('.interest-btn').setAttribute('data-starred', 'true');
                }
            }
        } catch (e) {
            // 로그인 안 한 경우 false가 오거나 에러가 날 수 있으나 무시 (기본 흰하트 유지)
        }
    });
}

document.addEventListener("DOMContentLoaded", ()=>{
    initStarredStatus();
})

window.searchKeyword = searchKeyword;
window.filterCategory = filterCategory;
window.toggleStarred = toggleStarred;
window.handleHeartIcon = handleHeartIcon;