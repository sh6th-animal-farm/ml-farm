import { DateUtil } from "../util/date_util.js";

let selectedProjectId = null;

// DOM이 다 로드된 후 이벤트 리스너 붙이기
document.addEventListener('DOMContentLoaded', () => {
    const btnLoad = document.querySelector('.btn-load');
    if (btnLoad) {
        btnLoad.addEventListener('click', openModal);
    }
    const btnRegister = document.querySelector('.register-btn');
    if (btnRegister) {
        btnRegister.addEventListener('click', insertProject);
    }
    const btnEdit = document.querySelector('.edit-btn');
    if (btnEdit) {
        btnEdit.addEventListener('click', updateProject);
    }
    const btnClose = document.querySelector('.modal-close');
    if (btnClose) {
        btnClose.addEventListener('click', closeModal);
    }

    const imageInput = document.getElementById('imageInput');
    const previewList = document.getElementById('imagePreviewList');
    const addBtn = previewList.querySelector('.add-btn');

    // 파일 선택 시 이벤트
    imageInput.addEventListener('change', function(e) {
        const files = Array.from(e.target.files);
        
        files.forEach(file => {
            const reader = new FileReader();
            
            reader.onload = function(event) {
                // 미리보기 박스 생성
                const box = document.createElement('div');
                box.className = 'image-upload-box';
                
                box.innerHTML = `
                    <img src="${event.target.result}" alt="preview">
                    <div class="btn-remove">&times;</div>
                `;
                
                // 삭제 이벤트 추가
                box.querySelector('.btn-remove').onclick = function(e) {
                    e.stopPropagation(); // 부모 클릭 방지
                    box.remove();
                    // 별도의 파일 배열을 관리할것
                };
                
                // 추가 버튼 앞에 삽입
                previewList.insertBefore(box, addBtn);
            };
            
            reader.readAsDataURL(file);
        });
    });
});

// 모달 열기 및 데이터 로드
async function openModal () {
    const modal = document.getElementById('projectModal');
    const spinner = document.getElementById('loadingSpinner');
    const listContainer = document.getElementById('projectListContainer');

    modal.style.display = 'block';
    
    // 1. 초기화: 기존 리스트 비우고 스피너 보여주기
    listContainer.innerHTML = '';
    spinner.style.display = 'block';

    try {
        // 2. API 호출
        const response = await fetch(`${ctx}/api/projects/all`);
        if (!response.ok) throw new Error('데이터를 불러오는데 실패했습니다.');
        
        const projects = await response.json();

        // 3. 데이터 렌더링
        renderProjectList(projects);
    } catch (error) {
        console.error('Error:', error);
        listContainer.innerHTML = `<li class="project-item" style="color:red; text-align:center;">Cannot Load Data.</li>`;
    } finally {
        // 4. 로딩 종료: 스피너 숨기기
        spinner.style.display = 'none';
    }
}

// 모달 닫기
function closeModal() {
    document.getElementById('projectModal').style.display = 'none';
}

// 프로젝트 리스트 렌더링 함수
function renderProjectList(projects) {
    const listContainer = document.getElementById('projectListContainer');
    
    if (projects.length === 0) {
        listContainer.innerHTML = '<li class="project-item">등록된 프로젝트가 없습니다.</li>';
        return;
    }

    projects.forEach(data => {
        const li = document.createElement('li');
        li.className = 'project-item';
        
        // 데이터 구조에 맞춰 HTML 생성
        li.innerHTML = `
        <strong>${data.projectName} (${data.projectRound}차)</strong><br>
        <small>ID: ${data.projectId} | 목표액: ${data.targetAmount.toLocaleString()}원 | 예상수익: ${data.expectedReturn}%</small>
        `;
        
        // 클릭 시 데이터 바인딩 이벤트
        li.onclick = () => selectProject(data);
        listContainer.appendChild(li);
    });
}


