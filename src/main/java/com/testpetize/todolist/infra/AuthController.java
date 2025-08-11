package com.testpetize.todolist.infra;

import com.testpetize.todolist.dto.user.LoginRequestDTO;
import com.testpetize.todolist.dto.user.LoginResponseDTO;
import com.testpetize.todolist.dto.user.RegisterRequestDTO;
import com.testpetize.todolist.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users", description = "API endpoints for user registration and login")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(description = "Register a new user in the system")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {

        authService.registerUser(registerRequestDTO);
        return ResponseEntity.ok("Users registered successfully");
    }

    @PostMapping("/login")
    @Operation(description = "Log the user into the system")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO, HttpServletResponse response) {

        LoginResponseDTO loginResponseDTO = authService.login(loginRequestDTO, response);
        return ResponseEntity.ok(loginResponseDTO);
    }
}
