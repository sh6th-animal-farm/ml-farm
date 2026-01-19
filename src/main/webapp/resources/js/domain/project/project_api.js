// api/project_api.js
import { http } from "../../api/http_client.js";

export const ProjectApi = {
    getAll: () => http.get(`${ctx}/api/projects/all`),
    getPictures: (id) => http.get(`${ctx}/api/project/picture/${id}/all`),
    insert: (data) => http.post(`${ctx}/api/projects/insert`, data),
    update: (data) => http.post(`${ctx}/api/projects/update`, data),
    starProject: (data) => http.post(`${ctx}/api/projects/starred/interest`, data)
};