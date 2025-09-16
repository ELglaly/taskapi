package com.example.taskapi.entity;

import com.example.taskapi.entity.appenum.TaskStatus;
import com.example.taskapi.entity.user.AppUser;
import com.example.taskapi.validation.NoXSS;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Task Entity representing a task in the system
 * - Validation with custom annotations
 * - Auditing with created/modified timestamps and users
 * - Soft delete via archived flag
 * - Optimistic locking with version field
 * - Dynamic updates to optimize SQL operations
 * - Builder pattern for flexible object creation
 */
@Entity
@Table(name = "tasks")
@EntityListeners(AuditingEntityListener.class)
@Getter
@DynamicUpdate
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required and cannot be empty")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    @Pattern(regexp = "^[\\p{L}\\p{N}\\p{P}\\p{Z}]{3,100}$", message = "Title contains invalid characters")
    @NoXSS
    @Column(nullable = false, length = 100)
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @NoXSS
    @Column(length = 500)
    private String description;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 100)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_task_user"))
    private AppUser appUser;

    @Version
    private Long version;

    @Column(name = "is_archived", nullable = false)
    private Boolean archived = false;

    // no-args constructor for JPA
    protected Task() {}

    // Private constructor for builder pattern
    private Task(Builder builder) {
        this.title = builder.title;
        this.description = builder.description;
        this.status = builder.status != null ? builder.status : TaskStatus.OPEN;
        this.appUser = builder.appUser;
        this.archived = false;
    }

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = TaskStatus.OPEN;
        }
        this.archived = false;
    }



    public static class Builder {
        private String title;
        private String description;
        private TaskStatus status;
        private AppUser appUser;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder status(TaskStatus status) {
            this.status = status;
            return this;
        }

        public Builder appUser(AppUser appUser) {
            this.appUser = appUser;
            return this;
        }

        public Task build() {
            return new Task(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}