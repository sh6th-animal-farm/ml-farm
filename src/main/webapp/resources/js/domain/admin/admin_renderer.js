const AdminRenderer = {
    /**
     * 파일 탐색기에서 선택한 프로젝트 사진을 사진 목록에 랜더링
     * @param {string} src - 파일 경로
     * @param {string} savedFileName - 저장된 파일 이름
     * @param {Function} onRemove - 제거할때 수행할 콜백 함수
     */
    createImageBox(src, savedFileName, onRemove) {
        const box = document.createElement('div');
        box.className = 'image-upload-box';
        box.innerHTML = `
            <img src="${src}" alt="preview">
            <div class="btn-remove">&times;</div>
            <input type="hidden" name="project_image_names" value="${savedFileName}">
        `;
        box.querySelector('.btn-remove').onclick = onRemove;
        return box;
    },
    /**
     * 프로젝트 리스트를 모달 내 컨테이너에 렌더링
     * @param {Array} projects - 프로젝트 데이터 배열
     * @param {Function} onSelect - 리스트 아이템 클릭 시 실행할 콜백 함수
     */
    renderProjectList(projects, onSelect) {
        const listContainer = document.getElementById('projectListContainer');
        
        if (!listContainer) return;

        // 초기화
        listContainer.innerHTML = '';

        if (projects.length === 0) {
            listContainer.innerHTML = '<li class="project-item">등록된 프로젝트가 없습니다.</li>';
            return;
        }

        projects.forEach(data => {
            const li = document.createElement('li');
            li.className = 'project-item';
            
            li.innerHTML = `
                <strong>${data.projectName} (${data.projectRound}차)</strong><br>
                <small>ID: ${data.projectId} | 목표액: ${data.targetAmount.toLocaleString()}원 | 예상수익: ${data.expectedReturn}%</small>
            `;
            
            // 핵심: 특정 함수 이름을 직접 호출하지 않고 인자로 받은 콜백을 실행
            li.onclick = () => {
                if (typeof onSelect === 'function') {
                    onSelect(data);
                }
            };
            
            listContainer.appendChild(li);
        });
    },
    /**
     * 기존에 프로젝트에 등록된 이미지들을 사진 목록에 렌더링
     * @param {Array} picturesData - 이미지 정보 배열
     * @param {HTMLElement} previewList - 이미지가 들어갈 컨테이너
     * @param {HTMLElement} addBtn - '추가' 버튼 (이 버튼 앞에 삽입)
     * @param {Function} onMarkDeleted - 삭제 버튼 클릭 시 실행할 콜백 (ID 전달용)
     */
    displayExistingImages(picturesData, previewList, addBtn, onMarkDeleted) {
        // 기존의 박스들 제거 (추가 버튼 제외)
        const existingBoxes = previewList.querySelectorAll('.image-upload-box:not(.add-btn)');
        existingBoxes.forEach(box => box.remove());

        picturesData.forEach(pic => {
            const box = document.createElement('div');
            box.className = 'image-upload-box existing';
            
            // 이미지 경로는 프로젝트별로 다를 수 있으니 상황에 맞게 처리
            box.innerHTML = `
                <img src="${ctx}/uploads/projects/${pic.imageUrl}" alt="project image">
                <div class="btn-remove" data-id="${pic.projectPictureId}">&times;</div>
            `;

            box.querySelector('.btn-remove').onclick = (e) => {
                e.stopPropagation();
                if (confirm("이 사진을 삭제하시겠습니까?")) {
                    // 메인 로직의 markImageAsDeleted 함수를 호출하여 ID를 리스트에 담게 함
                    if (typeof onMarkDeleted === 'function') {
                        onMarkDeleted(pic.projectPictureId);
                    }
                    box.remove();
                }
            };

            previewList.insertBefore(box, addBtn);
        });
    }
};

export {AdminRenderer}