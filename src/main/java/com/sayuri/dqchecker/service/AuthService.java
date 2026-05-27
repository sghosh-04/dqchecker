package com.sayuri.dqchecker.service;

import com.sayuri.dqchecker.dto.RegisterRequest;
import com.sayuri.dqchecker.dto.RegisterResponse;
import com.sayuri.dqchecker.entity.Role;
import com.sayuri.dqchecker.entity.User;
import com.sayuri.dqchecker.exception.UnauthorizedException;
import com.sayuri.dqchecker.repository.UserRepository;
import com.sayuri.dqchecker.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sayuri.dqchecker.dto.LoginRequest;
import com.sayuri.dqchecker.dto.LoginResponse;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return new RegisterResponse("Email already registered");
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(resolveRole(request.getRole()));

        userRepository.save(user);
        log.info("Registered user with email {}", user.getEmail());

        return new RegisterResponse("User registered successfully");
    }


    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);
        log.info("User logged in with email {}", user.getEmail());

        return new LoginResponse("Login successful", token);
    }

    private Role resolveRole(String role) {
        if (role == null || role.isBlank()) {
            return Role.USER;
        }
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return Role.USER;
        }
    }
}


