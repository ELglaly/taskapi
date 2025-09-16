package com.example.taskapi.repository;

import com.example.taskapi.entity.Task;
import com.example.taskapi.entity.appenum.TaskStatus;
import com.example.taskapi.entity.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, Long> {
    boolean existsById(Long id);
    <T> T findById(Long id, Class<T> type);
    void deleteById(Long id);

    @Modifying
    @Query("UPDATE Task t SET t.status = ?2 WHERE t.id = ?1")
    void updateTaskStatusById(Long id, TaskStatus status);
}
