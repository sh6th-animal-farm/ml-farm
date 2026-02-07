// util/http_client.js
export const http = {
  async request(url, options = {}) {
    const defaultHeaders = {
      "Content-Type": "application/json; charset=UTF-8",
      Accept: "application/json",
      Authorization: `Bearer ${localStorage.getItem("accessToken") || ""}`,
    };

    const config = {
      ...options,
      headers: { ...defaultHeaders, ...options.headers },
    };

    try {
      const response = await fetch(url, config);
      const text = await response.text();

      // 1. 서버 에러(400, 500 등)가 발생한 경우
      if (!response.ok) {
        let errorMsg = "서버 내부 오류가 발생했습니다.";
        console.log(response);
        if (response.status === 401)  {
          errorMsg = "로그인이 필요합니다.";
        } else if (response.status === 403) {
          errorMsg = "권한이 없습니다.";
        }
        
        try {
          const errorJson = JSON.parse(text);
          // 서버가 던진 JSON 파싱 시도
          errorMsg = errorJson.message || errorMsg;
        } catch (e) {
          // JSON 파싱 실패 시, 원본 메시지
          if (text && text.length < 100) errorMsg = text;
        }

        throw new Error(errorMsg);
      }

      // 2. 정상 응답(200 OK)인 경우
      // 응답 본문이 있는지 먼저 확인
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
      // ToastManager.show("요청 처리 중 오류가 발생했습니다.\n" + error.message);
      throw error;
    }
  },

  get(url) {
    return this.request(url, { method: "GET" });
  },
  post(url, body) {
    return this.request(url, { method: "POST", body: JSON.stringify(body) });
  },
};
