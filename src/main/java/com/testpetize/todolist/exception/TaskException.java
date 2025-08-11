package com.testpetize.todolist.exception;

public class TaskException extends RuntimeException {
    public TaskException(String message) {
        super(message);
    }

  public static TaskException taskNotFound() {
    return new TaskException("Task not found");
  }

  public static TaskException pendingSubtasksException() {
      return new TaskException("Cannot complete task with pending subtasks");
  }
}
