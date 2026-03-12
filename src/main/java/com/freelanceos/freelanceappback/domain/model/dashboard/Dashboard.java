package com.freelanceos.freelanceappback.domain.model.dashboard;

import java.math.BigDecimal;
import java.util.List;

public record Dashboard(
        BigDecimal monthlyTurnover,
        BigDecimal annualTurnover,
        BigDecimal pendingPayments,
        List<MonthlyStat> revenueHistory,
        List<ClientRevenueShare> clientDistribution,
        List<InvoiceSummary> overdueInvoices,
        List<MissionSummary> expiringMissions,
        TaxEstimation nextTaxDeadline
) {
}
