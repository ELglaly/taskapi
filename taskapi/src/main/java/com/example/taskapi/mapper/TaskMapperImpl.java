package com.example.taskapi.mapper;


import com.example.taskapi.dto.TaskDto;
import com.example.taskapi.entity.Task;
import com.example.taskapi.request.TaskCreateRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TaskMapperImpl implements TaskMapper{
    private final ModelMapper modelMapper;

    public TaskMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }


    @Override
    public TaskDto toDto(Task task) {
        return modelMapper.map(task,TaskDto.class);
    }

    @Override
    public Task toEntity(TaskDto taskDto) {
        return modelMapper.map(taskDto,Task.class);
    }

    @Override
    public Task toEntity(TaskCreateRequest taskCreateRequest) {
        return modelMapper.map(taskCreateRequest,Task.class);
    }
}
