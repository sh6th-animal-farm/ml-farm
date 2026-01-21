(function () {
  
  const base = (typeof ctx !== "undefined") ? ctx : "";

  const container = document.getElementById("carbonCardContainer");
  if (!container) return;

  // 현재 화면에 표시 중인 목록(렌더 대상)
  let viewItems = [];
  // 전체 데이터 캐시(검색용)
  let allItemsCache = null;

  // ===== 공통 유틸 =====
  function escapeHtml(str) {
    return String(str ?? "")
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;")
      .replaceAll("'", "&#039;");
  }

  function formatNumber(n) {
    const num = Number(n);
    if (Number.isNaN(num)) return escapeHtml(n);
    return num.toLocaleString("ko-KR");
  }

  function pickBadge(item) {
    const category = (item.category || "").toUpperCase();
    const year = item.vintageYear ? String(item.vintageYear) : "";
    if (category === "REDUCTION") return { cls: "blue", text: `감축형 · ${year || "-"}` };
    if (category === "REMOVAL") return { cls: "green", text: `제거형 · ${year || "-"}` };
    return { cls: "", text: `${year || "빈티지"}` };
  }

  function pickButtonClass(item) {
    const category = (item.category || "").toUpperCase();
    if (category === "REMOVAL") return "btn-green";
    if (category === "REDUCTION") return "btn-blue";
    return "btn-dark";
  }

  function setActive(btn) {
    document.querySelectorAll(".filter-group .menu-btn").forEach(b => b.classList.remove("active"));
    if (btn) btn.classList.add("active");
  }

  function render(list) {
    if (!Array.isArray(list) || list.length === 0) {
      container.innerHTML = `<div style="padding:20px; color:#777;">표시할 상품이 없습니다.</div>`;
      return;
    }

    container.innerHTML = list.map(item => {
      const title = escapeHtml(item.cpDetail || item.productCertificate || "탄소 상품");
      const badge = pickBadge(item);

      // item.imageUrl 있으면 사용, 없으면 기본 이미지(없어도 깨지지 않게)
      const imgUrl = item.imageUrl
        ? `${escapeHtml(item.imageUrl)}`
        : `${base}/resources/img/carbon_sample.jpg`;

      const amount = item.cpAmount ?? "-";
      const price = item.cpPrice ?? "-";
      const cert = escapeHtml(item.productCertificate ?? "-");

      const btnCls = pickButtonClass(item);

      return `
        <article class="carbon-card" data-category="${escapeHtml(item.category)}">
          <div class="carbon-img">
            <img src="${imgUrl}" alt="carbon" onerror="this.style.display='none';" />
            <span class="badge ${badge.cls}">${escapeHtml(badge.text)}</span>
          </div>

          <div class="carbon-body">
            <div class="carbon-title">${title}</div>
            <div class="divider"></div>

            <div class="meta-row">
              <span>구매 가능 수량</span>
              <span class="value">${formatNumber(amount)} tCO2e</span>
            </div>

            <div class="meta-row" style="margin-top:8px;">
              <span>인증</span>
              <span class="value">${cert}</span>
            </div>

            <div class="price-area">
              <div class="price">${formatNumber(price)} P</div>
            </div>
          </div>

          <div class="carbon-action">
            <button class="btn-action ${btnCls}" type="button"
              onclick="location.href='${base}/carbon/detail?cpId=${encodeURIComponent(item.cpId ?? "")}'">
              상세 보기 및 주문
            </button>
          </div>
        </article>
      `;
    }).join("");
  }

  // ===== API =====
  async function apiGetJson(url) {
    const res = await fetch(url, { headers: { "Accept": "application/json" } });
    if (!res.ok) throw new Error(`${res.status} ${res.statusText}`);
    const data = await res.json();
    if (!Array.isArray(data)) return [];
    return data;
  }

  async function loadAll() {
    const url = `${base}/api/carbon`;
    const data = await apiGetJson(url);
    allItemsCache = data;   // 검색용 캐시
    viewItems = data;       // 화면 표시 데이터
    render(viewItems);
  }

  async function loadByCategory(category) {
    const upper = String(category || "ALL").toUpperCase();
    if (upper === "ALL") {
      // 캐시 있으면 캐시 쓰고, 없으면 서버 호출
      if (Array.isArray(allItemsCache)) {
        viewItems = allItemsCache;
        render(viewItems);
        return;
      }
      await loadAll();
      return;
    }

    const url = `${base}/api/carbon/category?category=${encodeURIComponent(upper)}`;
    const data = await apiGetJson(url);

    // 카테고리 호출은 전체캐시를 덮지 않음(검색 시 전체로 돌아올 수 있게)
    viewItems = data;
    render(viewItems);
  }

  // ===== 전역 핸들러(태그의 onClick에서 호출) =====
  window.filterCategory = async function (category, btn) {
    try {
      setActive(btn);
      container.innerHTML = `<div style="padding:20px; color:#777;">불러오는 중...</div>`;
      await loadByCategory(category);
    } catch (e) {
      container.innerHTML = `<div style="padding:20px; color:#d00;">데이터를 불러오지 못했습니다.</div>`;
      console.error(e);
    }
  };

  // (옵션) 검색: 서버 호출 없이 "현재 전체 캐시"에서만 필터
  // - 검색 UI가 없으면 이 함수는 그냥 안 쓰면 됨
  window.searchCarbon = function (keyword) {
    const q = String(keyword || "").trim().toLowerCase();
    const baseList = Array.isArray(allItemsCache) ? allItemsCache : viewItems;

    if (!q) {
      render(viewItems);
      return;
    }

    const filtered = baseList.filter(it => {
      const a = String(it.cpDetail || "").toLowerCase();
      const b = String(it.productCertificate || "").toLowerCase();
      return a.includes(q) || b.includes(q);
    });

    render(filtered);
  };

  // ===== 초기 로딩 =====
  document.addEventListener("DOMContentLoaded", async () => {
    try {
      // 첫 버튼(전체) 강제 active
      const firstBtn = document.querySelector(".filter-group .menu-btn");
      if (firstBtn) firstBtn.classList.add("active");

      container.innerHTML = `<div style="padding:20px; color:#777;">불러오는 중...</div>`;
      await loadAll();
    } catch (e) {
      container.innerHTML = `<div style="padding:20px; color:#d00;">탄소 상품 데이터를 불러오지 못했습니다.</div>`;
      console.error(e);
    }
  });
})();
