// api/token_api.js
import { http } from "../../api/http_client.js";

export const TokenApi = {
    getToken: (projectId) => http.get(`${ctx}/api/token/${projectId}`),
}