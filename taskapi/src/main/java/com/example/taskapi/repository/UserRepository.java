package com.example.taskapi.repository;

import com.example.taskapi.entity.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<AppUser, Long> {
    boolean existsByAppUserContactEmail(String email);
    <T> T findByAppUserContactEmail(String email, Class<T> type);
}
