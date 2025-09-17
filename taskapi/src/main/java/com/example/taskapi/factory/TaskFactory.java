package com.example.taskapi.factory;

import com.example.taskapi.entity.Task;
import com.example.taskapi.request.TaskCreateRequest;
import org.springframework.stereotype.Component;

public interface TaskFactory {

    Task createTask(TaskCreateRequest taskCreateRequest);
}