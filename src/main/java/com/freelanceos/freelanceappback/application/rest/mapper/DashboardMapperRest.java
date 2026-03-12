package com.freelanceos.freelanceappback.application.rest.mapper;

import com.freelanceos.freelanceappback.application.rest.dto.dashboard.DashboardResponse;
import com.freelanceos.freelanceappback.domain.model.dashboard.Dashboard;
import org.springframework.stereotype.Component;

@Component
public class DashboardMapperRest {

    public DashboardResponse toResponse(Dashboard dashboard) {
        return new DashboardResponse(
                dashboard.monthlyTurnover(),
                dashboard.annualTurnover(),
                dashboard.pendingPayments(),
                dashboard.revenueHistory(),
                dashboard.clientDistribution(),
                dashboard.overdueInvoices(),
                dashboard.expiringMissions(),
                dashboard.nextTaxDeadline()
        );
    }
}
