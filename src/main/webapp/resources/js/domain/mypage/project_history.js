/* global ctx */
(() => {
  const state = { type: "JOIN", status: "ALL", page: 1, size: 10 };

  const tabsEl = document.getElementById("projectTabs");
  const filtersEl = document.getElementById("projectFilters");
  const listEl = document.getElementById("mpProjectList");
  const moreBtn = document.getElementById("mpMoreBtn"); // 너는 +더보기 버튼 쓸 수도
  const joinCountEl = document.getElementById("tabJoinCount");
  const starCountEl = document.getElementById("tabStarCount");
  
  window.mpSetStatus = function (status) {
    state.status = status;
    state.page = 1;

    // 버튼 active 스타일 토글 (menu_button이 data-status 없을 수 있어서 텍스트로도 처리)
    const labelByStatus = {
      ALL: "전체보기",
      SUBSCRIPTION: "청약중",
      ANNOUNCEMENT: "공고중",
      ENDED: "종료됨",
    };

    const targetLabel = labelByStatus[status];
    if (filtersEl) {
      filtersEl.querySelectorAll("button, a").forEach((b) => {
        const isTarget = targetLabel && b.textContent?.includes(targetLabel);
        b.classList.toggle("is-active", !!isTarget);
      });
    }

    loadList(true);
  };

  // ====== 공통: 안전 파서 ======
  function pickItems(json) {
    // 1) 배열이면 그대로
    if (Array.isArray(json)) return json;

    // 2) PagedResponseDTO 예상 케이스들
    if (json && Array.isArray(json.items)) return json.items;
    if (json && Array.isArray(json.content)) return json.content;
    if (json && Array.isArray(json.list)) return json.list;

    // 3) ApiResponseDTO(payload) 케이스
    if (json && json.payload) return pickItems(json.payload);
    if (json && json.data) return pickItems(json.data);

    return [];
  }

  function pickTabs(json) {
    const obj = json?.payload ?? json?.data ?? json ?? {};
    const joined =
      obj.joined ?? obj.joinCount ?? obj.joinedCount ?? obj.tabJoinCount ?? 0;
    const starred =
      obj.starred ?? obj.starCount ?? obj.starredCount ?? obj.tabStarCount ?? 0;

    return {
      joined: Number(joined) || 0,
      starred: Number(starred) || 0,
    };
  }

  async function apiGet(url) {
    const token = localStorage.getItem("accessToken");
    const res = await fetch(url, {
      method: "GET",
      headers: {
        Accept: "application/json",
        Authorization: token ? "Bearer " + token : "",
      },
    });

    // ❗️여기서 alert 금지: 조용히 throw
    if (!res.ok) {
      const text = await res.text().catch(() => "");
      throw new Error(`GET ${url} failed (${res.status}) ${text}`);
    }
    return res.json();
  }

  // ====== 탭 카운트 로드 (alert 절대 없음) ======
  async function fetchTabs() {
    try {
      const json = await apiGet(ctx + "/api/mypage/projects/tabs");
      const { joined, starred } = pickTabs(json);

      if (joinCountEl) joinCountEl.textContent = joined;
      if (starCountEl) starCountEl.textContent = starred;
    } catch (e) {
      console.warn("[tabs] fetch failed:", e.message);
      if (joinCountEl) joinCountEl.textContent = "0";
      if (starCountEl) starCountEl.textContent = "0";
    }
  }

  // ====== 리스트 로드 ======
  async function loadList(reset) {
    try {
      const url =
        ctx +
        `/api/mypage/projects?type=${encodeURIComponent(state.type)}` +
        `&status=${encodeURIComponent(state.status)}` +
        `&page=${state.page}&size=${state.size}`;

      const json = await apiGet(url);
      const items = pickItems(json); // ✅ 여기서 무조건 배열로 정규화

      if (reset) listEl.innerHTML = "";

      renderItems(items);

      // hasNext / total 같은 거 쓰고 싶으면 여기서 안전 처리
      const hasNext = (json?.hasNext ?? json?.payload?.hasNext ?? false) === true;

      if (moreBtn) moreBtn.style.display = hasNext ? "block" : "none";
    } catch (e) {
      console.warn("[list] load failed:", e.message);
      listEl.innerHTML = `<div class="empty-box">데이터를 불러오지 못했어요.</div>`;
      if (moreBtn) moreBtn.style.display = "none";
    }
  }
  
  

  // ====== 렌더 ======
  function escapeHtml(s) {
    return String(s ?? "")
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;")
      .replaceAll("'", "&#39;");
  }

  function renderItems(items) {
	  if (!Array.isArray(items) || items.length === 0) {
	    listEl.innerHTML = `<div class="mp-empty">데이터가 없습니다.</div>`;
	    return;
	  }
	
	  const html = items
	    .map((it) => {
	      const name = escapeHtml(it.projectName ?? it.name ?? "-");
	      const period = escapeHtml(it.periodText ?? it.period ?? "");
	      const projectStatus = escapeHtml(it.statusText1 ?? it.status1 ?? "");
	      const myStatus = escapeHtml(it.statusText2 ?? it.status2 ?? "");
	      const projectId = it.projectId ?? it.project_id ?? it.id ?? "";
	
	      // ✅ 대표 상태: 내 상태 우선
	      const displayStatus = myStatus || projectStatus;
	
	      return `
	        <div class="project-row">
	          <div class="left">
	            <div class="title">${name}</div>
	            <div class="sub">${period}</div>
	          </div>
	
	          <div class="right">
	            ${
	              displayStatus
	                ? `<span class="badge ${myStatus ? "yellow" : "green"}">${displayStatus}</span>`
	                : ``
	            }
	            <button class="btn-arrow" type="button" aria-label="상세 이동">
  					<span class="chevron"></span>
				</button>
	          </div>
	        </div>
	      `;
	    })
	    .join("");
	
	  // ❗ 카드 감싸지 말고 그대로 리스트
	  listEl.innerHTML = html;
	
	  // 버튼 이벤트
	  listEl.querySelectorAll(".btn-trade").forEach((btn) => {
	    btn.onclick = (e) => {
	      e.stopPropagation();
	      const pid = btn.getAttribute("data-project-id");
	      if (!pid) return;
	      location.href = ctx + "/projects/" + pid;
	    };
	  });
	}


  // ====== 이벤트 바인딩 ======
  function bindTabs() {
    if (!tabsEl) return;
    tabsEl.addEventListener("click", (e) => {
      const btn = e.target.closest("[data-type]");
      if (!btn) return;

      state.type = btn.getAttribute("data-type");
      state.page = 1;

      tabsEl.querySelectorAll("[data-type]").forEach((b) => b.classList.remove("is-active"));
      btn.classList.add("is-active");

      loadList(true);
    });
  }

  function bindFilters() {
    if (!filtersEl) return;
    filtersEl.addEventListener("click", (e) => {
      const btn = e.target.closest("[data-status]");
      if (!btn) return;

      state.status = btn.getAttribute("data-status");
      state.page = 1;

      filtersEl.querySelectorAll("[data-status]").forEach((b) => b.classList.remove("is-active"));
      btn.classList.add("is-active");

      loadList(true);
    });
  }

  function bindMore() {
    if (!moreBtn) return;
    moreBtn.onclick = async () => {
      state.page += 1;
      await loadList(false);
    };
  }

  // ====== init ======
  document.addEventListener("DOMContentLoaded", async () => {
    bindTabs();
    bindFilters();
    bindMore();

    await fetchTabs();
    await loadList(true);
  });
})();
