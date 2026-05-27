package com.sayuri.dqchecker.security;

import com.sayuri.dqchecker.entity.Role;
import com.sayuri.dqchecker.entity.User;
import com.sayuri.dqchecker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityUserDetailsServiceTest {

    @Test
    void loadsUserDetailsWithRoleAuthority() {
        UserRepository userRepository = mock(UserRepository.class);
        User user = new User();
        user.setEmail("admin@example.com");
        user.setPassword("encoded");
        user.setRole(Role.ADMIN);
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        SecurityUserDetailsService service = new SecurityUserDetailsService(userRepository);

        assertEquals("admin@example.com", service.loadUserByUsername("admin@example.com").getUsername());
    }

    @Test
    void throwsWhenUserMissing() {
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        SecurityUserDetailsService service = new SecurityUserDetailsService(userRepository);

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("missing@example.com"));
    }
}
