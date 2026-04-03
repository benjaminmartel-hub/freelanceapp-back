package com.freelanceos.freelanceappback.application.rest;

import com.freelanceos.freelanceappback.application.rest.dto.dashboard.DashboardResponse;
import com.freelanceos.freelanceappback.application.rest.mapper.DashboardMapperRest;
import com.freelanceos.freelanceappback.domain.exception.BadRequestException;
import com.freelanceos.freelanceappback.domain.exception.NotFoundException;
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
    private final AuthenticatedUserResolver authenticatedUserResolver;

    public DashboardController(GetDashboardUseCase getDashboardUseCase,
                               DashboardMapperRest dashboardMapperRest,
                               AuthenticatedUserResolver authenticatedUserResolver) {
        this.getDashboardUseCase = getDashboardUseCase;
        this.dashboardMapperRest = dashboardMapperRest;
        this.authenticatedUserResolver = authenticatedUserResolver;
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public DashboardResponse getDashboard(Principal principal) {
        String username = authenticatedUserResolver.resolve(principal);
        try {
            return dashboardMapperRest.toResponse(getDashboardUseCase.execute(username));
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (BadRequestException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
