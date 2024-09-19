package com.potatobuddy.godotmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.JsonPath;
import com.potatobuddy.godotmanager.dto.project.NewProjectRequest;
import com.potatobuddy.godotmanager.dto.project.UpdateProjectRequest;
import com.potatobuddy.godotmanager.model.Subtask;
import com.potatobuddy.godotmanager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {
    "spring.datasource.url=${mySQLContainer.getJdbcUrl()}",
    "spring.datasource.username=${mySQLContainer.getUsername()}",
    "spring.datasource.password=${mySQLContainer.getPassword()}",
    "spring.jpa.hibernate.ddl-auto=update"
})

@ActiveProfiles("test")
@Testcontainers
@IntegrationTest
public class ProjectControllerTests {
    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private final String TEST_PROJECT_NAME = "Test Project Name";
    private final String TEST_PROJECT_DESCRIPTION = "This is a test description";

    private final String CREATE_PROJECT_ENDPOINT = "/project/newProject";
    private final String UPDATE_PROJECT_ENDPOINT = "/project/updateProject/";

    private Task TEST_TASK = new Task.Builder()
        .withId(UUID.randomUUID().toString())
        .withName("Test Task 1")
        .withDescription("Test Description")
        .withDueDate(LocalDate.now())
        .withDifficulty("M")
        .withStatus("In Progress")
        .withProject(null)
        .build();

    private Subtask TEST_SUBTASK1 = new Subtask.Builder()
        .withId(UUID.randomUUID().toString())
        .withName("Test Subtask 1")
        .withDescription("Test Description")
        .withDueDate(LocalDate.now())
        .withDifficulty("M")
        .withStatus("In Progress")
        .withTask(null)
        .build();

    private Subtask TEST_SUBTASK2 = new Subtask.Builder()
        .withId(UUID.randomUUID().toString())
        .withName("Test Subtask 2")
        .withDescription("Test Description")
        .withDueDate(LocalDate.now())
        .withDifficulty("M")
        .withStatus("In Progress")
        .withTask(null)
        .build();


    // Create MySQL Container Docker Image
    @Container
    public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0.32")
        .withExposedPorts(3306)
        .withDatabaseName("testdb")
        .withUsername("testuser")
        .withPassword("testpassword");

    // Registers properties, sets datasource to test container
    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    @Transactional
    public void createNewProject_validRequest_returnsNewProject() throws Exception {
        // GIVEN
        NewProjectRequest newProjectRequest = new NewProjectRequest();
        newProjectRequest.setName(TEST_PROJECT_NAME);
        newProjectRequest.setDescription(TEST_PROJECT_DESCRIPTION);

        // WHEN
        mockMvc.perform(post(CREATE_PROJECT_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newProjectRequest)))

        // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("project_id").isString())
            .andExpect(jsonPath("project_name").value(TEST_PROJECT_NAME))
            .andExpect(jsonPath("project_description").value(TEST_PROJECT_DESCRIPTION))
            .andExpect(jsonPath("project_tasks").exists());
    }

    @Test
    @Transactional
    public void createNewProject_invalidRequest_returnsBadRequest() throws Exception {
        // GIVEN
        NewProjectRequest newProjectRequest = new NewProjectRequest();
        newProjectRequest.setName(null);
        newProjectRequest.setDescription(TEST_PROJECT_DESCRIPTION);

        // WHEN
        mockMvc.perform(post(CREATE_PROJECT_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newProjectRequest)))

        // THEN
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void updateProject_validRequestWithNewTaskAndSubtasks_updatesProject() throws Exception {
        // GIVEN
        NewProjectRequest newProjectRequest = new NewProjectRequest();
        newProjectRequest.setName(TEST_PROJECT_NAME);
        newProjectRequest.setDescription(TEST_PROJECT_DESCRIPTION);

        MvcResult result = mockMvc.perform(post(CREATE_PROJECT_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProjectRequest)))
            .andExpect(status().isOk())
            .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        String projectId = JsonPath.parse(responseJson).read("$.project_id");

        // WHEN
        Task task1 = new Task.Builder()
            .withName("Test Task 1")
            .withDescription("Test Description")
            .withDueDate(LocalDate.now())
            .withDifficulty("M")
            .withStatus("In Progress")
            .withProject(null)
            .build();

        Subtask subtask1 = new Subtask.Builder()
            .withName("Test Subtask 1")
            .withDescription("Test Description")
            .withDueDate(LocalDate.now())
            .withDifficulty("M")
            .withStatus("In Progress")
            .withTask(null)
            .build();

        Subtask subtask2 = new Subtask.Builder()
            .withName("Test Subtask 2")
            .withDescription("Test Description")
            .withDueDate(LocalDate.now())
            .withDifficulty("M")
            .withStatus("In Progress")
            .withTask(null)
            .build();

        task1.addSubtask(subtask1);
        task1.addSubtask(subtask2);

        UpdateProjectRequest updateProjectRequest = new UpdateProjectRequest();
        updateProjectRequest.setId(projectId);
        updateProjectRequest.setName(TEST_PROJECT_NAME);
        updateProjectRequest.setDescription(TEST_PROJECT_DESCRIPTION);
        updateProjectRequest.setTasks(List.of(TEST_TASK));
        updateProjectRequest.setBacklog(new ArrayList<>());


        // THEN
        mockMvc.perform(put(UPDATE_PROJECT_ENDPOINT + projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProjectRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("project_id").value(projectId))
            .andExpect(jsonPath("project_name").value(TEST_PROJECT_NAME))
            .andExpect(jsonPath("project_description").value(TEST_PROJECT_DESCRIPTION))
            .andExpect(jsonPath("project_tasks[0].name").value(TEST_TASK.getName()));

        UpdateProjectRequest updateProjectRequest2 = new UpdateProjectRequest();
        updateProjectRequest2.setId(projectId);
        updateProjectRequest2.setName(TEST_PROJECT_NAME);
        updateProjectRequest2.setDescription(TEST_PROJECT_DESCRIPTION);
        updateProjectRequest2.setTasks(List.of(TEST_TASK, task1));
        updateProjectRequest2.setBacklog(new ArrayList<>());


        // THEN
        mockMvc.perform(put(UPDATE_PROJECT_ENDPOINT + projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProjectRequest2)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("project_id").value(projectId))
            .andExpect(jsonPath("project_name").value(TEST_PROJECT_NAME))
            .andExpect(jsonPath("project_description").value(TEST_PROJECT_DESCRIPTION))
            .andExpect(jsonPath("project_tasks[1].name").value(TEST_TASK.getName()))
            .andExpect(jsonPath("project_tasks[1].subtasks").isArray())
            .andExpect(jsonPath("project_tasks[1].subtasks[0].id").isString())
            .andExpect(jsonPath("project_tasks[1].subtasks[0].name").value("Test Subtask 1"))
            .andExpect(jsonPath("project_tasks[1].subtasks[1].id").isString())
            .andExpect(jsonPath("project_tasks[1].subtasks[1].name").value("Test Subtask 2"));
    }

    @Test
    @Transactional
    public void updateProject_validRequestWithBacklogTasks_updatesProject() throws Exception {
        // GIVEN
        NewProjectRequest newProjectRequest = new NewProjectRequest();
        newProjectRequest.setName(TEST_PROJECT_NAME);
        newProjectRequest.setDescription(TEST_PROJECT_DESCRIPTION);

        MvcResult result = mockMvc.perform(post(CREATE_PROJECT_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProjectRequest)))
            .andExpect(status().isOk())
            .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        String projectId = JsonPath.parse(responseJson).read("$.project_id");

        // WHEN
        Task task1 = new Task.Builder()
            .withName("Test Task 1")
            .withDescription("Test Description")
            .withDueDate(LocalDate.now())
            .withDifficulty("M")
            .withStatus("In Progress")
            .withProject(null)
            .build();

        Subtask subtask1 = new Subtask.Builder()
            .withName("Test Subtask 1")
            .withDescription("Test Description")
            .withDueDate(LocalDate.now())
            .withDifficulty("M")
            .withStatus("In Progress")
            .withTask(null)
            .build();

        Subtask subtask2 = new Subtask.Builder()
            .withName("Test Subtask 2")
            .withDescription("Test Description")
            .withDueDate(LocalDate.now())
            .withDifficulty("M")
            .withStatus("In Progress")
            .withTask(null)
            .build();

        task1.addSubtask(subtask1);
        task1.addSubtask(subtask2);

        UpdateProjectRequest updateProjectRequest = new UpdateProjectRequest();
        updateProjectRequest.setId(projectId);
        updateProjectRequest.setName(TEST_PROJECT_NAME);
        updateProjectRequest.setDescription(TEST_PROJECT_DESCRIPTION);
        updateProjectRequest.setTasks(List.of(TEST_TASK));
        updateProjectRequest.setBacklog(new ArrayList<>());


        // THEN
        mockMvc.perform(put(UPDATE_PROJECT_ENDPOINT + projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProjectRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("project_id").value(projectId))
            .andExpect(jsonPath("project_name").value(TEST_PROJECT_NAME))
            .andExpect(jsonPath("project_description").value(TEST_PROJECT_DESCRIPTION))
            .andExpect(jsonPath("project_tasks[0].name").value(TEST_TASK.getName()));

        UpdateProjectRequest updateProjectRequest2 = new UpdateProjectRequest();
        updateProjectRequest2.setId(projectId);
        updateProjectRequest2.setName(TEST_PROJECT_NAME);
        updateProjectRequest2.setDescription(TEST_PROJECT_DESCRIPTION);
        updateProjectRequest2.setTasks(List.of(TEST_TASK, task1));
        updateProjectRequest2.setBacklog(new ArrayList<>());


        // THEN
        mockMvc.perform(put(UPDATE_PROJECT_ENDPOINT + projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProjectRequest2)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("project_id").value(projectId))
            .andExpect(jsonPath("project_name").value(TEST_PROJECT_NAME))
            .andExpect(jsonPath("project_description").value(TEST_PROJECT_DESCRIPTION))
            .andExpect(jsonPath("project_tasks[1].name").value(TEST_TASK.getName()))
            .andExpect(jsonPath("project_tasks[1].subtasks").isArray())
            .andExpect(jsonPath("project_tasks[1].subtasks[0].id").isString())
            .andExpect(jsonPath("project_tasks[1].subtasks[0].name").value("Test Subtask 1"))
            .andExpect(jsonPath("project_tasks[1].subtasks[1].id").isString())
            .andExpect(jsonPath("project_tasks[1].subtasks[1].name").value("Test Subtask 2"));

        UpdateProjectRequest updateProjectRequestWithBacklog = new UpdateProjectRequest();
        updateProjectRequestWithBacklog.setId(projectId);
        updateProjectRequestWithBacklog.setName(TEST_PROJECT_NAME);
        updateProjectRequestWithBacklog.setDescription(TEST_PROJECT_DESCRIPTION);
        updateProjectRequestWithBacklog.setTasks(new ArrayList<>());
        updateProjectRequestWithBacklog.setBacklog(List.of(TEST_TASK));

        mockMvc.perform(put(UPDATE_PROJECT_ENDPOINT + projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProjectRequestWithBacklog)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("project_id").value(projectId))
            .andExpect(jsonPath("project_name").value(TEST_PROJECT_NAME))
            .andExpect(jsonPath("project_description").value(TEST_PROJECT_DESCRIPTION))
            .andExpect(jsonPath("project_tasks").isArray())
            .andExpect(jsonPath("project_backlog[0].name").value(TEST_TASK.getName()));
    }
}