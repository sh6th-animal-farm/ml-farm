import { http } from "./http_client.js"

export const AuthApi = {
    getUser: ()=>http.get(`${ctx}/api/user/me`),
}