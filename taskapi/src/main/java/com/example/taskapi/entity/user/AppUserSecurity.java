package com.example.taskapi.entity.user;

import com.example.taskapi.entity.appenum.TaskStatus;
import com.example.taskapi.validation.NoXSS;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Embeddable
public class AppUserSecurity {

    @Column(name = "password_hash", nullable = false)
    @NotBlank(message = "Password hash is required")
    @NoXSS
    private String passwordHash;

}
