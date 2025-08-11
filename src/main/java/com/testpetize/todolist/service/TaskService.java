package com.testpetize.todolist.service;

import com.testpetize.todolist.config.TaskSpecifications;
import com.testpetize.todolist.domain.enuns.TaskPriority;
import com.testpetize.todolist.domain.enuns.TaskStatus;
import com.testpetize.todolist.domain.model.Task;
import com.testpetize.todolist.domain.model.Users;
import com.testpetize.todolist.dto.task.TaskRequestDTO;
import com.testpetize.todolist.dto.task.TaskResponseDTO;
import com.testpetize.todolist.exception.TaskException;
import com.testpetize.todolist.exception.UsersException;
import com.testpetize.todolist.repository.TaskRepository;
import com.testpetize.todolist.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UsersRepository userRepository;

    private Users getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(UsersException::userNotFound);
    }

    @Transactional
    public TaskResponseDTO createTask(TaskRequestDTO dto) {
        Task task = toEntity(dto);
        task.setUser(getAuthenticatedUser());

        if (dto.parentTaskId() != null) {
            Task parent = taskRepository.findById(dto.parentTaskId())
                    .orElseThrow(TaskException::taskNotFound);
            task.setParentTask(parent);
        }

        Task saved = taskRepository.save(task);
        return toDTO(saved);
    }

    private Specification<Task> safeAnd(Specification<Task> spec, Specification<Task> newSpec) {
        if (newSpec == null) return spec;
        return (spec == null) ? newSpec : spec.and(newSpec);
    }

    @Transactional
    public Page<TaskResponseDTO> listTasks(TaskStatus status, TaskPriority priority, ZonedDateTime dueDate, Pageable pageable) {
        Users user = getAuthenticatedUser();

        Specification<Task> spec = null;
        spec = safeAnd(spec, TaskSpecifications.hasUser(user));
        spec = safeAnd(spec, TaskSpecifications.hasStatus(status));
        spec = safeAnd(spec, TaskSpecifications.hasPriority(priority));
        spec = safeAnd(spec, TaskSpecifications.hasDueDate(dueDate));

        Page<Task> page = taskRepository.findAll(spec, pageable);
        return page.map(this::toDTO);
    }

    @Transactional
    public TaskResponseDTO updateStatus(UUID taskId, TaskStatus newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(TaskException::taskNotFound);

        if (newStatus == TaskStatus.COMPLETED) {
            List<Task> subtasks = taskRepository.findByParentTask(task);
            boolean hasPendingSubtasks = subtasks.stream()
                    .anyMatch(st -> st.getStatus() != TaskStatus.COMPLETED);
            if (hasPendingSubtasks) {
                throw TaskException.pendingSubtasksException();
            }
        }

        task.setStatus(newStatus);
        Task updated = taskRepository.save(task);

        return toDTO(updated);
    }

    @Transactional
    public void deleteTask(UUID taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw TaskException.taskNotFound();
        }
        taskRepository.deleteById(taskId);
    }

    public TaskResponseDTO toDTO(Task task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getStatus(),
                task.getPriority(),
                task.getParentTask() != null ? task.getParentTask().getId() : null
        );
    }

    public Task toEntity(TaskRequestDTO dto) {
        Task task = new Task();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setDueDate(dto.dueDate());
        task.setStatus(dto.status());
        task.setPriority(dto.priority());

        return task;
    }
}
