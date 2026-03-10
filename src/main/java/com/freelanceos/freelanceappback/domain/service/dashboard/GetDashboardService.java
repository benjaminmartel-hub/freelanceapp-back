package com.freelanceos.freelanceappback.domain.service.dashboard;

import com.freelanceos.freelanceappback.domain.model.dashboard.Dashboard;
import com.freelanceos.freelanceappback.domain.model.dashboard.DeclarationPeriod;
import com.freelanceos.freelanceappback.domain.model.dashboard.FiscalConfigSettings;
import com.freelanceos.freelanceappback.domain.model.dashboard.InvoiceStatus;
import com.freelanceos.freelanceappback.domain.model.dashboard.InvoiceSummary;
import com.freelanceos.freelanceappback.domain.model.dashboard.MissionStatus;
import com.freelanceos.freelanceappback.domain.model.dashboard.MissionSummary;
import com.freelanceos.freelanceappback.domain.model.dashboard.MonthlyRevenueAggregate;
import com.freelanceos.freelanceappback.domain.model.dashboard.MonthlyStat;
import com.freelanceos.freelanceappback.domain.model.dashboard.TaxEstimation;
import com.freelanceos.freelanceappback.domain.ports.in.dashboard.GetDashboardUseCase;
import com.freelanceos.freelanceappback.domain.ports.out.DashboardMetricsRepository;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.DashboardMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GetDashboardService implements GetDashboardUseCase {
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final List<InvoiceStatus> MONTHLY_REVENUE_STATUSES = List.of(InvoiceStatus.PAID, InvoiceStatus.SENT);

    private final DashboardMetricsRepository dashboardMetricsRepository;
    private final UserRepository userRepository;
    private final DashboardMapper dashboardMapper;

    public GetDashboardService(DashboardMetricsRepository dashboardMetricsRepository,
                               UserRepository userRepository,
                               DashboardMapper dashboardMapper) {
        this.dashboardMetricsRepository = dashboardMetricsRepository;
        this.userRepository = userRepository;
        this.dashboardMapper = dashboardMapper;
    }

    @Override
    public Dashboard execute(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        LocalDate today = LocalDate.now();
        LocalDate startOfCurrentMonth = today.withDayOfMonth(1);
        LocalDate startOfNextMonth = startOfCurrentMonth.plusMonths(1);
        LocalDate startOfCurrentYear = today.withDayOfYear(1);
        LocalDate startOfNextYear = startOfCurrentYear.plusYears(1);

        BigDecimal monthlyTurnover = defaultZero(dashboardMetricsRepository.sumInvoiceTotalHtForStatusesBetween(
                userId, MONTHLY_REVENUE_STATUSES, startOfCurrentMonth, startOfNextMonth));
        BigDecimal annualTurnover = defaultZero(dashboardMetricsRepository.sumInvoiceTotalHtForStatusesBetween(
                userId, List.of(InvoiceStatus.PAID), startOfCurrentYear, startOfNextYear));
        BigDecimal pendingPayments = defaultZero(dashboardMetricsRepository.sumInvoiceTotalHtForStatus(
                userId, InvoiceStatus.SENT));

        List<MonthlyStat> revenueHistory = buildRevenueHistory(userId, startOfCurrentMonth);
        List<InvoiceSummary> overdueInvoices = dashboardMetricsRepository.findOverdueInvoices(userId, today).stream()
                .map(invoiceEntity -> dashboardMapper.toDomain(invoiceEntity, today))
                .toList();
        List<MissionSummary> expiringMissions = dashboardMetricsRepository.findExpiringMissions(
                userId, MissionStatus.ACTIVE, today.plusDays(15)).stream()
                .map(dashboardMapper::toDomain)
                .toList();
        TaxEstimation taxEstimation = buildTaxEstimation(userId, today);

        return new Dashboard(
                monthlyTurnover,
                annualTurnover,
                pendingPayments,
                revenueHistory,
                overdueInvoices,
                expiringMissions,
                taxEstimation
        );
    }

    private List<MonthlyStat> buildRevenueHistory(Long userId, LocalDate startOfCurrentMonth) {
        LocalDate start = startOfCurrentMonth.minusMonths(11);
        LocalDate endExclusive = startOfCurrentMonth.plusMonths(1);
        List<MonthlyRevenueAggregate> aggregates = dashboardMetricsRepository.findMonthlyRevenueHistory(
                userId, MONTHLY_REVENUE_STATUSES, start, endExclusive).stream()
                .map(dashboardMapper::toDomain)
                .toList();

        Map<YearMonth, BigDecimal> byMonth = new HashMap<>();
        for (MonthlyRevenueAggregate aggregate : aggregates) {
            byMonth.put(YearMonth.of(aggregate.year(), aggregate.month()), defaultZero(aggregate.amount()));
        }

        return start.datesUntil(endExclusive, java.time.Period.ofMonths(1))
                .map(YearMonth::from)
                .map(yearMonth -> new MonthlyStat(
                        yearMonth.format(YEAR_MONTH_FORMATTER),
                        byMonth.getOrDefault(yearMonth, BigDecimal.ZERO)
                ))
                .toList();
    }

    private TaxEstimation buildTaxEstimation(Long userId, LocalDate today) {
        return dashboardMetricsRepository.findFiscalConfigByUserId(userId)
                .map(dashboardMapper::toDomain)
                .map(config -> calculateTaxFromConfig(userId, today, config))
                .orElseGet(() -> new TaxEstimation(BigDecimal.ZERO, today, "No fiscal configuration"));
    }

    private TaxEstimation calculateTaxFromConfig(Long userId, LocalDate today, FiscalConfigSettings config) {
        DateRange dateRange = periodRange(today, config.declarationPeriod());
        BigDecimal taxableBase = defaultZero(dashboardMetricsRepository.sumInvoiceTotalHtForStatusesBetween(
                userId, List.of(InvoiceStatus.PAID), dateRange.start(), dateRange.endExclusive()));
        BigDecimal amountToPay = config.vatEnabled()
                ? taxableBase.multiply(config.taxRate())
                : BigDecimal.ZERO;

        return new TaxEstimation(
                amountToPay,
                dateRange.endExclusive().plusDays(15),
                buildTaxLabel(config.declarationPeriod(), dateRange.start())
        );
    }

    private DateRange periodRange(LocalDate today, DeclarationPeriod declarationPeriod) {
        if (declarationPeriod == DeclarationPeriod.QUARTERLY) {
            int quarterStartMonth = ((today.getMonthValue() - 1) / 3) * 3 + 1;
            LocalDate start = LocalDate.of(today.getYear(), quarterStartMonth, 1);
            return new DateRange(start, start.plusMonths(3));
        }

        LocalDate start = today.withDayOfMonth(1);
        return new DateRange(start, start.plusMonths(1));
    }

    private String buildTaxLabel(DeclarationPeriod declarationPeriod, LocalDate start) {
        if (declarationPeriod == DeclarationPeriod.QUARTERLY) {
            int quarter = ((start.getMonthValue() - 1) / 3) + 1;
            return "Quarterly tax estimation Q" + quarter + " " + start.getYear();
        }
        return "Monthly tax estimation " + start.format(YEAR_MONTH_FORMATTER);
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private record DateRange(LocalDate start, LocalDate endExclusive) {
    }
}
