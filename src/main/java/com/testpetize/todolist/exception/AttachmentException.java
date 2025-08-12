package com.testpetize.todolist.exception;

public class AttachmentException extends RuntimeException {
    public AttachmentException(String message) {
        super(message);
    }

    public static AttachmentException fileProcessingError() {
      return new AttachmentException("Error processing uploaded file");
    }
}