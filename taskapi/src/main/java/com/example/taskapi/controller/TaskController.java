package com.example.taskapi.controller;

import com.example.taskapi.dto.TaskDto;
import com.example.taskapi.request.TaskCreateRequest;
import com.example.taskapi.request.TaskUpdateRequest;
import com.example.taskapi.response.ApiResponse;
import com.example.taskapi.service.task.TaskService;
import com.example.taskapi.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;

    /**
     * Create a new task
     * POST /tasks
     */
    @PostMapping
    public ResponseEntity<ApiResponse> createTask(
            @Valid @RequestBody TaskCreateRequest request) {

        log.info("Creating task for user");

        TaskDto createdTask = taskService.createTask(request);

        log.info("Task created successfully: id={}, title={}",
                createdTask.getId(), createdTask.getTitle() );

        return new ResponseEntity<>(new ApiResponse("Task Created Successfully",createdTask), HttpStatus.CREATED);
    }

    /**
     * Update an existing task
     * PUT /tasks/{taskId}
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskUpdateRequest request) {

        log.info("Updating task {} for user", taskId);

        TaskDto updatedTask = taskService.updateTask(request, taskId);

        log.info("Task updated successfully: id={}, title={}",
                updatedTask.getId(), updatedTask.getTitle());

        return ResponseEntity.ok(new ApiResponse("Task Updated Successfully",updatedTask));
    }

    /**
     * Get all tasks for current user with pagination and sorting
     * GET /tasks?page=0&size=10&sortBy=createdAt&sortDir=desc
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.debug("Getting tasks page: {}, size: {}, sortBy: {}, sortDir: {}",
                page, size, sortBy, sortDir);

        Page<TaskDto> tasks = taskService.getAllTasksForUser(page, size, sortBy, sortDir);

        log.debug("Retrieved {} tasks for user",
                tasks.getTotalElements());

        return ResponseEntity.ok(new ApiResponse("Fetched Successfully",tasks));
    }

    /**
     * Delete a task
     * DELETE tasks/{taskId}
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse> deleteTask(
            @PathVariable Long taskId) {

        log.info("Deleting task {} for user", taskId);

        taskService.deleteTask(taskId);

        log.info("Task {} deleted successfully", taskId);

        return ResponseEntity.ok(new ApiResponse("Task Deleted Successfully",null));
    }
}
