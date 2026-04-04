package com.freelanceos.freelanceappback.application.rest.dto.dashboard;

import com.freelanceos.freelanceappback.domain.model.dashboard.ClientRevenueShare;
import com.freelanceos.freelanceappback.domain.model.dashboard.InvoiceSummary;
import com.freelanceos.freelanceappback.domain.model.dashboard.MissionSummary;
import com.freelanceos.freelanceappback.domain.model.dashboard.MonthlyStat;
import com.freelanceos.freelanceappback.domain.model.dashboard.TaxEstimation;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(BigDecimal monthlyTurnover,
                                BigDecimal annualTurnover,
                                BigDecimal pendingPayments,
                                List<MonthlyStat> revenueHistory,
                                List<ClientRevenueShare> clientDistribution,
                                List<InvoiceSummary> overdueInvoices,
                                List<MissionSummary> expiringMissions,
                                TaxEstimation nextTaxDeadline) {
}
