function startGlobalTimer() {
  const updateTimers = () => {
    const now = new Date();

    document.querySelectorAll(".timer-display").forEach((display) => {
      const endTimeStr = display.getAttribute("data-end-time");
      const status = display.getAttribute("data-status");
      if (!endTimeStr) return;

      const endTime = new Date(endTimeStr);
      const diff = endTime - now;

      if (diff <= 0) {
        display.textContent = "청약 마감";
        display.classList.replace("text-error", "text-gray-400");
        return;
      }

      const totalSeconds = Math.floor(diff / 1000);
      // 시간 계산 로직
      const hours = String(Math.floor(totalSeconds / 3600)).padStart(2, "0");
      const mins = String(Math.floor((totalSeconds % 3600) / 60)).padStart(
        2,
        "0",
      );
      const secs = String(Math.floor(totalSeconds % 60)).padStart(2, "0");

      if (status === "SUBSCRIPTION") {
        display.textContent = `마감까지 ${hours}:${mins}:${secs}`;
      } else if (status === "ANNOUNCEMENT") {
        display.textContent = `시작까지 ${hours}:${mins}:${secs}`;
      }
    });
  };

  // 초기 실행 및 1초 주기 반복
  updateTimers();
  setInterval(updateTimers, 1000);
}

// DOM이 로드된 후 실행
document.addEventListener("DOMContentLoaded", startGlobalTimer);
