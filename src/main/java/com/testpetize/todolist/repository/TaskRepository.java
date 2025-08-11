package com.testpetize.todolist.repository;

import com.testpetize.todolist.domain.model.Task;
import com.testpetize.todolist.domain.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {

    Page<Task> findByUser(Users user, Pageable pageable);

    List<Task> findByParentTask(Task parentTask);
}
