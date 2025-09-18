package com.example.taskapi.factory;

import com.example.taskapi.entity.Task;
import com.example.taskapi.entity.appenum.TaskStatus;
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
        if(taskCreateRequest.getStatus().equalsIgnoreCase("open"))
        {
            task.setStatus(TaskStatus.OPEN);
        }
        else if(taskCreateRequest.getStatus().equalsIgnoreCase("done"))
        {
            task.setStatus(TaskStatus.DONE);
        }
        return task;
    }
}