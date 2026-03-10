package com.freelanceos.freelanceappback.application.rest;

import com.freelanceos.freelanceappback.application.rest.dto.dashboard.DashboardResponse;
import com.freelanceos.freelanceappback.application.rest.mapper.DashboardMapperRest;
import com.freelanceos.freelanceappback.domain.ports.in.dashboard.GetDashboardUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private final GetDashboardUseCase getDashboardUseCase;
    private final DashboardMapperRest dashboardMapperRest;

    public DashboardController(GetDashboardUseCase getDashboardUseCase, DashboardMapperRest dashboardMapperRest) {
        this.getDashboardUseCase = getDashboardUseCase;
        this.dashboardMapperRest = dashboardMapperRest;
    }

    @GetMapping("/{userId}")
    public DashboardResponse getDashboard(@PathVariable Long userId) {
        try {
            return dashboardMapperRest.toResponse(getDashboardUseCase.execute(userId));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }
}
