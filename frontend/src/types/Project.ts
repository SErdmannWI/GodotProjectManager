export interface Subtask {
  id: string;
  name: string;
  description: string;
  dueDate: string;
  status: string;
  difficulty: string;
}

export interface Task {
  id: string;
  name: string;
  description: string;
  dueDate: string;
  status: string;
  difficulty: string;
  subtasks: Subtask[];
}

export interface Project {
  project_id: string;
  project_name: string;
  project_description: string;
  project_tasks: Task[];
}

export type NewProject = {
  project_name: string;
  project_description: string;
};
