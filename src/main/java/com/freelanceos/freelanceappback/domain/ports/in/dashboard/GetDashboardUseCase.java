package com.freelanceos.freelanceappback.domain.ports.in.dashboard;

import com.freelanceos.freelanceappback.domain.model.dashboard.Dashboard;

public interface GetDashboardUseCase {
    Dashboard execute(String username);
}
