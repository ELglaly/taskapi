package com.example.taskapi.repository;

import com.example.taskapi.entity.Task;
import com.example.taskapi.entity.appenum.TaskStatus;
import com.example.taskapi.entity.user.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    boolean existsById(Long id);
    <T> T findById(@Param("id") Long id, Class<T> projection);
    void deleteById(Long id);

    Page<Task> findByAppUserId(@Param("userId") Long userId, Pageable pageable);
    @Modifying
    @Transactional
    @Query("UPDATE Task t SET t.status = :status WHERE t.id = :id")
    void updateTaskStatusById(@Param("id") Long id,
                              @Param("status") TaskStatus status);
}
