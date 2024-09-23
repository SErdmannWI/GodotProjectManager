package com.potatobuddy.godotmanager;
import com.potatobuddy.godotmanager.dto.project.NewProjectRequest;
import com.potatobuddy.godotmanager.dto.project.ProjectResponse;
import com.potatobuddy.godotmanager.dto.project.UpdateProjectRequest;
import com.potatobuddy.godotmanager.exceptions.InvalidProjectRequestException;
import com.potatobuddy.godotmanager.exceptions.ProjectNotFoundException;
import com.potatobuddy.godotmanager.model.Constants;
import com.potatobuddy.godotmanager.model.Project;
import com.potatobuddy.godotmanager.model.Subtask;
import com.potatobuddy.godotmanager.model.Task;
import com.potatobuddy.godotmanager.repository.ProjectRepository;
import com.potatobuddy.godotmanager.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTests {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    // Test project used for each test
    private Project project;
    private Project project2;
    private Project projectNoTasks;

    // Test variables
    private String testId1 = UUID.randomUUID().toString();
    private String testId2 = UUID.randomUUID().toString();
    private String testIdNoTasks = UUID.randomUUID().toString();
    private String testIdWithTasks = UUID.randomUUID().toString();

    private String testName1 = "Test Project 1";
    private String testName2 = "Test Project 2";
    private String testNameNoTasks = "Test Project with no Tasks";
    private String testNameWithTasks = "Test Project with Tasks";

    private String testDescription = "This is a test description";

    @BeforeEach
    public void setup() {
        projectRepository = mock(ProjectRepository.class);
        projectService = new ProjectService(projectRepository);

        project = new Project();
        project.setId(testId1);
        project.setName(testName1);
        project.setDescription(testDescription);
        project.setTasks(new ArrayList<>());

        project2 = new Project();
        project2.setId(testId2);
        project2.setName(testName2);
        project2.setDescription(testDescription);
        project2.setTasks(new ArrayList<>());
        project2.setTasks(new ArrayList<>());

        projectNoTasks = new Project.Builder()
            .withId(testIdNoTasks)
            .withDescription(testDescription)
            .withName(testNameNoTasks)
            .withTasks(new ArrayList<>())
            .withBacklog(new ArrayList<>())
            .build();
    }

    /**-------------------------------------------- Create Project ---------------------------------------------------*/

    @Test
    public void createProject_validProject_returnsProject() {
        // GIVEN
        NewProjectRequest newProjectRequest = new NewProjectRequest();
        newProjectRequest.setName(testName1);
        newProjectRequest.setDescription(testDescription);
        ArgumentCaptor<Project> projectArgumentCaptor = ArgumentCaptor.forClass(Project.class);
        Project mockProject = new Project.Builder()
            .withName(newProjectRequest.getName())
            .withDescription(newProjectRequest.getDescription())
            .build();

        // WHEN
        when(projectRepository.save(any())).thenReturn(mockProject);
        projectService.createProject(newProjectRequest);

        // THEN
        verify(projectRepository).save(projectArgumentCaptor.capture());
        Project capturedProject = projectArgumentCaptor.getValue();

        Assertions.assertNotNull(capturedProject);
        Assertions.assertNotNull(capturedProject.getId());
        Assertions.assertEquals(capturedProject.getName(), newProjectRequest.getName());
        Assertions.assertEquals(capturedProject.getDescription(), newProjectRequest.getDescription());
        Assertions.assertEquals(capturedProject.getTasks(), new ArrayList<>());
    }

    @Test
    public void createProject_nullName_throwsInvalidProjectRequestException() {
        // GIVEN
        NewProjectRequest newProjectRequest = new NewProjectRequest();
        newProjectRequest.setName(null);
        newProjectRequest.setDescription(testDescription);

        // WHEN / THEN
        Assertions.assertThrows(InvalidProjectRequestException.class,
            () -> projectService.createProject(newProjectRequest));
    }

    @Test
    public void createProject_emptyName_throwsInvalidProjectRequestException() {
        // GIVEN
        NewProjectRequest newProjectRequest = new NewProjectRequest();
        newProjectRequest.setName("");
        newProjectRequest.setDescription(testDescription);

        // WHEN / THEN
        Assertions.assertThrows(InvalidProjectRequestException.class,
            () -> projectService.createProject(newProjectRequest));
    }

    @Test
    public void createProject_blankName_throwsInvalidProjectRequestException() {
        // GIVEN
        NewProjectRequest newProjectRequest = new NewProjectRequest();
        newProjectRequest.setName(" ");
        newProjectRequest.setDescription(testDescription);

        // WHEN / THEN
        Assertions.assertThrows(InvalidProjectRequestException.class,
            () -> projectService.createProject(newProjectRequest));
    }

    @Test
    public void createProject_nullDescription_throwsInvalidProjectRequestException() {
        // GIVEN
        NewProjectRequest newProjectRequest = new NewProjectRequest();
        newProjectRequest.setName(testName1);
        newProjectRequest.setDescription(null);

        // WHEN / THEN
        Assertions.assertThrows(InvalidProjectRequestException.class,
            () -> projectService.createProject(newProjectRequest));
    }

    @Test
    public void createProject_emptyDescription_throwsInvalidProjectRequestException() {
        // GIVEN
        NewProjectRequest newProjectRequest = new NewProjectRequest();
        newProjectRequest.setName(testName1);
        newProjectRequest.setDescription("");

        // WHEN / THEN
        Assertions.assertThrows(InvalidProjectRequestException.class,
            () -> projectService.createProject(newProjectRequest));
    }

    @Test
    public void createProject_blankDescription_throwsInvalidProjectRequestException() {
        // GIVEN
        NewProjectRequest newProjectRequest = new NewProjectRequest();
        newProjectRequest.setName(testName1);
        newProjectRequest.setDescription(" ");

        // WHEN / THEN
        Assertions.assertThrows(InvalidProjectRequestException.class,
            () -> projectService.createProject(newProjectRequest));
    }

    /**--------------------------------------------- Get Project -----------------------------------------------------*/

    @Test
    public void getAllProjects_hasProjects_returnsProjects() {
        // GIVEN
        List<Project> projects = new ArrayList<>();
        projects.add(project);
        projects.add(project2);
        when(projectRepository.findAll()).thenReturn(projects);

        // WHEN
        List<ProjectResponse> result = projectService.getAllProjects();

        // THEN
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());

        Assertions.assertEquals(project.getId(), result.get(0).getId());
        Assertions.assertEquals(project.getName(), result.get(0).getName());
        Assertions.assertEquals(project.getDescription(), result.get(0).getDescription());
        Assertions.assertEquals(project.getTasks(), result.get(0).getTasks());
        Assertions.assertEquals(project2.getId(), result.get(1).getId());
        Assertions.assertEquals(project2.getName(), result.get(1).getName());
        Assertions.assertEquals(project2.getDescription(), result.get(1).getDescription());
        Assertions.assertEquals(project2.getTasks(), result.get(1).getTasks());
    }

    @Test
    public void getAllProjects_noProjects_returnsEmptyList() {
        // GIVEN
        when(projectRepository.findAll()).thenReturn(new ArrayList<>());

        // WHEN
        List<ProjectResponse> result = projectService.getAllProjects();

        // THEN
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void getProjectById_projectExists_returnsProject() {
        // GIVEN
        when(projectRepository.findById(testId1)).thenReturn(Optional.of(project));

        // WHEN
        ProjectResponse result = projectService.getProjectById(testId1);

        // THEN
        Assertions.assertNotNull(result);
        Assertions.assertEquals(project.getId(), result.getId());
        Assertions.assertEquals(project.getName(), result.getName());
        Assertions.assertEquals(project.getDescription(), result.getDescription());
        Assertions.assertEquals(project.getTasks(), result.getTasks());
    }

    @Test
    public void getProjectById_projectDoesNotExist_returnsNull() {
        // GIVEN
        String randomId = UUID.randomUUID().toString();
        when(projectRepository.findById(randomId)).thenReturn(Optional.empty());

        // WHEN / THEN
        Assertions.assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectById(randomId));
    }

    @Test
    public void getProjectById_nullId_throwsInvalidProjectRequestException() {
        // GIVEN
        String nullId = null;

        // WHEN / THEN
        Assertions.assertThrows(InvalidProjectRequestException.class, () -> projectService.getProjectById(nullId));
    }

    @Test
    public void getProjectById_emptyId_throwsInvalidProjectRequestException() {
        // GIVEN
        String emptyId = "";

        // WHEN / THEN
        Assertions.assertThrows(InvalidProjectRequestException.class, () -> projectService.getProjectById(emptyId));
    }

    @Test
    public void getProjectById_blankId_throwsInvalidProjectRequestException() {
        // GIVEN
        String blankId = " ";

        // WHEN / THEN
        Assertions.assertThrows(InvalidProjectRequestException.class, () -> projectService.getProjectById(blankId));
    }

    /**-------------------------------------------- Update Project ---------------------------------------------------*/
    @Test
    public void testUpdateProject_validRequestWithNoTasks_updatesProject() {
        // GIVEN
        UpdateProjectRequest updateProjectRequest = new UpdateProjectRequest();
        updateProjectRequest.setId(testIdNoTasks);
        updateProjectRequest.setName(testNameNoTasks);
        updateProjectRequest.setDescription(testDescription);
        updateProjectRequest.setTasks(new ArrayList<>());

        when(projectRepository.findById(testIdNoTasks)).thenReturn(Optional.of(projectNoTasks));
        when(projectRepository.save(projectNoTasks)).thenReturn(projectNoTasks);

        // WHEN
        ProjectResponse result = projectService.updateProject(updateProjectRequest);

        // THEN
        Assertions.assertNotNull(result);
        Assertions.assertEquals(updateProjectRequest.getId(), result.getId());
        Assertions.assertEquals(updateProjectRequest.getName(), result.getName());
        Assertions.assertEquals(updateProjectRequest.getDescription(), result.getDescription());
        Assertions.assertEquals(new ArrayList<>(), updateProjectRequest.getTasks());
    }

    @Test
    public void updateProject_validRequestWithNewTasks_updatesProject() {
        // GIVEN
        ArgumentCaptor<Project> projectArgumentCaptor = ArgumentCaptor.forClass(Project.class);

        UpdateProjectRequest updateProjectRequest = new UpdateProjectRequest();
        updateProjectRequest.setId(testIdWithTasks);
        updateProjectRequest.setName(testNameWithTasks);
        updateProjectRequest.setDescription(testDescription);
        updateProjectRequest.setTasks(getNoIdTasks());

        Project oldProject = new Project.Builder()
            .withName(testNameWithTasks)
            .withId(testIdWithTasks)
            .withDescription(testDescription)
            .withTasks(new ArrayList<>())
            .build();

        Project updatedProject = new Project.Builder()
            .withName(testNameWithTasks)
            .withId(testIdWithTasks)
            .withDescription(testDescription)
            .withTasks(updateProjectRequest.getTasks())
            .build();

        updatedProject.getTasks()
            .forEach(task -> task.setProject(updatedProject));

        when(projectRepository.findById(testIdWithTasks)).thenReturn(Optional.ofNullable(oldProject));
        when(projectRepository.save(any())).thenReturn(updatedProject);

        // WHEN
        ProjectResponse returnedProjectResponse = projectService.updateProject(updateProjectRequest);

        verify(projectRepository).save(projectArgumentCaptor.capture());
        Project capturedProject = projectArgumentCaptor.getValue();

        // THEN
        Assertions.assertEquals(updateProjectRequest.getId(), returnedProjectResponse.getId());
        Assertions.assertEquals(updateProjectRequest.getId(), capturedProject.getId());

        Assertions.assertEquals(updateProjectRequest.getName(), returnedProjectResponse.getName());
        Assertions.assertEquals(updateProjectRequest.getName(), capturedProject.getName());

        Assertions.assertEquals(updateProjectRequest.getDescription(), returnedProjectResponse.getDescription());
        Assertions.assertEquals(updateProjectRequest.getDescription(), capturedProject.getDescription());

        Assertions.assertEquals(updateProjectRequest.getTasks().size(), capturedProject.getTasks().size());
        Assertions.assertEquals(updateProjectRequest.getTasks().size(), returnedProjectResponse.getTasks().size());

        for (int i = 0; i < updateProjectRequest.getTasks().size(); i++) {
            Task requestTask = updateProjectRequest.getTasks().get(i);
            Task capturedTask = capturedProject.getTasks().get(i);
            Task responseTask = returnedProjectResponse.getTasks().get(i);

            // Ensure that the task has an assigned ID in both captured and returned tasks
            Assertions.assertNotNull(capturedTask.getId());
            Assertions.assertNotNull(responseTask.getId());

            // Check that other properties are equal
            Assertions.assertEquals(requestTask.getName(), capturedTask.getName());
            Assertions.assertEquals(requestTask.getName(), responseTask.getName());

            Assertions.assertEquals(requestTask.getDescription(), capturedTask.getDescription());
            Assertions.assertEquals(requestTask.getDescription(), responseTask.getDescription());

            Assertions.assertEquals(requestTask.getStatus(), capturedTask.getStatus());
            Assertions.assertEquals(requestTask.getStatus(), responseTask.getStatus());

            Assertions.assertEquals(requestTask.getDueDate(), capturedTask.getDueDate());
            Assertions.assertEquals(requestTask.getDueDate(), responseTask.getDueDate());

            Assertions.assertEquals(requestTask.getDifficulty(), capturedTask.getDifficulty());
            Assertions.assertEquals(requestTask.getDifficulty(), responseTask.getDifficulty());

            Assertions.assertEquals(requestTask.getTaskType(), capturedTask.getTaskType());
            Assertions.assertEquals(requestTask.getTaskType(), responseTask.getTaskType());

            // Check the subtasks for this task
            Assertions.assertEquals(requestTask.getSubtasks().size(), capturedTask.getSubtasks().size());
            Assertions.assertEquals(requestTask.getSubtasks().size(), responseTask.getSubtasks().size());

            for (int j = 0; j < requestTask.getSubtasks().size(); j++) {
                Subtask requestSubtask = requestTask.getSubtasks().get(j);
                Subtask capturedSubtask = capturedTask.getSubtasks().get(j);
                Subtask responseSubtask = responseTask.getSubtasks().get(j);

                // Ensure that the subtask has an assigned ID in both captured and returned subtasks
                Assertions.assertNotNull(capturedSubtask.getId());
                Assertions.assertNotNull(responseSubtask.getId());

                // Check that other properties are equal
                Assertions.assertEquals(requestSubtask.getName(), capturedSubtask.getName());
                Assertions.assertEquals(requestSubtask.getName(), responseSubtask.getName());

                Assertions.assertEquals(requestSubtask.getDescription(), capturedSubtask.getDescription());
                Assertions.assertEquals(requestSubtask.getDescription(), responseSubtask.getDescription());

                Assertions.assertEquals(requestSubtask.getStatus(), capturedSubtask.getStatus());
                Assertions.assertEquals(requestSubtask.getStatus(), responseSubtask.getStatus());

                Assertions.assertEquals(requestSubtask.getDueDate(), capturedSubtask.getDueDate());
                Assertions.assertEquals(requestSubtask.getDueDate(), responseSubtask.getDueDate());

                Assertions.assertEquals(requestSubtask.getDifficulty(), capturedSubtask.getDifficulty());
                Assertions.assertEquals(requestSubtask.getDifficulty(), responseSubtask.getDifficulty());
            }
        }
    }

    @Test
    public void updateProject_validRequestWithExistingTasks_updatesProject() {
        // GIVEN
        ArgumentCaptor<Project> projectArgumentCaptor = ArgumentCaptor.forClass(Project.class);

        UpdateProjectRequest updateProjectRequest = new UpdateProjectRequest();
        updateProjectRequest.setId(testIdWithTasks);
        updateProjectRequest.setName(testNameWithTasks);
        updateProjectRequest.setDescription(testDescription);
        updateProjectRequest.setTasks(getTasksWithId());

        Project oldProject = new Project.Builder()
            .withName(testNameWithTasks)
            .withId(testIdWithTasks)
            .withDescription(testDescription)
            .withTasks(new ArrayList<>())
            .build();

        Project updatedProject = new Project.Builder()
            .withName(testNameWithTasks)
            .withId(testIdWithTasks)
            .withDescription(testDescription)
            .withTasks(updateProjectRequest.getTasks())
            .build();

        when(projectRepository.findById(testIdWithTasks)).thenReturn(Optional.ofNullable(oldProject));
        when(projectRepository.save(any())).thenReturn(updatedProject);

        // WHEN
        ProjectResponse returnedProjectResponse = projectService.updateProject(updateProjectRequest);

        verify(projectRepository).save(projectArgumentCaptor.capture());
        Project capturedProject = projectArgumentCaptor.getValue();

        // THEN
        Assertions.assertEquals(updateProjectRequest.getId(), returnedProjectResponse.getId());
        Assertions.assertEquals(updateProjectRequest.getId(), capturedProject.getId());

        Assertions.assertEquals(updateProjectRequest.getName(), returnedProjectResponse.getName());
        Assertions.assertEquals(updateProjectRequest.getName(), capturedProject.getName());

        Assertions.assertEquals(updateProjectRequest.getDescription(), returnedProjectResponse.getDescription());
        Assertions.assertEquals(updateProjectRequest.getDescription(), capturedProject.getDescription());

        Assertions.assertEquals(updateProjectRequest.getTasks().size(), capturedProject.getTasks().size());
        Assertions.assertEquals(updateProjectRequest.getTasks().size(), returnedProjectResponse.getTasks().size());

        for (int i = 0; i < updateProjectRequest.getTasks().size(); i++) {
            Task requestTask = updateProjectRequest.getTasks().get(i);
            Task capturedTask = capturedProject.getTasks().get(i);
            Task responseTask = returnedProjectResponse.getTasks().get(i);

            // Check that the task ID is identical to the one in the request
            Assertions.assertEquals(requestTask.getId(), capturedTask.getId());
            Assertions.assertEquals(requestTask.getId(), responseTask.getId());

            // Check that other properties are equal
            Assertions.assertEquals(requestTask.getName(), capturedTask.getName());
            Assertions.assertEquals(requestTask.getName(), responseTask.getName());

            Assertions.assertEquals(requestTask.getDescription(), capturedTask.getDescription());
            Assertions.assertEquals(requestTask.getDescription(), responseTask.getDescription());

            Assertions.assertEquals(requestTask.getStatus(), capturedTask.getStatus());
            Assertions.assertEquals(requestTask.getStatus(), responseTask.getStatus());

            Assertions.assertEquals(requestTask.getDueDate(), capturedTask.getDueDate());
            Assertions.assertEquals(requestTask.getDueDate(), responseTask.getDueDate());

            Assertions.assertEquals(requestTask.getDifficulty(), capturedTask.getDifficulty());
            Assertions.assertEquals(requestTask.getDifficulty(), responseTask.getDifficulty());

            Assertions.assertEquals(requestTask.getTaskType(), capturedTask.getTaskType());
            Assertions.assertEquals(requestTask.getTaskType(), responseTask.getTaskType());

            // Check the subtasks for this task
            Assertions.assertEquals(requestTask.getSubtasks().size(), capturedTask.getSubtasks().size());
            Assertions.assertEquals(requestTask.getSubtasks().size(), responseTask.getSubtasks().size());

            for (int j = 0; j < requestTask.getSubtasks().size(); j++) {
                Subtask requestSubtask = requestTask.getSubtasks().get(j);
                Subtask capturedSubtask = capturedTask.getSubtasks().get(j);
                Subtask responseSubtask = responseTask.getSubtasks().get(j);

                // Check that the subtask ID is identical to the one in the request
                Assertions.assertEquals(requestSubtask.getId(), capturedSubtask.getId());
                Assertions.assertEquals(requestSubtask.getId(), responseSubtask.getId());

                // Check that other properties are equal
                Assertions.assertEquals(requestSubtask.getName(), capturedSubtask.getName());
                Assertions.assertEquals(requestSubtask.getName(), responseSubtask.getName());

                Assertions.assertEquals(requestSubtask.getDescription(), capturedSubtask.getDescription());
                Assertions.assertEquals(requestSubtask.getDescription(), responseSubtask.getDescription());

                Assertions.assertEquals(requestSubtask.getStatus(), capturedSubtask.getStatus());
                Assertions.assertEquals(requestSubtask.getStatus(), responseSubtask.getStatus());

                Assertions.assertEquals(requestSubtask.getDueDate(), capturedSubtask.getDueDate());
                Assertions.assertEquals(requestSubtask.getDueDate(), responseSubtask.getDueDate());

                Assertions.assertEquals(requestSubtask.getDifficulty(), capturedSubtask.getDifficulty());
                Assertions.assertEquals(requestSubtask.getDifficulty(), responseSubtask.getDifficulty());
            }
        }
    }

    @Test
    public void testUpdateProject_validProjectWithBacklogExistingIds_updatesProject() {
        // GIVEN
        ArgumentCaptor<Project> projectArgumentCaptor = ArgumentCaptor.forClass(Project.class);

        List<Task> originalTasks = getTasksWithId();
        List<Task> backlogTasks = originalTasks.stream()
            .map(task -> {
                Task newTask = new Task.Builder()
                    .withId(task.getId())
                    .withName(task.getName())
                    .withDescription(task.getDescription())
                    .withDueDate(task.getDueDate())
                    .withDifficulty(task.getDifficulty())
                    .withProject(task.getProject())
                    .withSubtasks(task.getSubtasks())
                    .build();

                newTask.setTaskType(Constants.TASK_TYPE_BACKLOG);
                return newTask;
            })
            .toList();

        List<Task> listWithBacklog = getNoIdTasks();
        listWithBacklog.addAll(backlogTasks);

        UpdateProjectRequest updateProjectRequest = new UpdateProjectRequest();
        updateProjectRequest.setId(testIdWithTasks);
        updateProjectRequest.setName(testNameWithTasks);
        updateProjectRequest.setDescription(testDescription);
        updateProjectRequest.setTasks(listWithBacklog);

        Project oldProject = new Project.Builder()
            .withName(testNameWithTasks)
            .withId(testIdWithTasks)
            .withDescription(testDescription)
            .withTasks(originalTasks)
            .build();

        Project updatedProject = new Project.Builder()
            .withName(testNameWithTasks)
            .withId(testIdWithTasks)
            .withDescription(testDescription)
            .withTasks(updateProjectRequest.getTasks())
            .build();

        // WHEN
        when(projectRepository.findById(testIdWithTasks)).thenReturn(Optional.of(oldProject));
        when(projectRepository.save(any())).thenReturn(updatedProject);

        projectService.updateProject(updateProjectRequest);

        verify(projectRepository).save(projectArgumentCaptor.capture());
        Project capturedProject = projectArgumentCaptor.getValue();

        // THEN
        Assertions.assertEquals(oldProject.getName(), updatedProject.getName());
        Assertions.assertEquals(oldProject.getName(), capturedProject.getName());
        Assertions.assertEquals(oldProject.getId(), updatedProject.getId());
        Assertions.assertEquals(oldProject.getId(), capturedProject.getId());
        Assertions.assertEquals(oldProject.getDescription(), updatedProject.getDescription());
        Assertions.assertEquals(oldProject.getDescription(), capturedProject.getDescription());

        // Check Tasks
        for (int i = 0; i < updateProjectRequest.getTasks().size(); i++) {
            Task newTask = updateProjectRequest.getTasks().get(i);
            Task capturedTask = capturedProject.getTasks().get(i);
            Task returnedTask = updatedProject.getTasks().get(i);

            Assertions.assertEquals(newTask, capturedTask);
            Assertions.assertEquals(newTask, returnedTask);
        }
    }

    @Test
    public void testUpdateProject_validProjectWithBacklogWithoutIds_updatesProject() {
        // GIVEN
        ArgumentCaptor<Project> projectArgumentCaptor = ArgumentCaptor.forClass(Project.class);

        List<Task> originalTasks = getTasksWithId();
        originalTasks.addAll(getNoIdBacklogTasks());

        UpdateProjectRequest updateProjectRequest = new UpdateProjectRequest();
        updateProjectRequest.setId(testIdWithTasks);
        updateProjectRequest.setName(testNameWithTasks);
        updateProjectRequest.setDescription(testDescription);
        updateProjectRequest.setTasks(originalTasks);

        Project oldProject = new Project.Builder()
            .withName(testNameWithTasks)
            .withId(testIdWithTasks)
            .withDescription(testDescription)
            .withTasks(originalTasks)
            .withBacklog(new ArrayList<>())
            .build();

        Project updatedProject = new Project.Builder()
            .withName(testNameWithTasks)
            .withId(testIdWithTasks)
            .withDescription(testDescription)
            .withTasks(updateProjectRequest.getTasks())
            .build();

        // WHEN
        when(projectRepository.findById(testIdWithTasks)).thenReturn(Optional.of(oldProject));
        when(projectRepository.save(any())).thenReturn(updatedProject);

        projectService.updateProject(updateProjectRequest);

        verify(projectRepository).save(projectArgumentCaptor.capture());
        Project capturedProject = projectArgumentCaptor.getValue();

        // THEN
        Assertions.assertEquals(oldProject.getName(), updatedProject.getName());
        Assertions.assertEquals(oldProject.getName(), capturedProject.getName());
        Assertions.assertEquals(oldProject.getId(), updatedProject.getId());
        Assertions.assertEquals(oldProject.getId(), capturedProject.getId());
        Assertions.assertEquals(oldProject.getDescription(), updatedProject.getDescription());
        Assertions.assertEquals(oldProject.getDescription(), capturedProject.getDescription());

        // Check Tasks
        for (int i = 0; i < updateProjectRequest.getTasks().size(); i++) {
            Task newTask = updateProjectRequest.getTasks().get(i);
            Task capturedTask = capturedProject.getTasks().get(i);
            Task returnedTask = updatedProject.getTasks().get(i);

            Assertions.assertEquals(Constants.TASK_TYPE_BACKLOG, capturedTask.getTaskType());

            Assertions.assertEquals(newTask, capturedTask);
            Assertions.assertEquals(newTask, returnedTask);

        }
    }

    @Test
    public void testUpdateProject_invalidRequest_throwsInvalidProjectRequestException() {
        // GIVEN
        UpdateProjectRequest updateProjectRequest = new UpdateProjectRequest();
        updateProjectRequest.setId(testId1);
        updateProjectRequest.setName(null);
        updateProjectRequest.setDescription(testDescription);

        // WHEN / THEN
        Assertions.assertThrows(InvalidProjectRequestException.class, () -> projectService.updateProject(updateProjectRequest));
    }

    @Test
    public void testUpdateProject_projectDoesNotExist_throwsProjectNotFoundException() {
        // GIVEN
        UpdateProjectRequest updateProjectRequest = new UpdateProjectRequest();
        updateProjectRequest.setId(testId1);
        updateProjectRequest.setName(testName1);
        updateProjectRequest.setDescription(testDescription);

        when(projectRepository.findById(testId1)).thenReturn(Optional.empty());

        // WHEN / THEN
        Assertions.assertThrows(ProjectNotFoundException.class, () -> projectService.updateProject(updateProjectRequest));
    }

    @Test
    public void testUpdateProject_nullId_throwsInvalidProjectRequestException() {
        // GIVEN
        UpdateProjectRequest updateProjectRequest = new UpdateProjectRequest();
        updateProjectRequest.setId(null);
        updateProjectRequest.setName(testName1);
        updateProjectRequest.setDescription(testDescription);

        // WHEN / THEN
        Assertions.assertThrows(InvalidProjectRequestException.class, () -> projectService.updateProject(updateProjectRequest));
    }

    @Test
    public void testUpdateProject_emptyId_throwsInvalidProjectRequestException() {
        // GIVEN
        UpdateProjectRequest updateProjectRequest = new UpdateProjectRequest();
        updateProjectRequest.setId("");
        updateProjectRequest.setName(testName1);
        updateProjectRequest.setDescription(testDescription);

        // WHEN / THEN
        Assertions.assertThrows(InvalidProjectRequestException.class, () -> projectService.updateProject(updateProjectRequest));
    }

    @Test
    public void testUpdateProject_blankId_throwsInvalidProjectRequestException() {
        // GIVEN
        UpdateProjectRequest updateProjectRequest = new UpdateProjectRequest();
        updateProjectRequest.setId(" ");
        updateProjectRequest.setName(testName1);
        updateProjectRequest.setDescription(testDescription);

        // WHEN / THEN
        Assertions.assertThrows(InvalidProjectRequestException.class, () -> projectService.updateProject(updateProjectRequest));
    }

    @Test
    public void testUpdateProject_nullName_throwsInvalidProjectRequestException() {
        // GIVEN
        UpdateProjectRequest updateProjectRequest = new UpdateProjectRequest();
        updateProjectRequest.setId(testId1);
        updateProjectRequest.setName(null);
        updateProjectRequest.setDescription(testDescription);

        // WHEN / THEN
        Assertions.assertThrows(InvalidProjectRequestException.class, () -> projectService.updateProject(updateProjectRequest));
    }

    @Test
    public void testUpdateProject_emptyName_throwsInvalidProjectRequestException() {
        // GIVEN
        UpdateProjectRequest updateProjectRequest = new UpdateProjectRequest();
        updateProjectRequest.setId(testId1);
        updateProjectRequest.setName("");
        updateProjectRequest.setDescription(testDescription);

        // WHEN / THEN
        Assertions.assertThrows(InvalidProjectRequestException.class, () -> projectService.updateProject(updateProjectRequest));
    }

    @Test
    public void testUpdateProject_blankName_throwsInvalidProjectRequestException() {
        // GIVEN
        UpdateProjectRequest updateProjectRequest = new UpdateProjectRequest();
        updateProjectRequest.setId(testId1);
        updateProjectRequest.setName(" ");
        updateProjectRequest.setDescription(testDescription);

        // WHEN / THEN
        Assertions.assertThrows(InvalidProjectRequestException.class, () -> projectService.updateProject(updateProjectRequest));
    }

    @Test
    public void testUpdateProject_nullDescription_throwsInvalidProjectRequestException() {
        // GIVEN
        UpdateProjectRequest updateProjectRequest = new UpdateProjectRequest();
        updateProjectRequest.setId(testId1);
        updateProjectRequest.setName(testName1);
        updateProjectRequest.setDescription(null);

        // WHEN / THEN
        Assertions.assertThrows(InvalidProjectRequestException.class, () -> projectService.updateProject(updateProjectRequest));
    }

    @Test
    public void testUpdateProject_emptyDescription_throwsInvalidProjectRequestException() {
        // GIVEN
        UpdateProjectRequest updateProjectRequest = new UpdateProjectRequest();
        updateProjectRequest.setId(testId1);
        updateProjectRequest.setName(testName1);
        updateProjectRequest.setDescription("");

        // WHEN / THEN
        Assertions.assertThrows(InvalidProjectRequestException.class, () -> projectService.updateProject(updateProjectRequest));
    }

    @Test
    public void testUpdateProject_blankDescription_throwsInvalidProjectRequestException() {
        // GIVEN
        UpdateProjectRequest updateProjectRequest = new UpdateProjectRequest();
        updateProjectRequest.setId(testId1);
        updateProjectRequest.setName(testName1);
        updateProjectRequest.setDescription(" ");

        // WHEN / THEN
        Assertions.assertThrows(InvalidProjectRequestException.class, () -> projectService.updateProject(updateProjectRequest));
    }

    /**-------------------------------------------- Delete Project ---------------------------------------------------*/
    @Test
    public void testDeleteProject_projectExists_deletesProject() {
        // GIVEN
        when(projectRepository.existsById(testId1)).thenReturn(true);
        doNothing().when(projectRepository).deleteById(testId1);


        // WHEN / THEN
        Assertions.assertDoesNotThrow(() -> projectService.deleteProject(testId1));
    }

    @Test
    public void testDeleteProject_projectDoesNotExist_throwsProjectNotFoundException() {
        // GIVEN
        when(projectRepository.existsById(testId1)).thenReturn(false);

        // WHEN / THEN
        Assertions.assertThrows(ProjectNotFoundException.class, () -> projectService.deleteProject(testId1));
    }

    @Test
    public void testDeleteProject_nullId_throwsInvalidProjectRequestException() {
        // GIVEN/ WHEN / THEN
        Assertions.assertThrows(InvalidProjectRequestException.class, () -> projectService.deleteProject(null));
    }

    @Test
    public void testDeleteProject_blankId_throwsInvalidProjectRequestException() {
        // GIVEN/ WHEN / THEN
        Assertions.assertThrows(InvalidProjectRequestException.class, () -> projectService.deleteProject(" "));
    }

    @Test
    public void testDeleteProject_emptyId_throwsInvalidProjectRequestException() {
        // GIVEN/ WHEN / THEN
        Assertions.assertThrows(InvalidProjectRequestException.class, () -> projectService.deleteProject(""));
    }

    /**------------------------------------------- Utility Methods ---------------------------------------------------*/
    /**
     * Creates a list of Tasks where each Task has no ID
     * Task 1 has 2 subtasks
     * Task 2 has 1 subtask
     * Task 3 has no subtasks
     *
     * @return
     */
    private List<Task> getNoIdTasks() {
        String task1Name = "Test Task 1";
        String task2Name = "Test Task 2";
        String task3Name = "Test Task 3";
        String task1description = "Test Description 1";
        String task2description = "Test Description 2";
        String task3description = "Test Description 3";
        String task1Status = "Not Started";
        String task2Status = "In Progress";
        String task3Status = "Completed";
        LocalDate task1DueDate = LocalDate.parse("2022-01-01");
        LocalDate task2DueDate = LocalDate.parse("2022-01-02");
        LocalDate task3DueDate = LocalDate.parse("2022-01-03");
        String task1Difficulty = "S";
        String task2Difficulty = "M";
        String task3Difficulty = "XL";

        String task1Subtask1Name = "Test Subtask 1";
        String task1Subtask2Name = "Test Subtask 2";
        String task2Subtask1Name = "Test Subtask 3";
        String task1Subtask1Description = "Test Subtask Description 1";
        String task1Subtask2Description = "Test Subtask Description 2";
        String task2Subtask1Description = "Test Subtask Description 3";
        String task1Subtask1Status = "Not Started";
        String task1Subtask2Status = "In Progress";
        String task2Subtask1Status = "Completed";
        LocalDate task1Subtask1DueDate = LocalDate.parse("2022-01-01");
        LocalDate task1Subtask2DueDate = LocalDate.parse("2022-01-02");
        LocalDate task2Subtask1DueDate = LocalDate.parse("2022-01-03");
        String task1Subtask1Difficulty = "S";
        String task1Subtask2Difficulty = "M";
        String task2Subtask1Difficulty = "XL";

        // Create Subtasks and add to Lists
        Subtask task1Subtask1 = new Subtask.Builder()
                .withName(task1Subtask1Name)
                .withDescription(task1Subtask1Description)
                .withStatus(task1Subtask1Status)
                .withDueDate(task1Subtask1DueDate)
                .withDifficulty(task1Subtask1Difficulty)
                .withId(null)
                .build();

        Subtask task1Subtask2 = new Subtask.Builder()
                .withName(task1Subtask2Name)
                .withDescription(task1Subtask2Description)
                .withStatus(task1Subtask2Status)
                .withDueDate(task1Subtask2DueDate)
                .withDifficulty(task1Subtask2Difficulty)
                .withId("")
                .build();

        Subtask task2Subtask1 = new Subtask.Builder()
                .withName(task2Subtask1Name)
                .withDescription(task2Subtask1Description)
                .withStatus(task2Subtask1Status)
                .withDueDate(task2Subtask1DueDate)
                .withDifficulty(task2Subtask1Difficulty)
                .build();

        List<Subtask> task1Subtasks = new ArrayList<>();
        task1Subtasks.add(task1Subtask1);
        task1Subtasks.add(task1Subtask2);

        List<Subtask> task2Subtasks = new ArrayList<>();
        task2Subtasks.add(task2Subtask1);

        // Create Tasks, add subtasks to Tasks 1 and 2, and add Tasks to List
        Task testTask1 = new Task.Builder()
                .withName(task1Name)
                .withDescription(task1description)
                .withStatus(task1Status)
                .withDueDate(task1DueDate)
                .withDifficulty(task1Difficulty)
                .withTaskType(Constants.TASK_TYPE_ACTIVE)
                .withSubtasks(task1Subtasks)
                .withId(null)
                .build();

        Task testTask2 = new Task.Builder()
                .withName(task2Name)
                .withDescription(task2description)
                .withStatus(task2Status)
                .withDueDate(task2DueDate)
                .withDifficulty(task2Difficulty)
                .withTaskType(Constants.TASK_TYPE_ACTIVE)
                .withSubtasks(task2Subtasks)
                .withId("")
                .build();

        Task testTask3 = new Task.Builder()
                .withName(task3Name)
                .withDescription(task3description)
                .withStatus(task3Status)
                .withDueDate(task3DueDate)
                .withDifficulty(task3Difficulty)
                .withTaskType(Constants.TASK_TYPE_ACTIVE)
                .build();

        List<Task> noIdTasks = new ArrayList<>();
        noIdTasks.add(testTask1);
        noIdTasks.add(testTask2);
        noIdTasks.add(testTask3);

        return noIdTasks;
    }

    /**
     * Creates a list of Tasks where each Task has no ID
     * Task 1 has 2 subtasks
     * Task 2 has 1 subtask
     * Task 3 has no subtasks
     *
     * @return
     */
    private List<Task> getTasksWithId() {
        String task1Name = "Test Task 1";
        String task2Name = "Test Task 2";
        String task3Name = "Test Task 3";
        String task1Id = UUID.randomUUID().toString();
        String task2Id = UUID.randomUUID().toString();
        String task3Id = UUID.randomUUID().toString();
        String task1description = "Test Description 1";
        String task2description = "Test Description 2";
        String task3description = "Test Description 3";
        String task1Status = "Not Started";
        String task2Status = "In Progress";
        String task3Status = "Completed";
        LocalDate task1DueDate = LocalDate.parse("2022-01-01");
        LocalDate task2DueDate = LocalDate.parse("2022-01-02");
        LocalDate task3DueDate = LocalDate.parse("2022-01-03");
        String task1Difficulty = "S";
        String task2Difficulty = "M";
        String task3Difficulty = "XL";

        String task1Subtask1Name = "Test Subtask 1";
        String task1Subtask2Name = "Test Subtask 2";
        String task2Subtask1Name = "Test Subtask 3";
        String task1Subtask1Id = UUID.randomUUID().toString();
        String task1Subtask2Id = UUID.randomUUID().toString();
        String task2Subtask1Id = UUID.randomUUID().toString();
        String task1Subtask1Description = "Test Subtask Description 1";
        String task1Subtask2Description = "Test Subtask Description 2";
        String task2Subtask1Description = "Test Subtask Description 3";
        String task1Subtask1Status = "Not Started";
        String task1Subtask2Status = "In Progress";
        String task2Subtask1Status = "Completed";
        LocalDate task1Subtask1DueDate = LocalDate.parse("2022-01-01");
        LocalDate task1Subtask2DueDate = LocalDate.parse("2022-01-02");
        LocalDate task2Subtask1DueDate = LocalDate.parse("2022-01-03");
        String task1Subtask1Difficulty = "S";
        String task1Subtask2Difficulty = "M";
        String task2Subtask1Difficulty = "XL";

        // Create Subtasks and add to Lists
        Subtask task1Subtask1 = new Subtask.Builder()
            .withName(task1Subtask1Name)
            .withDescription(task1Subtask1Description)
            .withStatus(task1Subtask1Status)
            .withDueDate(task1Subtask1DueDate)
            .withDifficulty(task1Subtask1Difficulty)
            .withId(task1Subtask1Id)
            .build();

        Subtask task1Subtask2 = new Subtask.Builder()
            .withName(task1Subtask2Name)
            .withDescription(task1Subtask2Description)
            .withStatus(task1Subtask2Status)
            .withDueDate(task1Subtask2DueDate)
            .withDifficulty(task1Subtask2Difficulty)
            .withId(task1Subtask2Id)
            .build();

        Subtask task2Subtask1 = new Subtask.Builder()
            .withName(task2Subtask1Name)
            .withDescription(task2Subtask1Description)
            .withStatus(task2Subtask1Status)
            .withDueDate(task2Subtask1DueDate)
            .withDifficulty(task2Subtask1Difficulty)
            .withId(task2Subtask1Id)
            .build();

        List<Subtask> task1Subtasks = new ArrayList<>();
        task1Subtasks.add(task1Subtask1);
        task1Subtasks.add(task1Subtask2);

        List<Subtask> task2Subtasks = new ArrayList<>();
        task2Subtasks.add(task2Subtask1);

        // Create Tasks, add subtasks to Tasks 1 and 2, and add Tasks to List
        Task testTask1 = new Task.Builder()
            .withName(task1Name)
            .withDescription(task1description)
            .withStatus(task1Status)
            .withDueDate(task1DueDate)
            .withDifficulty(task1Difficulty)
            .withTaskType(Constants.TASK_TYPE_ACTIVE)
            .withSubtasks(task1Subtasks)
            .withId(task1Id)
            .build();

        Task testTask2 = new Task.Builder()
            .withName(task2Name)
            .withDescription(task2description)
            .withStatus(task2Status)
            .withDueDate(task2DueDate)
            .withDifficulty(task2Difficulty)
            .withTaskType(Constants.TASK_TYPE_ACTIVE)
            .withSubtasks(task2Subtasks)
            .withId(task2Id)
            .build();

        Task testTask3 = new Task.Builder()
            .withName(task3Name)
            .withDescription(task3description)
            .withStatus(task3Status)
            .withDueDate(task3DueDate)
            .withDifficulty(task3Difficulty)
            .withTaskType(Constants.TASK_TYPE_ACTIVE)
            .withId(task3Id)
            .build();

        List<Task> tasksWithId = new ArrayList<>();
        tasksWithId.add(testTask1);
        tasksWithId.add(testTask2);
        tasksWithId.add(testTask3);

        return tasksWithId;
    }

    private List<Task> getNoIdBacklogTasks() {
        String task1Name = "Test Task 1";
        String task2Name = "Test Task 2";
        String task3Name = "Test Task 3";
        String task1description = "Test Description 1";
        String task2description = "Test Description 2";
        String task3description = "Test Description 3";
        String task1Status = "Not Started";
        String task2Status = "In Progress";
        String task3Status = "Completed";
        LocalDate task1DueDate = LocalDate.parse("2022-01-01");
        LocalDate task2DueDate = LocalDate.parse("2022-01-02");
        LocalDate task3DueDate = LocalDate.parse("2022-01-03");
        String task1Difficulty = "S";
        String task2Difficulty = "M";
        String task3Difficulty = "XL";

        String task1Subtask1Name = "Test Subtask 1";
        String task1Subtask2Name = "Test Subtask 2";
        String task2Subtask1Name = "Test Subtask 3";
        String task1Subtask1Id = UUID.randomUUID().toString();
        String task1Subtask2Id = UUID.randomUUID().toString();
        String task2Subtask1Id = UUID.randomUUID().toString();
        String task1Subtask1Description = "Test Subtask Description 1";
        String task1Subtask2Description = "Test Subtask Description 2";
        String task2Subtask1Description = "Test Subtask Description 3";
        String task1Subtask1Status = "Not Started";
        String task1Subtask2Status = "In Progress";
        String task2Subtask1Status = "Completed";
        LocalDate task1Subtask1DueDate = LocalDate.parse("2022-01-01");
        LocalDate task1Subtask2DueDate = LocalDate.parse("2022-01-02");
        LocalDate task2Subtask1DueDate = LocalDate.parse("2022-01-03");
        String task1Subtask1Difficulty = "S";
        String task1Subtask2Difficulty = "M";
        String task2Subtask1Difficulty = "XL";

        // Create Subtasks and add to Lists
        Subtask task1Subtask1 = new Subtask.Builder()
            .withName(task1Subtask1Name)
            .withDescription(task1Subtask1Description)
            .withStatus(task1Subtask1Status)
            .withDueDate(task1Subtask1DueDate)
            .withDifficulty(task1Subtask1Difficulty)
            .withId(task1Subtask1Id)
            .build();

        Subtask task1Subtask2 = new Subtask.Builder()
            .withName(task1Subtask2Name)
            .withDescription(task1Subtask2Description)
            .withStatus(task1Subtask2Status)
            .withDueDate(task1Subtask2DueDate)
            .withDifficulty(task1Subtask2Difficulty)
            .withId(task1Subtask2Id)
            .build();

        Subtask task2Subtask1 = new Subtask.Builder()
            .withName(task2Subtask1Name)
            .withDescription(task2Subtask1Description)
            .withStatus(task2Subtask1Status)
            .withDueDate(task2Subtask1DueDate)
            .withDifficulty(task2Subtask1Difficulty)
            .withId(task2Subtask1Id)
            .build();

        List<Subtask> task1Subtasks = new ArrayList<>();
        task1Subtasks.add(task1Subtask1);
        task1Subtasks.add(task1Subtask2);

        List<Subtask> task2Subtasks = new ArrayList<>();
        task2Subtasks.add(task2Subtask1);

        // Create Tasks, add subtasks to Tasks 1 and 2, and add Tasks to List
        Task testTask1 = new Task.Builder()
            .withName(task1Name)
            .withDescription(task1description)
            .withStatus(task1Status)
            .withDueDate(task1DueDate)
            .withDifficulty(task1Difficulty)
            .withTaskType(Constants.TASK_TYPE_BACKLOG)
            .withSubtasks(task1Subtasks)
            .build();

        Task testTask2 = new Task.Builder()
            .withName(task2Name)
            .withDescription(task2description)
            .withStatus(task2Status)
            .withDueDate(task2DueDate)
            .withDifficulty(task2Difficulty)
            .withTaskType(Constants.TASK_TYPE_BACKLOG)
            .withSubtasks(task2Subtasks)
            .build();

        Task testTask3 = new Task.Builder()
            .withName(task3Name)
            .withDescription(task3description)
            .withStatus(task3Status)
            .withDueDate(task3DueDate)
            .withDifficulty(task3Difficulty)
            .withTaskType(Constants.TASK_TYPE_BACKLOG)
            .build();

        List<Task> tasksWithId = new ArrayList<>();
        tasksWithId.add(testTask1);
        tasksWithId.add(testTask2);
        tasksWithId.add(testTask3);

        return tasksWithId;
    }

    private List<Task> getBacklogTasksWithId() {
        String task1Name = "Test Task 1";
        String task2Name = "Test Task 2";
        String task3Name = "Test Task 3";
        String task1Id = UUID.randomUUID().toString();
        String task2Id = UUID.randomUUID().toString();
        String task3Id = UUID.randomUUID().toString();
        String task1description = "Test Description 1";
        String task2description = "Test Description 2";
        String task3description = "Test Description 3";
        String task1Status = "Not Started";
        String task2Status = "In Progress";
        String task3Status = "Completed";
        LocalDate task1DueDate = LocalDate.parse("2022-01-01");
        LocalDate task2DueDate = LocalDate.parse("2022-01-02");
        LocalDate task3DueDate = LocalDate.parse("2022-01-03");
        String task1Difficulty = "S";
        String task2Difficulty = "M";
        String task3Difficulty = "XL";

        String task1Subtask1Name = "Test Subtask 1";
        String task1Subtask2Name = "Test Subtask 2";
        String task2Subtask1Name = "Test Subtask 3";
        String task1Subtask1Id = UUID.randomUUID().toString();
        String task1Subtask2Id = UUID.randomUUID().toString();
        String task2Subtask1Id = UUID.randomUUID().toString();
        String task1Subtask1Description = "Test Subtask Description 1";
        String task1Subtask2Description = "Test Subtask Description 2";
        String task2Subtask1Description = "Test Subtask Description 3";
        String task1Subtask1Status = "Not Started";
        String task1Subtask2Status = "In Progress";
        String task2Subtask1Status = "Completed";
        LocalDate task1Subtask1DueDate = LocalDate.parse("2022-01-01");
        LocalDate task1Subtask2DueDate = LocalDate.parse("2022-01-02");
        LocalDate task2Subtask1DueDate = LocalDate.parse("2022-01-03");
        String task1Subtask1Difficulty = "S";
        String task1Subtask2Difficulty = "M";
        String task2Subtask1Difficulty = "XL";

        // Create Subtasks and add to Lists
        Subtask task1Subtask1 = new Subtask.Builder()
            .withName(task1Subtask1Name)
            .withDescription(task1Subtask1Description)
            .withStatus(task1Subtask1Status)
            .withDueDate(task1Subtask1DueDate)
            .withDifficulty(task1Subtask1Difficulty)
            .withId(task1Subtask1Id)
            .build();

        Subtask task1Subtask2 = new Subtask.Builder()
            .withName(task1Subtask2Name)
            .withDescription(task1Subtask2Description)
            .withStatus(task1Subtask2Status)
            .withDueDate(task1Subtask2DueDate)
            .withDifficulty(task1Subtask2Difficulty)
            .withId(task1Subtask2Id)
            .build();

        Subtask task2Subtask1 = new Subtask.Builder()
            .withName(task2Subtask1Name)
            .withDescription(task2Subtask1Description)
            .withStatus(task2Subtask1Status)
            .withDueDate(task2Subtask1DueDate)
            .withDifficulty(task2Subtask1Difficulty)
            .withId(task2Subtask1Id)
            .build();

        List<Subtask> task1Subtasks = new ArrayList<>();
        task1Subtasks.add(task1Subtask1);
        task1Subtasks.add(task1Subtask2);

        List<Subtask> task2Subtasks = new ArrayList<>();
        task2Subtasks.add(task2Subtask1);

        // Create Tasks, add subtasks to Tasks 1 and 2, and add Tasks to List
        Task testTask1 = new Task.Builder()
            .withName(task1Name)
            .withDescription(task1description)
            .withStatus(task1Status)
            .withDueDate(task1DueDate)
            .withDifficulty(task1Difficulty)
            .withTaskType(Constants.TASK_TYPE_BACKLOG)
            .withSubtasks(task1Subtasks)
            .withId(task1Id)
            .build();

        Task testTask2 = new Task.Builder()
            .withName(task2Name)
            .withDescription(task2description)
            .withStatus(task2Status)
            .withDueDate(task2DueDate)
            .withDifficulty(task2Difficulty)
            .withTaskType(Constants.TASK_TYPE_BACKLOG)
            .withSubtasks(task2Subtasks)
            .withId(task2Id)
            .build();

        Task testTask3 = new Task.Builder()
            .withName(task3Name)
            .withDescription(task3description)
            .withStatus(task3Status)
            .withDueDate(task3DueDate)
            .withDifficulty(task3Difficulty)
            .withTaskType(Constants.TASK_TYPE_BACKLOG)
            .withId(task3Id)
            .build();

        List<Task> tasksWithId = new ArrayList<>();
        tasksWithId.add(testTask1);
        tasksWithId.add(testTask2);
        tasksWithId.add(testTask3);

        return tasksWithId;
    }
}
