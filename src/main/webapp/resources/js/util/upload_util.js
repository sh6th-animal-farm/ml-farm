const UploadUtil = {
    uploadImage : async function (file, addImageBox) {
        const formData = new FormData();
        formData.append('file', file);

        try {
            const response = await fetch(`${ctx}/api/upload/project-image`, {
                method: 'POST',
                body: formData
            });

            if (response.ok) {
                const result = await response.json();
                // 서버가 준 실제 저장된 URL로 미리보기 박스 생성
                addImageBox(ctx+result.url, result.savedFileName); 
            }
        } catch (error) {
            console.error("Upload Error:", error);
        }
    }
}

export {UploadUtil}