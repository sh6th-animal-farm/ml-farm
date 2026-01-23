(function () {

  function syncActiveMenu() {
    const params = new URLSearchParams(window.location.search);
    const category = (params.get("category") || "ALL").toUpperCase();

    document.querySelectorAll(".filter-group .menu-btn")
      .forEach(btn => {
        const label = btn.textContent.trim();

        if (
          (category === "ALL" && label === "전체") ||
          (category === "REDUCTION" && label === "감축형") ||
          (category === "REMOVAL" && label === "제거형")
        ) {
          btn.classList.add("active");
        } else {
          btn.classList.remove("active");
        }
      });
  }

  window.filterCarbonCard = function (keyword) {
    const q = String(keyword || "").toLowerCase();

    document.querySelectorAll(".carbon-card").forEach(card => {
      const text = card.textContent.toLowerCase();
      card.style.display = text.includes(q) ? "" : "none";
    });
  };

  document.addEventListener("DOMContentLoaded", function () {
    syncActiveMenu();
  });

})();
