const ModalManager = {
    open: function (config) {
        const {
            type = "Success",
            title = "",
            content = "",
            titleOnly = false,
            buttonCount = "one",
            onConfirm,
            confirmUrl,
        } = config;

        const modal = document.getElementById("globalModal");
        const titleEl = document.getElementById("modalTitle");
        const contentEl = document.getElementById("modalContent");
        const footer = document.getElementById("modalFooter");
        const cancelBtn = document.getElementById("modalCancelBtn");
        const confirmBtn = document.getElementById("modalConfirmBtn");

        // 아이콘 세팅
        document.getElementById("modalIconSuccess").style.display =
            type === "Success" ? "flex" : "none";
        document.getElementById("modalIconWarning").style.display =
            type === "Warning" ? "flex" : "none";

        // 제목 및 내용 세팅
        titleEl.innerText = title;
        contentEl.innerText = content;
        // contentEl.style.display = titleOnly ? "none" : "block";

        // 버튼 개수 제어
        if (buttonCount === "one") {
            footer.classList.add("one-btn");
            cancelBtn.style.display = "none";
            confirmBtn.style.order = 1; // 단독 버튼일 때 위치 고정
        } else {
            footer.classList.remove("one-btn");
            cancelBtn.style.display = "block";
            confirmBtn.style.order = 0; // 이미지처럼 '확인'이 왼쪽
        }

        // 확인 버튼 이벤트 바인딩
        confirmBtn.onclick = () => {
            if (onConfirm) onConfirm(); // 함수가 있으면 실행
            if (confirmUrl) location.href = confirmUrl; // URL이 있으면 이동
            this.close();
        };

        modal.style.display = "flex";
    },
    close: function () {
        document.getElementById("globalModal").style.display = "none";
        confirmBtn.onclick = null;
        document.getElementById("modalContent").innerText = "";
    },
    // 단축 함수들
    alert: function (title, content, callback) {
        this.open({
            title,
            content,
            type: "Warning",
            buttonCount: "one",
            onConfirm: callback,
        });
    },
    confirm: function (title, content, callback) {
        this.open({
            title,
            content,
            type: "Success",
            buttonCount: "two",
            onConfirm: callback,
        });
    },
};

const ToastManager = {
    show: function (message) {
        const container = document.getElementById("toastContainer");
        const toast = document.createElement("div");
        toast.className = "toast-item";
        toast.innerText = message;

        container.appendChild(toast);

        // 3초 후 삭제
        setTimeout(() => {
            toast.remove();
        }, 3000);
    },
};

// 사용 예시

// 1. 성공 알림
// ModalManager.open({
//     type: "Success",
//     title: "저장 완료",
//     content: "변경사항이 성공적으로 반영되었습니다.",
//     buttonCount: "one"
// });

// 2. 경고 알림
// 단축 함수 사용 (Warning 아이콘이 기본 적용됨)
// ModalManager.alert("로그인 실패", "아이디 또는 비밀번호를 확인해주세요.");

// 3. 확인/취소 모달
// ModalManager.confirm("청약 취소", "정말 청약을 취소하시겠습니까?", function () {
//     console.log("취소 로직 실행");
// });

// 4. 위험 동작 확인 모달
// ModalManager.open({
//     type: "Warning",
//     title: "데이터 삭제",
//     content: "삭제된 데이터는 복구할 수 없습니다. 계속하시겠습니까?",
//     buttonCount: "two",
//     onConfirm: function() {
//         deleteData();
//     }
// });

// 5. 페이지 전환 후 모달
// A 페이지 (등록 로직)
// PendingManager.setPending({
//     type: "Success",
//     title: "등록 성공",
//     content: "게시글이 목록에 추가되었습니다.",
//     buttonCount: "one"
// }, "MODAL");
// location.href = "/project/list";

// 6. 특수 레이아웃
// ModalManager.open({
//     type: "Success",
//     title: "마이페이지로 이동하시겠습니까?",
//     titleOnly: true,
//     buttonCount: "two",
//     confirmUrl: "/mypage"
// });
