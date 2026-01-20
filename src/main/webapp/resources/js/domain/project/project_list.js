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

async function starProject(userId, projectId, element) {
  if (!userId) {
    alert("로그인이 필요한 서비스입니다."); //TODO: 처리 방식 변경
    return;
  }

  let body = { userId, projectId };

  try {
    const curStarredStatus = await ProjectApi.starProject(body);
    toggleStarred(element, curStarredStatus);
  } catch (error) {
    console.error("즐겨찾기 요청 실패: ", error);
  }
}

function toggleStarred(element, curStarredStatus) {
  const icon = element.querySelector("svg path");
  const isStarred = element.getAttribute("data-starred") === "true";

  if (curStarredStatus === null || curStarredStatus != isStarred) {
    if (!isStarred) {
      icon.style.fill = "var(--green-300)";
      element.setAttribute("data-starred", "true");
    } else {
      icon.style.fill = "var(--gray-900)";
      element.setAttribute("data-starred", "false");
    }
  }
}

window.starProject = starProject;
window.searchKeyword = searchKeyword;
window.filterCategory = filterCategory;
window.toggleStarred = toggleStarred;
