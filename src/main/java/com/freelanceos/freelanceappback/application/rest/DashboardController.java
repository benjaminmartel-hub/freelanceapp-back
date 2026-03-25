package com.freelanceos.freelanceappback.application.rest;

import com.freelanceos.freelanceappback.application.rest.dto.dashboard.DashboardResponse;
import com.freelanceos.freelanceappback.application.rest.mapper.DashboardMapperRest;
import com.freelanceos.freelanceappback.domain.ports.in.dashboard.GetDashboardUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private final GetDashboardUseCase getDashboardUseCase;
    private final DashboardMapperRest dashboardMapperRest;

    public DashboardController(GetDashboardUseCase getDashboardUseCase, DashboardMapperRest dashboardMapperRest) {
        this.getDashboardUseCase = getDashboardUseCase;
        this.dashboardMapperRest = dashboardMapperRest;
    }

    @GetMapping("/me")
    @PreAuthorize("#principal != null && #principal.name == authentication.name")
    public DashboardResponse getDashboard(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        try {
            return dashboardMapperRest.toResponse(getDashboardUseCase.execute(principal.getName()));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }
}
