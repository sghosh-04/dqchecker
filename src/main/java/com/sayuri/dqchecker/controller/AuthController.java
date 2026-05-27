package com.sayuri.dqchecker.controller;

import com.sayuri.dqchecker.dto.RegisterRequest;
import com.sayuri.dqchecker.dto.RegisterResponse;

import com.sayuri.dqchecker.dto.LoginRequest;
import com.sayuri.dqchecker.dto.LoginResponse;
import com.sayuri.dqchecker.dto.ProfileResponse;

import com.sayuri.dqchecker.service.AuthService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a user")
    public RegisterResponse register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Login and receive a JWT")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request
    ) {
        System.out.println("LOGIN HIT");
        return authService.login(request);
    }

    @GetMapping("/profile")
    @Operation(summary = "Get the authenticated user's profile")
    public ProfileResponse profile(Authentication authentication) {
        return authService.profile(authentication.getName());
    }
}
