<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/mypage.css">

<div class="mypage-container">
	<div class="sidebar-wrapper">
		<jsp:include page="/WEB-INF/views/common/mypage_sidebar.jsp" />
	</div>

	<div class="content-wrapper">
		<t:section_header title="내 정보" subtitle="마리팜에서 사용되는 회원님의 정보를 관리합니다." />

		<div class="info-card">
			<div class="profile-summary">
				<span class="user-name" id="p_userName">-</span> <span
					class="badge-announcement" id="p_investorType">-</span>
			</div>
			<p class="join-date">
				가입일: <span id="p_createdAt">-</span>
			</p>
		</div>

		<div class="info-card">
			<h2 class="card-title">기본 정보</h2>

			<div class="input-group-column">
				<div class="input-group">
					<label class="input-label">이메일</label>
					<div class="input-value" id="p_email">-</div>
				</div>

				<div class="input-group">
					<label class="input-label">휴대폰 번호</label>
					<div class="input-value" id="p_phoneNumber">-</div>
				</div>

				<div class="input-group">
					<label class="input-label">주소</label>
					<div class="input-value" id="p_address">-</div>
					<button class="btn-edit" type="button" id="btnEditAddress">
						<t:status_badge label="수정" status="inProgress" />
					</button>
				</div>

				<div class="input-group">
					<label class="input-label">비밀번호</label>
					<div class="input-value">••••••••</div>
					<button class="btn-edit" type="button" id="btnEditPassword">
						<t:status_badge label="수정" status="inProgress" />
					</button>
				</div>
			</div>
		</div>

		<div class="info-card">
			<h2 class="card-title">알림 및 수신 설정</h2>

			<div class="input-group-column">
				<div class="noti-setting-row" id="row-push">
					<div class="setting-info">
						<p class="s-title">푸시 알림 동의</p>
						<p class="s-desc">투자 상품 오픈, 이벤트 및 서비스 혜택 알림을 실시간으로 받습니다.</p>
					</div>
					<t:toggle_button onchange="" checked="" />
				</div>

				<div class="noti-setting-row" id="row-email">
					<div class="setting-info">
						<p class="s-title">이메일 수신 동의</p>
						<p class="s-desc">자산 리포트 및 주요 뉴스레터를 이메일로 받아보실 수 있습니다.</p>
					</div>
					<t:toggle_button onchange="" checked="" />
				</div>
			</div>
		</div>

		<div class="delete-account">
			<div class="delete-info">
				<p class="d-title">계정 삭제가 필요하신가요?</p>
				<p class="d-desc">회원 탈퇴 시 모든 투자 기록 및 자산 정보가 삭제되며 복구할 수 없습니다.</p>
			</div>
			<button class="btn-withdraw" type="button">회원 탈퇴</button>
		</div>
	</div>
</div>
<div id="addressModal" class="modal-backdrop" style="display: none;">
	<div class="modal-card">
		<div class="modal-header">
			<h3>주소 수정</h3>
			<button type="button" id="addrClose">×</button>
		</div>

		<div class="modal-body">
			<input id="addrInput" type="text" class="modal-input"
				placeholder="주소를 입력해주세요" />
		</div>

		<div class="modal-footer">
			<button type="button" class="btn-cancel" id="addrCancel">취소</button>
			<button type="button" class="btn-save" id="addrSave">저장</button>
		</div>
	</div>
</div>
<!-- 비밀번호 수정 모달 -->
<div id="pwModal" class="modal hidden" aria-hidden="true">
  <div class="modal-content">
    <div class="modal-header">
      <h3>비밀번호 변경</h3>
      <button type="button" class="modal-close" id="pwModalCloseBtn">×</button>
    </div>

    <form id="pwForm" class="modal-body">
      <div class="form-row">
        <label for="currentPw">현재 비밀번호</label>
        <input id="currentPw" type="password" autocomplete="current-password" required />
      </div>

      <div class="form-row">
        <label for="newPw">새 비밀번호</label>
        <input id="newPw" type="password" autocomplete="new-password" required />
      </div>

      <div class="form-row">
        <label for="newPw2">새 비밀번호 확인</label>
        <input id="newPw2" type="password" autocomplete="new-password" required />
      </div>

      <p id="pwError" class="form-error hidden"></p>

      <div class="modal-footer">
        <button type="button" class="btn ghost" id="pwCancelBtn">취소</button>
        <button type="submit" class="btn primary">변경</button>
      </div>
    </form>
  </div>
