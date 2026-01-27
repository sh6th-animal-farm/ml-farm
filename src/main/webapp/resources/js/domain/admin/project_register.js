import { ProjectApi } from "../project/project_api.js";
import { TokenApi } from "../token/token_api.js";
import { DateUtil } from "../../util/date_util.js";
import { FormUtil } from "../../util/form_util.js";
import { UI } from "../../util/ui_util.js";
import { UploadUtil } from "../../util/upload_util.js";
import { AdminRenderer } from "./admin_renderer.js";

let selectedProjectId = null;
let deletedPictureIds = [];
const previewList = document.getElementById('imagePreviewList');
const addBtn = previewList.querySelector('.add-btn');

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

    // 파일 선택 시 이벤트
    imageInput.addEventListener('change', function(e) {
        const files = Array.from(e.target.files);
        
        files.forEach(file => UploadUtil.uploadImage(file, addImageBox))
    });
});

// 사진 선택 시 동작
function addImageBox(src, savedFileName) {
    // 미리보기 박스 생성
    const box = AdminRenderer.createImageBox(src, savedFileName, 
        // 제거 버튼 눌렀을때 동작
        function(e) {
            e.stopPropagation(); // 부모 클릭 방지
            box.remove();
        }
    )
    // 추가 버튼 앞에 삽입
    previewList.insertBefore(box, addBtn);
};

// 모달 열기 및 데이터 로드
async function openModal () {
    // 기존 리스트 비우고 스피너 보여주기
    UI.show('projectModal');
    UI.clear('projectListContainer');
    UI.show('loadingSpinner');

    try {
        // API 호출
        const projects = await ProjectApi.getAll();
        // 데이터 렌더링
        AdminRenderer.renderProjectList(projects, selectProject);
    } catch (error) {
        listContainer.innerHTML = `<li class="project-item" style="color:red; text-align:center;">Cannot Load Data.</li>`;
    } finally {
        // 로딩 종료: 스피너 숨기기
        UI.hide('loadingSpinner')
    }
}

// 모달 닫기
function closeModal() {
    UI.hide('projectModal');
}

// 선택한 프로젝트 정보를 폼에 자동 입력
async function selectProject(data) {
    selectedProjectId = data.projectId;
    // JSP 내의 input name/id에 맞춰 매핑
    document.getElementsByName('farm_id')[0].value = data.farmId;
    document.getElementsByName('project_name')[0].value = data.projectName;
    document.getElementsByName('project_round')[0].value = data.projectRound;
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
    
    // 사진 정보 가저오기
    try {
        const picturesData = await ProjectApi.getPictures(data.projectId);
        if (picturesData) {
            AdminRenderer.displayExistingImages(picturesData, previewList, addBtn, markImageAsDeleted);
        }
    } catch (error) {
        // 사진 정보가 없을 경우 빈칸으로 유지하거나 사용자 알림
        console.error("Picture API Error:", error);
    }

    // 토큰 정보 가져오기
    try {
        const tokenData = await TokenApi.getToken(data.projectId);
        
        if (tokenData) {
            // JSP의 input name인 token_name과 ticker_symbol에 값 세팅
            document.getElementsByName('token_name')[0].value = tokenData.tokenName || '';
            document.getElementsByName('ticker_symbol')[0].value = tokenData.tickerSymbol || '';
            document.getElementsByName('total_supply')[0].value = tokenData.totalSupply || '';
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
    return FormUtil.adminProjectFormToJSON(formData, selectedProjectId, deletedPictureIds);
}

// 프로젝트 수정
function updateProject() {
    const data = getFormData();

    ProjectApi.update(data)
    .then(text => {
        console.log(text);
	    if(text === "success") {
            alert("수정되었습니다.");
	        window.location.href = `${ctx}/admin/project/new`;
	    } else {
            const errorMsg = text;
            alert("실패: " + errorMsg);
        }
	});
	
}

function insertProject() {
    const data = getFormData();

    ProjectApi.insert(data)
    .then(text => {
        if(text === "success") {
            alert("프로젝트가 등록되고 증권사 API 성공하였습니다.");
            window.location.href = `${ctx}/admin/project/new`;
        } else if(text === "api_fail"){
        	alert("프로젝트가 등록되고 증권사 API 실패하였습니다.");
            window.location.href = `${ctx}/admin/project/new`;
        } else {
            const errorMsg = text;
            alert("실패: " + errorMsg);
        }
    });
}

function markImageAsDeleted(pictureId) {
    if (pictureId && !deletedPictureIds.includes(pictureId)) {
        deletedPictureIds.push(pictureId);
    }
}