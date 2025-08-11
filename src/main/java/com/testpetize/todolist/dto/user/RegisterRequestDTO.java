package com.testpetize.todolist.dto.user;

public record RegisterRequestDTO(
        String name,
        String email,
        String password
) {

}
