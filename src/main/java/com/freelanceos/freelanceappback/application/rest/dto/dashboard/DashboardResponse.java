package com.freelanceos.freelanceappback.application.rest.dto.dashboard;

import com.freelanceos.freelanceappback.domain.model.dashboard.InvoiceSummary;
import com.freelanceos.freelanceappback.domain.model.dashboard.MissionSummary;
import com.freelanceos.freelanceappback.domain.model.dashboard.MonthlyStat;
import com.freelanceos.freelanceappback.domain.model.dashboard.TaxEstimation;
import com.freelanceos.freelanceappback.domain.model.dashboard.ClientRevenueShare;

import java.math.BigDecimal;
import java.util.List;

public class DashboardResponse {
    private BigDecimal monthlyTurnover;
    private BigDecimal annualTurnover;
    private BigDecimal pendingPayments;
    private List<MonthlyStat> revenueHistory;
    private List<ClientRevenueShare> clientDistribution;
    private List<InvoiceSummary> overdueInvoices;
    private List<MissionSummary> expiringMissions;
    private TaxEstimation nextTaxDeadline;

    public DashboardResponse() {
    }

    public DashboardResponse(BigDecimal monthlyTurnover,
                             BigDecimal annualTurnover,
                             BigDecimal pendingPayments,
                             List<MonthlyStat> revenueHistory,
                             List<ClientRevenueShare> clientDistribution,
                             List<InvoiceSummary> overdueInvoices,
                             List<MissionSummary> expiringMissions,
                             TaxEstimation nextTaxDeadline) {
        this.monthlyTurnover = monthlyTurnover;
        this.annualTurnover = annualTurnover;
        this.pendingPayments = pendingPayments;
        this.revenueHistory = revenueHistory;
        this.clientDistribution = clientDistribution;
        this.overdueInvoices = overdueInvoices;
        this.expiringMissions = expiringMissions;
        this.nextTaxDeadline = nextTaxDeadline;
    }

    public BigDecimal getMonthlyTurnover() {
        return monthlyTurnover;
    }

    public void setMonthlyTurnover(BigDecimal monthlyTurnover) {
        this.monthlyTurnover = monthlyTurnover;
    }

    public BigDecimal getAnnualTurnover() {
        return annualTurnover;
    }

    public void setAnnualTurnover(BigDecimal annualTurnover) {
        this.annualTurnover = annualTurnover;
    }

    public BigDecimal getPendingPayments() {
        return pendingPayments;
    }

    public void setPendingPayments(BigDecimal pendingPayments) {
        this.pendingPayments = pendingPayments;
    }

    public List<MonthlyStat> getRevenueHistory() {
        return revenueHistory;
    }

    public void setRevenueHistory(List<MonthlyStat> revenueHistory) {
        this.revenueHistory = revenueHistory;
    }

    public List<ClientRevenueShare> getClientDistribution() {
        return clientDistribution;
    }

    public void setClientDistribution(List<ClientRevenueShare> clientDistribution) {
        this.clientDistribution = clientDistribution;
    }

    public List<InvoiceSummary> getOverdueInvoices() {
        return overdueInvoices;
    }

    public void setOverdueInvoices(List<InvoiceSummary> overdueInvoices) {
        this.overdueInvoices = overdueInvoices;
    }

    public List<MissionSummary> getExpiringMissions() {
        return expiringMissions;
    }

    public void setExpiringMissions(List<MissionSummary> expiringMissions) {
        this.expiringMissions = expiringMissions;
    }

    public TaxEstimation getNextTaxDeadline() {
        return nextTaxDeadline;
    }

    public void setNextTaxDeadline(TaxEstimation nextTaxDeadline) {
        this.nextTaxDeadline = nextTaxDeadline;
    }
}
