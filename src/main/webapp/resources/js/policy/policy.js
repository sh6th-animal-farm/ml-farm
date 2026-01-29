(function () {
  function init() {
    const menuItems = Array.from(document.querySelectorAll(".terms-item"));
    const sections  = Array.from(document.querySelectorAll(".content-section"));

    if (menuItems.length === 0 || sections.length === 0) return;

    function activate(id) {
      sections.forEach(s => s.classList.toggle("active", s.id === id));
      menuItems.forEach(li => li.classList.toggle("active", li.dataset.target === id));
    }

    // 클릭 이벤트
    menuItems.forEach(li => {
      li.addEventListener("click", () => activate(li.dataset.target));
    });

    // 초기 탭: URL(tab) > 서버(window.__POLICY_TAB__) > 기본값
    const params = new URLSearchParams(window.location.search);
    const fromUrl = params.get("tab");
    const fromServer = window.__POLICY_TAB__;

    let initial = "marifarm";
    if (fromUrl && document.getElementById(fromUrl)) initial = fromUrl;
    else if (fromServer && document.getElementById(fromServer)) initial = fromServer;

    activate(initial);
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
