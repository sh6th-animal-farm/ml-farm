// ========= 프로젝트 등록 ==========

function openModal() { document.getElementById('projectModal').style.display = 'block'; }
function closeModal() { document.getElementById('projectModal').style.display = 'none'; }

// 선택한 프로젝트 정보를 폼에 자동 입력 (차수는 +1)
function selectProject(data) {
    document.getElementById('farm_id').value = data.farm;
    document.getElementById('project_name').value = data.name;
    document.getElementById('project_round').value = data.round + 1; // 차수 자동 증가
    document.getElementById('target_amount').value = data.target;
    document.getElementById('min_amount').value = data.min;
    document.getElementById('max_amount').value = data.max;
    document.getElementById('expected_return').value = data.roi;
    document.getElementById('manager_count').value = data.mgr;
    
    alert(data.name + ' 정보가 불러와졌습니다. 차수가 ' + (data.round + 1) + '로 업데이트되었습니다.');
    closeModal();
}