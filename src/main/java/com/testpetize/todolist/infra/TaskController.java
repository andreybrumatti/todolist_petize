package com.testpetize.todolist.infra;

import com.testpetize.todolist.domain.enuns.TaskPriority;
import com.testpetize.todolist.domain.enuns.TaskStatus;
import com.testpetize.todolist.dto.task.TaskRequestDTO;
import com.testpetize.todolist.dto.task.TaskResponseDTO;
import com.testpetize.todolist.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Tag(name = "Task", description = "API endpoints for creating, listing, updating, and deleting tasks")
@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/create")
    @Operation(summary = "Create a new task",
            description = "Creates a new task with title, description, due date, status, and priority.")
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO dto) {
        TaskResponseDTO created = taskService.createTask(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/list")
    @Operation(summary = "List tasks with optional filters and pagination",
            description = "Returns a paginated list of tasks belonging to the authenticated user, filtered optionally by status, priority, and due date.")
    public Page<TaskResponseDTO> listTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) ZonedDateTime dueDate,
            @PageableDefault(size = 10, sort = "dueDate") Pageable pageable
    ) {
        return taskService.listTasks(status, priority, dueDate, pageable);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update the status of a task",
            description = "Updates the status of a task identified by its UUID.")
    public ResponseEntity<TaskResponseDTO> updateStatus(
            @PathVariable UUID id,
            @RequestParam TaskStatus status
    ) {
        TaskResponseDTO updated = taskService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Delete a task",
            description = "Deletes the task identified by its UUID.")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
