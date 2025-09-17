package com.example.taskapi.dto;

import com.example.taskapi.entity.Task;
import com.example.taskapi.entity.appenum.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for Task entity
 * @see Task
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto implements Serializable {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
}
