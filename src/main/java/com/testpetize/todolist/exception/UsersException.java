package com.testpetize.todolist.exception;

public class UsersException extends RuntimeException{

    public UsersException(String message) {
        super(message);
    }

    public static UsersException userAlreadyExists(String email) {
        return new UsersException("Users with email " + email + " already exists");
    }

    public static UsersException userNotFound() {
        return new UsersException("User not found");
    }

    public static UsersException invalidPassword() {
        return new UsersException("Invalid password");
    }

    public static UsersException invalidEmail() {
        return new UsersException("Invalid email");
    }

    public static UsersException invalidName() {
        return new UsersException("Invalid name");
    }

    public static UsersException sessionNotFound(String tokenJWT) {
        return new UsersException("Session not found for user with token JWT: " + tokenJWT);
    }
}