const ModalManager = {
    open: function(config) {
        const { type = 'Alert', title = '', content = '', titleOnly = false, buttonCount = 'one', onConfirm } = config;
        
        const modal = document.getElementById('globalModal');
        const titleEl = document.getElementById('modalTitle');
        const contentEl = document.getElementById('modalContent');
        const footer = document.getElementById('modalFooter');
        const cancelBtn = document.getElementById('modalCancelBtn');
        const confirmBtn = document.getElementById('modalConfirmBtn');

        // 제목 및 내용 세팅
        titleEl.innerText = title;
        contentEl.innerText = content;
        contentEl.style.display = titleOnly ? 'none' : 'block';

        // 버튼 개수 제어
        cancelBtn.style.display = (buttonCount === 'two' || type === 'Confirm') ? 'block' : 'none';
        
        // 확인 버튼 이벤트 바인딩
        confirmBtn.onclick = () => {
            if (onConfirm) onConfirm();
            this.close();
        };

        modal.style.display = 'flex';
    },
    close: function() {
        document.getElementById('globalModal').style.display = 'none';
    },
    // 단축 함수들
    alert: function(title, content, callback) {
        this.open({ title, content, type: 'Alert', buttonCount: 'one', onConfirm: callback });
    },
    confirm: function(title, content, callback) {
        this.open({ title, content, type: 'Confirm', buttonCount: 'two', onConfirm: callback });
    }
};

const ToastManager = {
    show: function(message) {
        const container = document.getElementById('toastContainer');
        const toast = document.createElement('div');
        toast.className = 'toast-item';
        toast.innerText = message;

        container.appendChild(toast);

        // 3초 후 삭제
        setTimeout(() => {
            toast.remove();
        }, 3000);
    }
};