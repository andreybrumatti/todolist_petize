package com.testpetize.todolist.dto.task;

import com.testpetize.todolist.domain.enuns.TaskPriority;
import com.testpetize.todolist.domain.enuns.TaskStatus;

import java.time.ZonedDateTime;
import java.util.UUID;

public record TaskResponseDTO(
        UUID id,
        String title,
        String description,
        ZonedDateTime dueDate,
        TaskStatus status,
        TaskPriority priority,
        UUID parentTaskId
) {
}
