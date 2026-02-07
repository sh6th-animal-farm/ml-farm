import { http } from "./http_client.js";

export const AdminApi = {
    insertFarm: (data) => http.post(`${ctx}/farm/insert`, data),
    getCoords: (address) => http.get(`${ctx}/farm/get-coords?address=${encodeURIComponent(address)}`)
}