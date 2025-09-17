package com.example.taskapi.factory;

import com.example.taskapi.entity.Task;
import com.example.taskapi.mapper.TaskMapper;
import com.example.taskapi.request.TaskCreateRequest;
import org.springframework.stereotype.Component;

@Component
public class TaskFactoryImpl implements TaskFactory {
    private final TaskMapper taskMapper;

    public TaskFactoryImpl(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    @Override
    public Task createTask(TaskCreateRequest taskCreateRequest) {
        Task task = taskMapper.toEntity(taskCreateRequest);
        return task;
    }
}