// 선택한 프로젝트 정보를 폼에 자동 입력 (차수는 +1)
async function selectProject(data) {
    selectedProjectId = data.projectId;
    console.log("pid: ", data.projectId);
    // JSP 내의 input name/id에 맞춰 매핑
    document.getElementsByName('farm_id')[0].value = data.farmId;
    document.getElementsByName('project_name')[0].value = data.projectName;
    document.getElementsByName('project_round')[0].value = data.projectRound + 1; // 새 차수 등록용
    document.getElementsByName('project_description')[0].value = data.projectDescription || '';
    document.getElementsByName('target_amount')[0].value = data.targetAmount;
    document.getElementsByName('min_amount_per_investor')[0].value = data.minAmountPerInvestor;
    document.getElementsByName('expected_return')[0].value = data.expectedReturn;
    document.getElementsByName('manager_count')[0].value = data.managerCount;
    // DTO의 필드명과 JSP의 input name을 매핑합니다.
    const dateFields = {
        'announcement_start_date': data.announcementStartDate,
        'announcement_end_date': data.announcementEndDate,
        'subscription_start_date': data.subscriptionStartDate,
        'subscription_end_date': data.subscriptionEndDate,
        'result_announcement_date': data.resultAnnouncementDate,
        'project_start_date': data.projectStartDate,
        'project_end_date': data.projectEndDate
    };

    // 각 날짜 필드 순회하며 값 입력
    for (const [name, value] of Object.entries(dateFields)) {
        document.getElementsByName(name)[0].value = DateUtil.toLocalDateTime(value);
    }
    
    // TODO: 사진 정보 가져와서 displayExistingImages 로 화면에 띄우기 

    try {
        const response = await fetch(`${ctx}/api/token/${data.projectId}`);
        if (!response.ok) throw new Error('토큰 정보를 가져오지 못했습니다.');
        
        const tokenData = await response.json();
        
        if (tokenData) {
            // JSP의 input name인 token_name과 ticker_symbol에 값 세팅
            document.getElementsByName('token_name')[0].value = tokenData.tokenName || '';
            document.getElementsByName('ticker_symbol')[0].value = tokenData.tickerSymbol || '';
        }
    } catch (error) {
        console.error("Token API Error:", error);
        // 토큰 정보가 없을 경우 빈칸으로 유지하거나 사용자 알림
    }

    closeModal();
}

// 폼 데이터를 JSON으로 만드는 공통 함수
function getFormData() {
    const form = document.querySelector('form');
    const formData = new FormData(form);
    const obj = {};
    
    // FormData를 JSON 객체로 변환
    formData.forEach((value, key) => {
        const camelKey = key.replace(/_([a-z])/g, (g) => g[1].toUpperCase());
        if (key === 'target_amount' || key === 'project_round') {
            obj[camelKey] = Number(value);
        } else if (key.includes('date') && value) {
            obj[camelKey] = DateUtil.toOffsetDateTime(value);
        } else {
            obj[camelKey] = value;
        }
    });
    
    // 수정일 경우 ID 포함
    if (selectedProjectId) {
        obj['projectId'] = selectedProjectId;
    }
    return obj;
}

// 프로젝트 수정
function updateProject() {
    const data = getFormData();

	fetch(`${ctx}/api/projects/update`, { method: 'POST',
        headers: { 
            'Content-Type': 'application/json; charset=UTF-8',
            'Accept': 'application/json' 
        },
        body: JSON.stringify(data) })
	.then(res => res.text())
	.then(status => {
	    if(status === "success") {
            alert("수정되었습니다.");
	        window.location.href = `${ctx}/admin/project/new`;
	    } else {
            const errorMsg = status;
            alert("실패: " + errorMsg);
        }
	});
}

function insertProject() {
    const data = getFormData();
}


// 기존 이미지들을 화면에 그려주는 함수
function displayExistingImages(imageUrls) {
    const previewList = document.getElementById('imagePreviewList');
    const addBtn = previewList.querySelector('.add-btn');

    // 기존에 있던 미리보기들(이미지 박스들)만 제거 (추가 버튼은 남김)
    const existingBoxes = previewList.querySelectorAll('.image-upload-box:not(.add-btn)');
    existingBoxes.forEach(box => box.remove());

    if (!imageUrls || !Array.isArray(imageUrls)) return;

    imageUrls.forEach(url => {
        const box = document.createElement('div');
        box.className = 'image-upload-box existing'; // 기존 이미지임을 표시하는 클래스 추가
        
        // 이미지 경로가 contextPath를 포함해야 한다면 ctx를 붙여줍니다.
        const fullUrl = url.startsWith('http') ? url : `${ctx}/uploads/projects/${url}`;

        box.innerHTML = `
            <img src="${fullUrl}" alt="project image">
            <div class="btn-remove" data-filename="${url}">&times;</div>
        `;

        // 삭제 버튼 클릭 이벤트 (기존 이미지는 서버에서도 지워야 하므로 처리 방식이 다를 수 있음)
        box.querySelector('.btn-remove').onclick = function(e) {
            e.stopPropagation();
            if(confirm("이 사진을 삭제하시겠습니까?")) {
                box.remove();
                // TODO: 삭제된 이미지 파일명을 별도 배열에 담아두었다가 수정 완료 시 서버에 알리기
                markImageAsDeleted(url); 
            }
        };

        // 추가 버튼( + ) 앞에 삽입
        previewList.insertBefore(box, addBtn);
    });
}