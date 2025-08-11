package com.testpetize.todolist.domain.model;

import com.testpetize.todolist.domain.enuns.TaskPriority;
import com.testpetize.todolist.domain.enuns.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task_tb")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    @Column(nullable = false)
    private String title;

    @Size(max = 500, message = "Description can have max 500 characters")
    private String description;

    @FutureOrPresent(message = "Due date must be today or in the future")
    private ZonedDateTime dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentTask")
    private List<Task> subTasks;

    @ManyToOne
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    @PrePersist
    public void prePersist() {
        if (status == null) status = TaskStatus.PENDING;
        if (priority == null) priority = TaskPriority.MEDIUM;
    }
}
