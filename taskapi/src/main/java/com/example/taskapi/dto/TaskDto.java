package com.example.taskapi.dto;

import com.example.taskapi.entity.Task;
import com.example.taskapi.entity.appenum.TaskStatus;

import java.io.Serializable;

/**
 * DTO for Task entity
 * @see Task
 */
public record TaskDto(Long id,
                      String title,
                      String description,
                      TaskStatus status) implements Serializable {
}