</div>
<script>
document.addEventListener("DOMContentLoaded", function () {

  const token = localStorage.getItem("accessToken");

  fetch(ctx + "/api/mypage/profile", {
    method: "GET",
    headers: {
      "Accept": "application/json",
      "Authorization": "Bearer " + token
    }
  })
  .then(res => {
    if (!res.ok) throw new Error("데이터 로드 실패 (상태: " + res.status + ")");
    return res.json();
  })
  .then(data => {
    if (!data) return;
    // 값 박기
    document.getElementById("p_userName").innerText = data.userName ?? "-";
    document.getElementById("p_email").innerText = data.email ?? "-";
    document.getElementById("p_phoneNumber").innerText = data.phoneNumber ?? "-";
    document.getElementById("p_address").innerText = data.address ?? "-";

    // investorType 라벨링(원하면 여기만 바꾸면 됨)
    let type = data.investorType ?? "-";
    if (type === "PRO") type = "Professional Investor";
    else if (type === "ELIGIBLE") type = "Eligible Investor";
    else if (type === "GENERAL") type = "General Investor";
    document.getElementById("p_investorType").innerText = type;

    const elCreated = document.getElementById("p_createdAt");
    const v = data.createdAt;

    if (elCreated) {
      const s = (v === null || v === undefined) ? "" : String(v);
      elCreated.textContent = (s.length >= 10) ? s.slice(0, 10) : "-";
    }

    // toggle_button 내부 input checkbox 찾아서 체크 (carbon처럼 DOM에서 찾아서 처리)
    const pushRow = document.getElementById("row-push");
    const pushInput = pushRow ? pushRow.querySelector('input[type="checkbox"]') : null;
    if (pushInput) pushInput.checked = !!data.pushYn;

    const emailRow = document.getElementById("row-email");
    const emailInput = emailRow ? emailRow.querySelector('input[type="checkbox"]') : null;
    if (emailInput) emailInput.checked = !!data.receiveEmailYn;
  })
  .catch(err => {
    console.error("Fetch Error:", err);
  });

});

document.addEventListener("DOMContentLoaded", () => {
  const token = localStorage.getItem("accessToken");

  // PATCH 공통
  async function patchProfile(payload) {
    const res = await fetch(ctx + "/api/mypage/profile", {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        "Accept": "application/json",
        "Authorization": token ? ("Bearer " + token) : ""
      },
      body: JSON.stringify(payload)
    });

    if (!res.ok) throw new Error("저장 실패: " + res.status);
  }

  // ===== 주소 모달 =====
  const modal = document.getElementById("addressModal");
  const addrInput = document.getElementById("addrInput");
  const btnEditAddress = document.getElementById("btnEditAddress");
  const btnClose = document.getElementById("addrClose");
  const btnCancel = document.getElementById("addrCancel");
  const btnSave = document.getElementById("addrSave");

  function openAddrModal() {
    const cur = document.getElementById("p_address")?.innerText?.trim() ?? "";
    addrInput.value = (cur === "-" ? "" : cur);
    modal.style.display = "block";
    setTimeout(() => addrInput.focus(), 0);
  }

  function closeAddrModal() {
    modal.style.display = "none";
  }

  if (btnEditAddress) btnEditAddress.onclick = openAddrModal;
  if (btnClose) btnClose.onclick = closeAddrModal;
  if (btnCancel) btnCancel.onclick = closeAddrModal;

  if (modal) {
    modal.onclick = (e) => { if (e.target === modal) closeAddrModal(); };
  }

  if (btnSave) {
    btnSave.onclick = async () => {
      try {
        const v = addrInput.value.trim();
        // 주소만 변경 → 나머지 필드는 null로 안 보내면 됨
        await patchProfile({ address: v || null });

        // 화면 즉시 반영
        const el = document.getElementById("p_address");
        if (el) el.innerText = v || "-";

        closeAddrModal();
      } catch (e) {
        console.error(e);
        alert("주소 저장 실패");
      }
    };
  }

  // ===== 토글 즉시 저장 =====
  function bindToggle(rowId, fieldName) {
    const row = document.getElementById(rowId);
    if (!row) return;

    const input = row.querySelector('input[type="checkbox"]');
    if (!input) return;

    input.onchange = async () => {
      const next = input.checked;
      try {
        await patchProfile({ [fieldName]: next });
      } catch (e) {
        console.error(e);
        alert("설정 저장 실패");
        input.checked = !next; // 롤백
      }
    };
  }

  bindToggle("row-push", "pushYn");
  bindToggle("row-email", "receiveEmailYn");
});

