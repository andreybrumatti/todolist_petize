package com.testpetize.todolist.service;

import com.testpetize.todolist.domain.model.Users;
import com.testpetize.todolist.dto.user.LoginRequestDTO;
import com.testpetize.todolist.dto.user.LoginResponseDTO;
import com.testpetize.todolist.dto.user.RegisterRequestDTO;
import com.testpetize.todolist.exception.UsersException;
import com.testpetize.todolist.repository.UsersRepository;
import com.testpetize.todolist.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public void registerUser(RegisterRequestDTO dto) {

        validateUser(dto);

        if (usersRepository.existsByEmail(dto.email())) {
            throw UsersException.userAlreadyExists(dto.email());
        }

        String encodedPassword = passwordEncoder.encode(dto.password());

        Users users = new Users();
        users.setName(dto.name());
        users.setEmail(dto.email());
        users.setPassword(encodedPassword);

        usersRepository.save(users);
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.email(),
                        dto.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtService.generateAcessToken(authentication);

        return new LoginResponseDTO(accessToken);
    }


    private void validateUser(RegisterRequestDTO userDto) {
        if (userDto == null) {
            throw UsersException.userNotFound();
        }

        if (userDto.password() == null || userDto.password().isEmpty()) {
            throw UsersException.invalidPassword();
        }

        if (userDto.email() == null || userDto.email().isEmpty()) {
            throw UsersException.invalidEmail();
        }

        if (userDto.name() == null || userDto.name().isEmpty()) {
            throw UsersException.invalidName();
        }
    }
}
