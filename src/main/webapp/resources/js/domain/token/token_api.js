// api/token_api.js
import { http } from "../../api/http_client.js";

export const TokenApi = {
    getToken: (projectId) => http.get(`${ctx}/api/token/${projectId}`),
    getTokenList: () => http.get(`${ctx}/api/market`),
    getCashBalance: () => http.get(`${ctx}/api/account/balance`),
    getTokenBalance: (tokenId) => http.get(`${ctx}/api/account/balance/${tokenId}`),
    getPendingList: (tokenId) => http.get(`${ctx}/api/token/pending/${tokenId}`),
    createOrder: (tokenId, order) => http.post(`${ctx}/api/token/order/${tokenId}`, order),
    cancelOrder: (tokenId, orderId) => http.post(`${ctx}/api/token/order-cancel/${tokenId}/${orderId}`),
    getCandles: (tokenId, unit = 1) => http.get(`${ctx}/api/market/candles/${tokenId}?unit=${unit}`),
};
