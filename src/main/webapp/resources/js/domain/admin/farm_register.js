import { AdminApi } from "../../api/admin_api.js";
import { FormUtil } from "../../util/form_util.js";

document.addEventListener('DOMContentLoaded', () => {
    const farmForm = document.querySelector('form');
    if (farmForm) {
        farmForm.addEventListener('submit', (e)=>insertFarm(e))
    }

});

function openModal() {
    console.log("농장 정보 불러오기 모달 실행");
}

function getFormData() {
    const form = document.querySelector("form");
    const formData = new FormData(form);
    return FormUtil.adminFarmFormToJSON(formData);
}

function insertFarm(e) {
    e.preventDefault();
    const data = getFormData();
    AdminApi.insertFarm(data)
    .then((text) => {
        // 백엔드에서 ResponseEntity.ok("success")를 보낸 경우
        if (text === "success") {
            PendingManager.setPending(
                "농장 등록이 완료되었습니다!",
            );
            window.location.href = `${ctx}/admin/farm/new`;
        } else {
            // 백엔드에서 throw new RuntimeException으로 던진 메시지가 text에 담겨옴
            // (예: "프로젝트 등록 실패: 증권사 서비스 오류...")
            ToastManager.show(
                "등록 실패: " + text + "\n(데이터는 저장되지 않았습니다.)",
            );
        }
    })
    .catch((error) => {
        // 네트워크 오류나 정말 예상치 못한 서버 에러 발생 시
        console.error(error);
        ToastManager.show(error.message);
    });
}