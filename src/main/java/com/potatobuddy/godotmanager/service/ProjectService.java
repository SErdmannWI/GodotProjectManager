package com.potatobuddy.godotmanager.service;

import com.potatobuddy.godotmanager.dto.project.NewProjectRequest;
import com.potatobuddy.godotmanager.dto.project.ProjectResponse;
import com.potatobuddy.godotmanager.dto.project.UpdateProjectRequest;
import com.potatobuddy.godotmanager.exceptions.InvalidProjectRequestException;
import com.potatobuddy.godotmanager.exceptions.ProjectNotFoundException;
import com.potatobuddy.godotmanager.model.Project;
import com.potatobuddy.godotmanager.model.Subtask;
import com.potatobuddy.godotmanager.model.Task;
import com.potatobuddy.godotmanager.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectService {

    private ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**------------------------------------------- Project Methods ---------------------------------------------------*/

    public ProjectResponse createProject(NewProjectRequest newProjectRequest) {
        if (newProjectRequest.getName() == null || newProjectRequest.getName().isEmpty() || newProjectRequest.getName().isBlank()) {
            throw new InvalidProjectRequestException("Project name cannot be empty or null");
        } else if (newProjectRequest.getDescription() == null || newProjectRequest.getDescription().isEmpty() || newProjectRequest.getDescription().isBlank()) {
            throw new InvalidProjectRequestException("Project description cannot be empty or null");
        }

        Project newProject = new Project.Builder()
            .withId(UUID.randomUUID().toString())
            .withName(newProjectRequest.getName())
            .withDescription(newProjectRequest.getDescription())
            .build();

        Project savedProject = projectRepository.save(newProject);

        return projectToProjectResponse(savedProject);
    }

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream()
            .map(ProjectService::projectToProjectResponse)
            .toList();
    }

    public ProjectResponse getProjectById(String id) {
        if (id == null || id.isEmpty() || id.isBlank()) {
            throw new InvalidProjectRequestException("Project ID cannot be empty or null");
        }

        Project project = projectRepository.findById(id)
            .orElseThrow( () -> new ProjectNotFoundException("Project not found with ID: " + id));

        return projectToProjectResponse(project);
    }

    public void deleteProject(String id) {
        if (id == null || id.isEmpty() || id.isBlank()) {
            throw new InvalidProjectRequestException("Project ID cannot be empty or null");
        }

        if (!projectRepository.existsById(id)) {
            throw new ProjectNotFoundException("Project not found with ID: " + id);
        }

        projectRepository.deleteById(id);
    }

    public ProjectResponse updateProject(UpdateProjectRequest updateProjectRequest) {
        if (updateProjectRequest.getId() == null || updateProjectRequest.getId().isEmpty() || updateProjectRequest.getId().isBlank()) {
            throw new InvalidProjectRequestException("Project ID cannot be empty or null");
        }
        if (updateProjectRequest.getName() == null || updateProjectRequest.getName().isEmpty() || updateProjectRequest.getName().isBlank()) {
            throw new InvalidProjectRequestException("Project name cannot be empty or null");
        } else if (updateProjectRequest.getDescription() == null || updateProjectRequest.getDescription().isEmpty() || updateProjectRequest.getDescription().isBlank()) {
            throw new InvalidProjectRequestException("Project description cannot be empty or null");
        }

        // Fetch the project to update from the database
        Project projectToUpdate = projectRepository.findById(updateProjectRequest.getId())
            .orElseThrow(() -> new ProjectNotFoundException("Project not found with ID: " + updateProjectRequest.getId()));

        // Update the project fields
        projectToUpdate.setName(updateProjectRequest.getName());
        projectToUpdate.setDescription(updateProjectRequest.getDescription());

        // Clear existing tasks (if necessary) and add updated tasks
        projectToUpdate.getTasks().clear();

        for (Task incomingTask : updateProjectRequest.getTasks()) {
            // If a task doesn't have an ID, generate one
            if (incomingTask.getId() == null || incomingTask.getId().isEmpty()) {
                assignTaskId(incomingTask);
            }

            // Set the project relationship correctly for each task
            incomingTask.setProject(projectToUpdate);

            // Handle subtasks for each task
            for (Subtask incomingSubtask : incomingTask.getSubtasks()) {
                if (incomingSubtask.getId() == null || incomingSubtask.getId().isEmpty()) {
                    assignSubtaskId(incomingSubtask);
                }

                // Set the task relationship correctly for each subtask
                incomingSubtask.setTask(incomingTask);
            }

            // Add the task (with subtasks) to the project
            projectToUpdate.addTask(incomingTask);
        }

        // Clear existing backlog (if necessary) and add updated backlog
        projectToUpdate.getBacklog().clear();

        for (Task backlogTask : updateProjectRequest.getBacklog()) {
            // Add the backlog task to the project
            projectToUpdate.addBacklog(backlogTask);
        }

        // Save the updated project and return the response
        Project savedProject = projectRepository.save(projectToUpdate);

        return projectToProjectResponse(savedProject);
    }

    /**------------------------------------------- Utility Methods ---------------------------------------------------*/

    public static ProjectResponse projectToProjectResponse(Project project) {
        return new ProjectResponse.Builder()
            .withId(project.getId())
            .withName(project.getName())
            .withDescription(project.getDescription())
            .withTasks(project.getTasks())
            .withBacklog(project.getBacklog())
            .build();
    }

    private void assignTaskId(Task task) {
        task.setId(UUID.randomUUID().toString());
    }

    private void assignSubtaskId(Subtask subtask) {
        subtask.setId(UUID.randomUUID().toString());
    }
}

