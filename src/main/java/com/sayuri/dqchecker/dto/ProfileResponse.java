package com.sayuri.dqchecker.dto;

import com.sayuri.dqchecker.entity.Role;

public class ProfileResponse {

    private Long id;
    private String username;
    private String email;
    private Role role;

    public ProfileResponse(Long id, String username, String email, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}
