import { AuthApi } from "../../api/auth_api.js";
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

  if (selectedType === "CROP") {
    openAddressModal();
  } else {
    if (!confirm(typeName + " 수령으로 확정하시겠습니까?")) return;

    const dividendIdEl = document.querySelector('input[name="dividendId"]');
    const dividendId = dividendIdEl ? dividendIdEl.value : null;

    const requestData = {
      dividendId: dividendId,
      dividendType: selectedType,
    };

    sendSelection(requestData);
  }
};

// 주소 모달 열기
function openAddressModal() {
  document.getElementById("addressModal").style.display = "block";
}

// 주소 수정 토글
window.toggleAddressEdit = function () {
  new daum.Postcode({
    oncomplete: function (data) {
      // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

      // 도로명 주소의 노출 규칙에 따라 주소를 조합한다.
      // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가짐
      let addr =
        data.userSelectedType === "R" ? data.roadAddress : data.jibunAddress; // 주소 변수
      let extraAddr = ""; // 참고항목 변수

      if (data.bname !== "" && /[동|로|가]$/g.test(data.bname))
        extraAddr += data.bname;
      if (data.buildingName !== "" && data.apartment === "Y") {
        extraAddr +=
          extraAddr !== "" ? ", " + data.buildingName : data.buildingName;
      }
      if (extraAddr !== "") addr += " (" + extraAddr + ")";

      // 1. 기본 주소를 hidden 필드에 저장
      document.getElementById("baseAddress").value = addr;
      // 2. 입력창 영역 표시 및 상세주소 칸 비우기
      document.getElementById("addressInputArea").style.display = "block";
      document.getElementById("detailAddress").value = "";
      // 3. 화면 표시 업데이트
      document.getElementById("displayAddress").innerText = addr;
      document.getElementById("detailAddress").focus();

      // 반짝임 피드백 추가
      const addressBox = document.querySelector(".current-address-box");

      // 혹시 이미 클래스가 있다면 제거 후 다시 추가 (연속 클릭 대비)
      addressBox.classList.remove("address-flash");

      // 브라우저가 리플로우(Reflow)를 발생시켜 애니메이션을 다시 시작하게 함
      void addressBox.offsetWidth;

      addressBox.classList.add("address-flash");

      // 애니메이션 종료(1초) 후 클래스 제거 (깔끔한 상태 유지)
      setTimeout(() => {
        addressBox.classList.remove("address-flash");
      }, 1000);
    },
  }).open();
};

// 상세주소 입력 시 실시간으로 합쳐서 보여주는 함수
window.combineAddress = function () {
  const base = document.getElementById("baseAddress").value;
  const detail = document.getElementById("detailAddress").value;
  document.getElementById("displayAddress").innerText = base + " " + detail;
};

// 모달 내 확정 클릭 시 실행
window.confirmCropSelection = async function () {
  let finalAddress = document.getElementById("displayAddress").innerText;

  if (!finalAddress.trim() || finalAddress === "주소 등록이 필요합니다.") {
    ToastManager.show("새로운 주소를 입력해주세요.");
    return;
  }

  if (!confirm(`입력하신 주소(${finalAddress})로 배송을 확정하시겠습니까?`))
    return;
  const dividendIdEl = document.querySelector('input[name="dividendId"]');
  const dividendId = dividendIdEl ? dividendIdEl.value : null;

  const requestData = {
    dividendId: dividendId,
    dividendType: "CROP",
    address: finalAddress, // 주소 정보 포함
  };

  await sendSelection(requestData);
};

// 공통 전송 함수
async function sendSelection(data) {
  try {
    const response = await ProjectApi.selectDividendPoll(data);
    if (response) {
      PendingManager.setPending(response.message || "처리가 완료되었습니다.");
      location.href = ctx + "/mypage";
    }
  } catch (error) {
    console.error("통신 실패:", error);
  }
}

window.closeAddressModal = function () {
  document.getElementById("addressModal").style.display = "none";
};

document.addEventListener("DOMContentLoaded", async () => {
  let user = null;
  try {
    user = await AuthApi.getUser();
    if (!user) {
      location.href = ctx + "/auth/login";
    }
  } catch (e) {
    location.href = ctx + "/auth/login";
  }

  document.getElementById("displayAddress").innerText =
    user.address || "주소 등록이 필요합니다.";
});
