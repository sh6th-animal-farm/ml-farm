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
    }
}

export {FormUtil}