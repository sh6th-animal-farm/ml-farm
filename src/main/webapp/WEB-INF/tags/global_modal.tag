<%@ tag language="java" pageEncoding="UTF-8" body-content="scriptless"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<div id="globalModal" class="mlf-modal-overlay">
    <div class="mlf-modal-container">
        <div class="mlf-modal-icon-wrap" id="modalIconWrap">
            <div class="global-modal-icon success" id="modalIconSuccess">
                <t:icon name="check" color="var(--green-600)"/>
            </div>
            <div class="global-modal-icon danger" id="modalIconWarning" style="display:none;">
                <t:icon name="warning" color="var(--error)"/>
            </div>
        </div>

        <div class="mlf-modal-content">
            <h4 class="mlf-modal-title" id="modalTitle">제목</h4>
            <p class="mlf-modal-body-text" id="modalContent">내용</p>
        </div>

        <div class="mlf-modal-footer-grid" id="modalFooter">
            <button id="modalConfirmBtn" class="btn-confirm">확인</button>
            <button id="modalCancelBtn" class="btn-cancel" onclick="ModalManager.close()">취소</button>
        </div>
    </div>
</div>

<style>
.mlf-modal-overlay {
    display: none;
    position: fixed;
    top: 0; left: 0;
    width: 100%; height: 100%;
    background: rgba(0, 0, 0, 0.4);
    z-index: 9999;
    backdrop-filter: blur(8px);
    align-items: center;
    justify-content: center;
}

.mlf-modal-container {
    background: #ffffff;
    width: 90%;
    max-width: 360px; /* 이미지의 컴팩트한 사이즈 반영 */
    border-radius: var(--radius-l); /* 더 둥근 라운드값 */
    padding: 32px 24px 24px;
    box-shadow: var(--shadow);
    text-align: center; /* 모든 요소 중앙 정렬 */
    animation: modalPop 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

/* 아이콘 스타일 */
.mlf-modal-icon-wrap {
    display: flex;
    justify-content: center;
    margin-bottom: 20px;
}

.global-modal-icon {
    width: 56px; height: 56px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
}

.global-modal-icon svg { width: 28px; height: 28px; }

/* 이미지의 컬러칩 반영 */
.global-modal-icon.success { background-color: var(--green-50); }
.global-modal-icon.danger { background-color: var(--error-light); }

/* 텍스트 스타일 */
.mlf-modal-title {
    font-size: 20px;
    font-weight: 700;
    color: #111;
    margin: 0 0 12px 0;
    line-height: 1.4;
}

.mlf-modal-body-text {
    font-size: 15px;
    color: #666;
    margin: 0 0 28px 0;
    line-height: 1.6;
    white-space: pre-wrap;
}

/* 하단 버튼 그리드 (가로 배치) */
.mlf-modal-footer-grid {
    display: grid;
    grid-template-columns: 1fr 1fr; /* 2개일 때 절반씩 */
    gap: 12px;
}

/* 버튼 하나일 때 처리용 클래스 */
.mlf-modal-footer-grid.one-btn {
    grid-template-columns: 1fr;
}

.btn-confirm {
    background-color: var(--green-600); /* 이미지의 메인 그린 */
    color: white;
    border: none;
    border-radius: var(--radius-s);
    height: 52px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: 0.2s;
}

.btn-cancel {
    background-color: white;
    color: #4CAF50;
    border: 1.5px solid var(--green-600);
    border-radius: 12px;
    height: 52px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
}

.btn-confirm:active { transform: scale(0.98); opacity: 0.9; }

@keyframes modalPop {
    from { opacity: 0; transform: scale(0.9) translateY(20px); }
    to { opacity: 1; transform: scale(1) translateY(0); }
}
</style>