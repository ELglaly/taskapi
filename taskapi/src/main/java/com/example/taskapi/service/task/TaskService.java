package com.example.taskapi.service.task;

import com.example.taskapi.dto.TaskDto;
import com.example.taskapi.entity.Task;
import com.example.taskapi.request.TaskCreateRequest;
import com.example.taskapi.request.TaskUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface TaskService {
    TaskDto createTask(TaskCreateRequest  taskCreateRequest);
    TaskDto updateTask(TaskUpdateRequest taskUpdateRequest, Long taskId);
    Page<TaskDto> getAllTasksForUser(int page, int size, String sortBy,String sortDir);
    void deleteTask(Long taskId);
}
