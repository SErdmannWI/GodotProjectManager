package com.potatobuddy.godotmanager.controller;

import com.potatobuddy.godotmanager.dto.project.NewProjectRequest;
import com.potatobuddy.godotmanager.dto.project.ProjectResponse;
import com.potatobuddy.godotmanager.dto.project.UpdateProjectRequest;
import com.potatobuddy.godotmanager.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/all")
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<ProjectResponse> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable String id) {
        ProjectResponse project = projectService.getProjectById(id);
        if (project != null) {
            return ResponseEntity.ok(project);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/newProject")
    public ResponseEntity<ProjectResponse> createProject(@RequestBody NewProjectRequest newProjectRequest) {
        return ResponseEntity.ok(projectService.createProject(newProjectRequest));
    }

    @PutMapping("/updateProject/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable String id, @RequestBody UpdateProjectRequest updatedProjectRequest) {
        ProjectResponse project = projectService.updateProject(updatedProjectRequest);
        if (project != null) {
            return ResponseEntity.ok(project);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable String id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

}
