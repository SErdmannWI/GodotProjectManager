import api from './config';
import { Project, NewProject, Task, Subtask } from '@/types/Project';

export const projectApi = {
  getAllProjects: () => api.get<Project[]>('/project/all'),
  getProjectById: (id: string) => api.get<Project>(`/project/${id}`),
  createProject: (project: NewProject) => api.post<Project>('/project/newProject', project),
  updateProject: (id: string, project: Partial<Project>) => 
      api.put<Project>(`/project/updateProject/${id}`, project),
  deleteProject: (id: string) => api.delete(`/project/${id}`),
};
