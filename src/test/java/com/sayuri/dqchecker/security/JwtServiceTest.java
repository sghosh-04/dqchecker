package com.sayuri.dqchecker.security;

import com.sayuri.dqchecker.entity.Role;
import com.sayuri.dqchecker.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    @Test
    void generatesAndValidatesToken() {
        JwtService jwtService = new JwtService("mysecretkeymysecretkeymysecretkey1234567890", 86400000);
        User user = new User();
        user.setUsername("sayuri");
        user.setEmail("sayuri@example.com");
        user.setPassword("encoded");
        user.setRole(Role.ADMIN);

        String token = jwtService.generateToken(user);
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("sayuri@example.com")
                .password("encoded")
                .roles("ADMIN")
                .build();

        assertEquals("sayuri@example.com", jwtService.extractEmail(token));
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }
}