document.addEventListener("DOMContentLoaded", () => {
  const btnEditPassword = document.getElementById("btnEditPassword");
  const pwModal = document.getElementById("pwModal");
  const pwModalCloseBtn = document.getElementById("pwModalCloseBtn");
  const pwCancelBtn = document.getElementById("pwCancelBtn");
  const pwForm = document.getElementById("pwForm");
  const pwError = document.getElementById("pwError");

  function openPwModal() {
    pwError.classList.add("hidden");
    pwError.textContent = "";

    pwForm.reset();
    pwModal.classList.remove("hidden");
    pwModal.setAttribute("aria-hidden", "false");
  }

  function closePwModal() {
    pwModal.classList.add("hidden");
    pwModal.setAttribute("aria-hidden", "true");
  }

  function showPwError(msg) {
    pwError.textContent = msg;
    pwError.classList.remove("hidden");
  }

  btnEditPassword?.addEventListener("click", openPwModal);
  pwModalCloseBtn?.addEventListener("click", closePwModal);
  pwCancelBtn?.addEventListener("click", closePwModal);

  // 바깥 클릭 닫기(주소 모달이랑 동일하게)
  pwModal?.addEventListener("click", (e) => {
    if (e.target === pwModal) closePwModal();
  });

  pwForm?.addEventListener("submit", async (e) => {
    e.preventDefault();

    const currentPw = document.getElementById("currentPw").value.trim();
    const newPw = document.getElementById("newPw").value.trim();
    const newPw2 = document.getElementById("newPw2").value.trim();

    // 간단 검증
    if (!currentPw || !newPw || !newPw2) return showPwError("모든 값을 입력해주세요.");
    if (newPw !== newPw2) return showPwError("새 비밀번호 확인이 일치하지 않습니다.");
    if (newPw.length < 8) return showPwError("새 비밀번호는 8자 이상으로 해주세요.");
    if (currentPw === newPw) return showPwError("현재 비밀번호와 다른 비밀번호로 설정해주세요.");

    try {
      const token = localStorage.getItem("accessToken");
      const res = await fetch(ctx + "/api/mypage/password", {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          "Authorization": token ? "Bearer " + token : ""
        },
        body: JSON.stringify({
          currentPassword: currentPw,
          newPassword: newPw
        })
      });

      const data = await res.json().catch(() => ({}));

      if (!res.ok) {
        // 서버가 메시지 내려주면 그거 쓰고, 없으면 기본
        return showPwError(data?.message || "비밀번호 변경 실패. 현재 비밀번호를 확인해주세요.");
      }

      alert("비밀번호가 변경됐습니다.");
      closePwModal();
    } catch (err) {
      console.error(err);
      showPwError("네트워크 오류. 잠시 후 다시 시도해주세요.");
    }
  });
});
</script>
