package com.example.taskapi.service.task;

import com.example.taskapi.dto.TaskDto;
import com.example.taskapi.entity.Task;
import com.example.taskapi.entity.appenum.TaskStatus;
import com.example.taskapi.entity.user.AppUser;
import com.example.taskapi.exception.TaskNotFoundException;
import com.example.taskapi.factory.TaskFactory;
import com.example.taskapi.mapper.TaskMapper;
import com.example.taskapi.repository.TaskRepository;
import com.example.taskapi.repository.UserRepository;
import com.example.taskapi.request.TaskCreateRequest;
import com.example.taskapi.request.TaskUpdateRequest;
import com.example.taskapi.validation.TaskValidation;
import com.example.taskapi.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskFactory taskFactory;
    private final TaskValidation taskValidation;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TaskDto createTask(TaskCreateRequest request) {
        log.info("Validating create request: {}", request);
        taskValidation.validateTaskCreateRequest(request);

        AppUser currentUser = userRepository.getReferenceById(getCurrentUser().getId());
        Task task = taskFactory.createTask(request);
        task.setAppUser(currentUser);

        Task saved = taskRepository.save(task);
        log.info("Task created with ID {}", saved.getId());
        return taskMapper.toDto(saved);
    }

    @Override
    @Transactional
    public TaskDto updateTask(TaskUpdateRequest request, Long taskId) {
        log.info("Updating task {} with data {}", taskId, request);

        Task existing = taskRepository.findById(taskId)
                .orElseThrow(TaskNotFoundException::new);

        CustomUserDetails userDetails = getCurrentUser();
        if (taskId.equals(userDetails.getId())) {
            throw new AccessDeniedException("Cannot update another user’s task");
        }

        Task saved = taskRepository.save(existing);
        log.info("Task {} updated", saved.getId());
        return taskMapper.toDto(saved);
    }

    @Override
    @Transactional()
    public Page<TaskDto> getAllTasksForUser(int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        return taskRepository.findByAppUserId(getCurrentUser().getId(),
                PageRequest.of(page, size, sort),TaskDto.class);
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId) {
        log.info("Deleting task {}", taskId);

        taskRepository.findById(taskId).ifPresentOrElse(
                t -> {
                    requireOwn(t.getAppUser().getId());
                    taskRepository.delete(t);
                    log.info("Deleted task {}", taskId);
                },
                () -> { throw new TaskNotFoundException(); }
        );
    }

    // ------------------- Helpers -------------------

    private CustomUserDetails getCurrentUser() {
        return  (CustomUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private void requireOwn(Long ownerId) {
        CustomUserDetails user = getCurrentUser();
        if (!user.getId().equals(ownerId)) {
            throw new AccessDeniedException("Access denied");
        }
    }

}
