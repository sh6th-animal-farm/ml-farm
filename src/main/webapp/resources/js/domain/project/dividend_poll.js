import { ProjectApi } from "./project_api.js";

window.selectType = function (type, element) {
  // 모든 카드에서 active 제거
  document
    .querySelectorAll(".card-choice")
    .forEach((card) => card.classList.remove("active"));
  // 클릭한 카드에 active 추가
  element.classList.add("active");
  // hidden input 업데이트
  document.getElementById("selectedType").value = type;
};

document.getElementById("pollForm").onsubmit = async function (e) {
  e.preventDefault();
  const selectedType = document.getElementById("selectedType").value;
  const typeName = selectedType === "CASH" ? "현금" : "작물";

  if (!confirm(typeName + " 수령으로 확정하시겠습니까?")) return;

  const requestData = {
    dividendId: this.dividendId.value,
    dividendType: selectedType,
  };

  try {
    let responseText = await ProjectApi.selectDividendPoll(requestData);

    if (responseText) {
      alert(responseText);
      location.href = ctx + "/mypage"; // 성공 시 마이페이지로 이동
    } else {
      alert("배당 수령 방식 선택에 실패했습니다. 다시 시도해주세요.");
    }
  } catch (error) {
    console.error("배당 수령 방식 선택 실패: ", error);
  }
};
