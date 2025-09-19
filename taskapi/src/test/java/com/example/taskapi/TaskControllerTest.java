package com.example.taskapi;
import com.example.taskapi.controller.TaskController;
import com.example.taskapi.dto.TaskDto;
import com.example.taskapi.entity.appenum.TaskStatus;
import com.example.taskapi.exception.TaskNotFoundException;
import com.example.taskapi.request.TaskCreateRequest;
import com.example.taskapi.request.TaskUpdateRequest;
import com.example.taskapi.security.CustomUserDetails;
import com.example.taskapi.security.CustomUserDetailsService;
import com.example.taskapi.security.JwtService;
import com.example.taskapi.service.task.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = TaskController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Import(TaskControllerTest.TestConfig.class)
@ActiveProfiles("test")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskService taskService;

    private CustomUserDetails mockUser;
    private TaskDto sampleTaskDto;
    private TaskCreateRequest createRequest;
    private TaskUpdateRequest updateRequest;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public TaskService taskService() {
            return Mockito.mock(TaskService.class);
        }
        @Bean
        public JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }
        @Bean
        public CustomUserDetailsService customUserDetailsService() {
            return Mockito.mock(CustomUserDetailsService.class);
        }
    }

    @BeforeEach
    void setUp() {
        // Reset the mock before each test
        Mockito.reset(taskService);

        // Mock user setup
        mockUser = CustomUserDetails.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        // Sample task DTO
        sampleTaskDto = TaskDto.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.OPEN)
                .build();

        // Sample create request
        createRequest = TaskCreateRequest.builder()
                .title("New Task")
                .description("New Description")
                .status("open")
                .build();

        // Sample update request
        updateRequest = TaskUpdateRequest.builder()
                .status("DONE")
                .build();
    }

    @Test
    @DisplayName("POST /tasks - success")
    void testCreateTask_ShouldReturnApiResponseAndSuccessMessage_WhenTaskIsValid() throws Exception {
        // Given
        when(taskService.createTask(any(TaskCreateRequest.class)))
                .thenReturn(sampleTaskDto);

        // When & Then
        mockMvc.perform(post("/tasks")
                        .with(user(mockUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Task Created Successfully"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.title").value("Test Task"))
                .andExpect(jsonPath("$.data.description").value("Test Description"))
                .andExpect(jsonPath("$.data.status").value("OPEN"));

        // Verify service was called
        verify(taskService, times(1)).createTask(any(TaskCreateRequest.class));
    }

    @Test
    @DisplayName("POST /tasks - Title Empty")
    void testCreateTask_ShouldReturnValidationErrorMessage_WhenTaskTitleIsEmpty() throws Exception {
        TaskCreateRequest invalid = TaskCreateRequest.builder()
                .title("")
                .description("Desc")
                .status("open")
                .build();

        mockMvc.perform(post("/tasks")
                        .with(user(mockUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.title").exists());

        verify(taskService, never()).createTask(any());
    }

    @ParameterizedTest(name = "{index} - Create Task with title \"{0}\" ")
    @ValueSource(strings = {"s", "ss",
            "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss"
    })
    @DisplayName("POST /tasks - Title Size failure")
    void testCreateTask_ShouldReturnValidationErrorMessage_WhenTaskTitleSizeIsInvalid(String title) throws Exception {
        TaskCreateRequest invalid = TaskCreateRequest.builder()
                .title(title)
                .description("Desc")
                .status("open")
                .build();

        mockMvc.perform(post("/tasks")
                        .with(user(mockUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.title").exists());

        verify(taskService, never()).createTask(any());
    }

    @Test
    @DisplayName("POST /tasks - Title Status failure")
    void testCreateTask_ShouldReturnValidationErrorMessage_WhenTaskStatusIsEmpty() throws Exception {
        TaskCreateRequest invalid = TaskCreateRequest.builder()
                .title("Task")
                .description("Desc")
                .status("")
                .build();

        mockMvc.perform(post("/tasks")
                        .with(user(mockUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.status").exists());

        verify(taskService, never()).createTask(any());
    }

    @Test
    @DisplayName("PUT /tasks/{id} - success")
    void testCreateTask_ShouldReturnApiResponseAndSuccessMessage_WhenTaskStatusIsExists() throws Exception {
        long id = 1L;
        TaskDto updated = TaskDto.builder()
                .id(id)
                .title("Updated Task")
                .description("Updated Description")
                .status(TaskStatus.DONE)
                .build();

        when(taskService.updateTask(any(), eq(id))).thenReturn(updated);

        mockMvc.perform(put("/tasks/{taskId}", id)
                        .with(user(mockUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Task Updated Successfully"))
                .andExpect(jsonPath("$.data.status").value("DONE"));

        verify(taskService).updateTask(any(), eq(id));
    }

    @Test
    @DisplayName("PUT /tasks/{id} - not found")
    void testUpdateTask_ShouldReturnTaskNotFoundException_WhenTaskDoesNotExist() throws Exception {
        long id = 999L;
        when(taskService.updateTask(any(), eq(id)))
                .thenThrow(new TaskNotFoundException());

        mockMvc.perform(put("/tasks/{taskId}", id)
                        .with(user(mockUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("TASK_NOT_FOUND"));

        verify(taskService).updateTask(any(), eq(id));
    }

    @Test
    @DisplayName("GET /tasks - success")
    void testGetAllTasks_ShouldReturnApiResponseAndSuccessMessage_WhenTasksExist() throws Exception {
        List<TaskDto> list = List.of(
                sampleTaskDto,
                TaskDto.builder()
                        .id(2L)
                        .title("Second")
                        .description("Desc2")
                        .status(TaskStatus.DONE)
                        .build()
        );
        Page<TaskDto> page = new PageImpl<>(list);
        when(taskService.getAllTasksForUser(0,10,"createdAt","desc"))
                .thenReturn(page);

        mockMvc.perform(get("/tasks")
                        .with(user(mockUser))
                        .param("page","0")
                        .param("size","10")
                        .param("sortBy","createdAt")
                        .param("sortDir","desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.totalElements").value(2));

        verify(taskService).getAllTasksForUser(0,10,"createdAt","desc");
    }

    @Test
    @DisplayName("GET /tasks - defaults")
    void testGetAllTasks_Defaults() throws Exception {
        when(taskService.getAllTasksForUser(0,10,"createdAt","desc"))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/tasks").with(user(mockUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(0)))
                .andExpect(jsonPath("$.data.totalElements").value(0));

        verify(taskService).getAllTasksForUser(0,10,"createdAt","desc");
    }

    @Test
    @DisplayName("DELETE /tasks/{id} - success")
    @WithMockUser("testuser")
    void testDeleteTask_Success() throws Exception {
        long id = 1L;
        doNothing().when(taskService).deleteTask(id);

        mockMvc.perform(delete("/tasks/{taskId}", id).with(user(mockUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Task Deleted Successfully"));

        verify(taskService).deleteTask(id);
    }

    @Test
    @DisplayName("DELETE /tasks/{id} - not found")
    @WithMockUser("testuser")
    void testDeleteTask_NotFound() throws Exception {
        long id = 999L;
        doThrow(new TaskNotFoundException()).when(taskService).deleteTask(id);

        mockMvc.perform(delete("/tasks/{taskId}", id).with(user(mockUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("TASK_NOT_FOUND"));

        verify(taskService).deleteTask(id);
    }

    @Test
    @DisplayName("DELETE /tasks/{id} - access denied")
    @WithMockUser("testuser")
    void testDeleteTask_testCreateTask_ShouldReturnAccessDeniedException_WhenUserCantAccess() throws Exception {
        long id = 1L;
        doThrow(new AccessDeniedException("denied")).when(taskService).deleteTask(id);

        mockMvc.perform(delete("/tasks/{taskId}", id).with(user(mockUser)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));

        verify(taskService).deleteTask(id);
    }

}
