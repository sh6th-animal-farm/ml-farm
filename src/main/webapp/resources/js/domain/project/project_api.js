// api/project_api.js
import { http } from "../../api/http_client.js";

let commonUrl = `${ctx}/api/project`;
export const ProjectApi = {
  getAll: () => http.get(`${commonUrl}}/all`),
  getPictures: (id) => http.get(`${commonUrl}}/picture/${id}/all`),
  insert: (data) => http.post(`${commonUrl}}/insert`, data),
  update: (data) => http.post(`${commonUrl}}/update`, data),
  searchProjects: (query) => http.get(`${ctx}/project/list/fragment${query}`),
  starProject: (data) => http.post(`${commonUrl}}/starred`, data),
  getAllFarm:() => http.get(`${commonUrl}}/farm/all`)
};
