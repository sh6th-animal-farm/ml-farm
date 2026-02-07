import { DateUtil } from "./date_util.js";

const FormUtil = {
    // 프로젝트 등록/수정 폼 내부 데이터를 JSON 객체로 변환
    adminProjectFormToJSON (formData, selectedProjectId, deletedPictureIds) {
        const obj = {};
    
        const arrayFields = ['project_image_names', 'projectImageNames'];
        
        // snake_case -> camelCase
        formData.forEach((value, key) => {
            const camelKey = key.replace(/_([a-z])/g, (g) => g[1].toUpperCase());

            if (arrayFields.includes(key) || arrayFields.includes(camelKey)) {
                if (!obj[camelKey]) {
                    obj[camelKey] = [];
                }
                obj[camelKey].push(value);
                return;
            }

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
        obj['deletedPictureIds'] = deletedPictureIds;
        return obj;
    },
    // 농장 등록/수정 폼 내부 데이터를 JSON 객체로 변환
    adminFarmFormToJSON(formData, selectedFarmId) {
        const obj = {};
        
        // 숫자형으로 변환해야 하는 필드 정의 (BigDecimal 대응)
        const numericFields = ['area', 'latitude', 'longitude', 'altitude'];

        formData.forEach((value, key) => {
            // snake_case (HTML name) -> camelCase (DTO field)
            let camelKey = key.replace(/_([a-z])/g, (g) => g[1].toUpperCase());

            // 1. 숫자형 데이터 처리
            if (numericFields.includes(key) || numericFields.includes(camelKey)) {
                obj[camelKey] = value ? Number(value) : null;
            } 
            // 2. 날짜 데이터 처리 (open_at -> openAt)
            else if (key === 'open_at' && value) {
                obj[camelKey] = DateUtil.toOffsetDateTime(value);
            } 
            // 3. 기타 문자열 데이터
            else {
                obj[camelKey] = value;
            }
        });

        // 수정 모드일 경우 ID 추가
        if (selectedFarmId) {
            obj['farmId'] = selectedFarmId;
        }

        return obj;
    }
}

export {FormUtil}