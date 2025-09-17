package com.example.taskapi.mapper;

import com.example.taskapi.dto.TaskDto;
import com.example.taskapi.entity.Task;
import com.example.taskapi.request.TaskCreateRequest;

public interface TaskMapper {

    TaskDto toDto(Task task);
    Task toEntity(TaskDto taskDto);
    Task toEntity(TaskCreateRequest taskCreateRequest);

}
