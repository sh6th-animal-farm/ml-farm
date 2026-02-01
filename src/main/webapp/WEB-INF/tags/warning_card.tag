<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<%@ attribute name="id" required="true" type="java.lang.String" %>
<%@ attribute name="title" required="false" type="java.lang.String" %>

<div id="${id}" class="modal-overlay" onclick="if(event.target === this) closeModal('${id}')">
    <div class="modal-content">
        <div class="modal-icon">
            <div class="icon-circle">
                <t:icon name="warning" size="30" color="var(--error)" />
            </div>
        </div>

        <c:if test="${not empty title}">
            <h2 class="modal-title">${title}</h2>
        </c:if>

        <div class="modal-description">
            <jsp:doBody />
        </div>

        <div class="modal-buttons">
            <button type="button" class="btn-primary" onclick="location.href='/account/connect'">증권 계좌 연동하기</button>
            <button type="button" class="btn-secondary" onclick="closeModal('${id}')">다시 시도</button>
        </div>
    </div>
</div>

<style>
.modal-overlay {
    position: fixed; top: 0; left: 0; width: 100%; height: 100%;
    background: rgba(0, 0, 0, 0.4); 
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
    display: none; align-items: center; justify-content: center; z-index: 9999;
}

.modal-content {
    background: #ffffff;
    width: 90%; max-width: 360px;
    padding: 36px 24px 24px 24px; border-radius: 12px;
    text-align: center;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
    animation: modal-fade-in 0.3s ease-out;
}

.icon-circle {
    width: 60px; height: 60px; border-radius: 50%;
    display: inline-flex; align-items: center; justify-content: center;
    background: var(--error-light); 
    margin: 0 auto 24px;
}

.modal-title { color: var(--gray-900); margin-bottom: 28px; font: var(--font-subtitle-01); }
.modal-description { color: var(--gray-900); margin-bottom: 28px; font: var(--font-caption-01); line-height: 1.6; }
.modal-description strong { font: var(--font-caption-02); color: var(--gray-900); }

.modal-buttons { display: flex; flex-direction: column; gap: 12px; }
.modal-buttons button {
    padding: 16px; border-radius: 8px; font: var(--font-button-02);
    cursor: pointer; transition: none; border: none;
}

.btn-primary { background: var(--gray-900); color: #ffffff; }
.btn-primary:hover, .btn-primary:active { background: var(--gray-900) !important; color: #ffffff !important; }

.btn-secondary { background: #ffffff; color: var(--green-600); border: 1px solid var(--green-600) !important; }
.btn-secondary:hover, .btn-secondary:active { background: #ffffff !important; color: var(--green-600) !important; border-color: var(--green-600) !important; }

@keyframes modal-fade-in {
    from { opacity: 0; transform: translateY(10px); }
    to { opacity: 1; transform: translateY(0); }
}
</style>

<script>
    function closeModal(id) {
        var modal = document.getElementById(id);
        if (modal) {
            modal.style.display = 'none';
            document.body.style.overflow = 'auto';
        }
    }
</script>