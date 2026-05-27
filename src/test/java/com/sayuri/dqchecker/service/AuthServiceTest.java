package com.sayuri.dqchecker.service;

import com.sayuri.dqchecker.dto.LoginRequest;
import com.sayuri.dqchecker.dto.LoginResponse;
import com.sayuri.dqchecker.dto.ProfileResponse;
import com.sayuri.dqchecker.dto.RegisterRequest;
import com.sayuri.dqchecker.entity.Role;
import com.sayuri.dqchecker.entity.User;
import com.sayuri.dqchecker.exception.UnauthorizedException;
import com.sayuri.dqchecker.repository.UserRepository;
import com.sayuri.dqchecker.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerCreatesUserWithDefaultRole() {
        RegisterRequest request = registerRequest(null);
        when(userRepository.existsByEmail("sayuri@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");

        assertEquals("User registered successfully", authService.register(request).getMessage());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerReturnsMessageForDuplicateEmail() {
        when(userRepository.existsByEmail("sayuri@example.com")).thenReturn(true);

        assertEquals("Email already registered", authService.register(registerRequest("ADMIN")).getMessage());
    }

    @Test
    void loginReturnsJwtToken() {
        User user = user();
        LoginRequest request = loginRequest();
        when(userRepository.findByEmail("sayuri@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("token");

        LoginResponse response = authService.login(request);

        assertEquals("Login successful", response.getMessage());
        assertEquals("token", response.getToken());
    }

    @Test
    void loginRejectsWrongPassword() {
        when(userRepository.findByEmail("sayuri@example.com")).thenReturn(Optional.of(user()));
        when(passwordEncoder.matches("password123", "encoded")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.login(loginRequest()));
    }

    @Test
    void profileReturnsAuthenticatedUser() {
        when(userRepository.findByEmail("sayuri@example.com")).thenReturn(Optional.of(user()));

        ProfileResponse response = authService.profile("sayuri@example.com");

        assertEquals("sayuri", response.getUsername());
        assertEquals("sayuri@example.com", response.getEmail());
        assertEquals(Role.USER, response.getRole());
    }

    private RegisterRequest registerRequest(String role) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("sayuri");
        request.setEmail("sayuri@example.com");
        request.setPassword("password123");
        request.setRole(role);
        return request;
    }

    private LoginRequest loginRequest() {
        LoginRequest request = new LoginRequest();
        request.setEmail("sayuri@example.com");
        request.setPassword("password123");
        return request;
    }

    private User user() {
        User user = new User();
        user.setUsername("sayuri");
        user.setEmail("sayuri@example.com");
        user.setPassword("encoded");
        user.setRole(Role.USER);
        return user;
    }
}
