import { http } from "./http_client.js";

export const AdminApi = {
    insertFarm: (data) => http.post(`${ctx}/farm/insert`, data)
}