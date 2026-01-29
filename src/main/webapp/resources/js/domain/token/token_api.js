// api/token_api.js
import { http } from "../../api/http_client.js";

export const TokenApi = {
    getCandle: (tokenId) => http.get(`${ctx}/api/market/candles/${tokenId}`),
};
