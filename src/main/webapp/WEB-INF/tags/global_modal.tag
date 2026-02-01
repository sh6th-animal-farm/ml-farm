<%@ tag language="java" pageEncoding="UTF-8" body-content="scriptless"%>

<div id="globalModal" class="mlf-modal-overlay">
	<div class="mlf-modal-container">
		
		<div class="mlf-modal-header" id="modalHeader">
			<h4 class="mlf-modal-title" id="modalTitle"></h4>
			<button type="button" class="mlf-modal-close"
				onclick="ModalManager.close()">&times;</button>
		</div>
		<div class="mlf-modal-body">
			<p id="modalContent"></p>
		</div>
		<div class="mlf-modal-footer" id="modalFooter">
			<button id="modalCancelBtn" class="btn-sub"
				onclick="ModalManager.close()">취소</button>
			<button id="modalConfirmBtn" class="btn-main">확인</button>
		</div>
	</div>
</div>

<style>
.mlf-modal-overlay {
	display: none;
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: rgba(0, 0, 0, 0.5);
	z-index: 9999;
	backdrop-filter: blur(4px);
	align-items: center;
	justify-content: center;
}

.mlf-modal-container {
	background: var(--white);
	width: 90%;
	max-width: 400px;
	border-radius: var(--radius-l);
	box-shadow: var(--shadow);
	overflow: hidden;
	animation: modalUp 0.3s ease-out;
}

.mlf-modal-header {
	padding: 20px 24px;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.mlf-modal-body {
	padding: 0 24px 24px 24px;
	font: var(--font-body-01);
	color: var(--gray-600);
}

.mlf-modal-footer {
	padding: 16px 24px;
	display: flex;
	gap: 8px;
	justify-content: flex-end;
	background: var(--gray-50);
}

.mlf-modal-footer button {
	padding: 10px 20px;
	font: var(--font-button-02);
	border-radius: var(--radius-m);
	border: none;
	cursor: pointer;
}
</style>