// api/project_api.js
import { http } from "../../api/http_client.js";

let commonUrl = "/api/project";
export const ProjectApi = {

  getAll: () => http.get(`${ctx}${commonUrl}}/all`),
  getPictures: (id) => http.get(`${ctx}${commonUrl}}/picture/${id}/all`),
  insert: (data) => http.post(`${ctx}${commonUrl}}/insert`, data),
  update: (data) => http.post(`${ctx}${commonUrl}}/update`, data),
  searchProjects: (query) => http.get(`${ctx}/project/list/fragment${query}`),
  starProject: (data) => http.post(`${ctx}${commonUrl}}/starred`, data),
  getAllFarm:() => http.get(`${ctx}${commonUrl}}/farm/all`)
};
