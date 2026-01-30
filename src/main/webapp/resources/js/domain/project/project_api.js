// api/project_api.js
import { http } from "../../api/http_client.js";

export const ProjectApi = {
  getAll: () => http.get(`${ctx}/api/project/all`),
  getPictures: (id) => http.get(`${ctx}/api/project/picture/${id}/all`),
  insert: (data) => http.post(`${ctx}/api/project/insert`, data),
  update: (data) => http.post(`${ctx}/api/project/update`, data),
  searchProjects: (query) => http.get(`${ctx}/project/list/fragment${query}`),
  starProject: (data) => http.post(`${ctx}/api/project/starred`, data),
  selectDividendPoll: (data) =>
    http.post(`${ctx}/api/project/dividend/poll/select`, data),
};
