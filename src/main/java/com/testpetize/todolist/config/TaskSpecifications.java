package com.testpetize.todolist.config;

import com.testpetize.todolist.domain.enuns.TaskPriority;
import com.testpetize.todolist.domain.enuns.TaskStatus;
import com.testpetize.todolist.domain.model.Task;
import com.testpetize.todolist.domain.model.Users;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZonedDateTime;

public class TaskSpecifications {
    public static Specification<Task> hasUser(Users user) {
        return (root, query, builder) -> builder.equal(root.get("user"), user);
    }

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, builder) -> status == null ? null : builder.equal(root.get("status"), status);
    }

    public static Specification<Task> hasPriority(TaskPriority priority) {
        return (root, query, builder) -> priority == null ? null : builder.equal(root.get("priority"), priority);
    }

    public static Specification<Task> hasDueDate(ZonedDateTime dueDate) {
        return (root, query, builder) -> dueDate == null ? null : builder.equal(root.get("dueDate"), dueDate);
    }
}