package com.sayuri.dqchecker.controller;

import com.sayuri.dqchecker.dto.DashboardResponse;
import com.sayuri.dqchecker.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard metrics")
    public DashboardResponse dashboard(Authentication authentication) {
        return dashboardService.getDashboard(authentication.getName(), isAdmin(authentication));
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }
}
