// util/http_client.js
export const http = {
    async request(url, options = {}) {
        const defaultHeaders = {
            'Content-Type': 'application/json; charset=UTF-8',
            'Accept': 'application/json'
        };

        const config = {
            ...options,
            headers: { ...defaultHeaders, ...options.headers }
        };

        try {
            const response = await fetch(url, config);
            console.log(response);
            // 공통 에러 처리 (상태 코드 체크)
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || '서버 응답 오류');
            }

            // 응답 본문이 있는지 먼저 확인
            const text = await response.text(); 
            if (!text) return null; // 빈 응답 처리

            // 본문이 JSON 형태인지 시도해보고, 아니면 텍스트 그대로 반환
            try {
                return JSON.parse(text);
            } catch (e) {
                return text; // JSON이 아니면 "success" 같은 문자열 반환
            }
            
        } catch (error) {
            console.error(`[API Error] ${url}:`, error);
            // 여기서 디자인 가이드에 따른 공통 알림(모달/토스트) 호출 가능
            alert("요청 처리 중 오류가 발생했습니다.\n" + error.message);
            throw error;
        }
    },

    get(url) { return this.request(url, { method: 'GET' }); },
    post(url, body) { return this.request(url, { method: 'POST', body: JSON.stringify(body) }); }
};