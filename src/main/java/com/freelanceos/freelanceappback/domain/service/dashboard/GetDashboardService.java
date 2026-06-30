package com.freelanceos.freelanceappback.domain.service.dashboard;

import com.freelanceos.freelanceappback.domain.exception.BadRequestException;
import com.freelanceos.freelanceappback.domain.model.dashboard.Dashboard;
import com.freelanceos.freelanceappback.domain.model.dashboard.DeclarationPeriod;
import com.freelanceos.freelanceappback.domain.model.dashboard.FiscalConfigSettings;
import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStatus;
import com.freelanceos.freelanceappback.domain.model.dashboard.InvoiceSummary;
import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;
import com.freelanceos.freelanceappback.domain.model.dashboard.MissionSummary;
import com.freelanceos.freelanceappback.domain.model.dashboard.MonthlyRevenueAggregate;
import com.freelanceos.freelanceappback.domain.model.dashboard.MonthlyStat;
import com.freelanceos.freelanceappback.domain.model.dashboard.ClientRevenueShare;
import com.freelanceos.freelanceappback.domain.model.dashboard.TaxEstimation;
import com.freelanceos.freelanceappback.domain.ports.in.dashboard.GetDashboardUseCase;
import com.freelanceos.freelanceappback.domain.ports.out.DashboardMetricsRepository;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.DashboardMapper;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GetDashboardService implements GetDashboardUseCase {
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter MONTH_YEAR_FRENCH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH);
    private static final List<InvoiceStatus> MONTHLY_REVENUE_STATUSES = List.of(InvoiceStatus.PAID, InvoiceStatus.SENT);
    private static final List<InvoiceStatus> CLIENT_DISTRIBUTION_STATUSES = List.of(InvoiceStatus.PAID, InvoiceStatus.SENT);

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
    public Dashboard execute(String username) {
        LocalDate today = LocalDate.now();
        LocalDate startOfCurrentMonth = today.withDayOfMonth(1);
        LocalDate startOfNextMonth = startOfCurrentMonth.plusMonths(1);
        LocalDate startOfCurrentYear = today.withDayOfYear(1);
        LocalDate startOfNextYear = startOfCurrentYear.plusYears(1);

        java.util.Optional<Long> userId = resolveUserId(username);
        if (userId.isEmpty()) {
            return buildEmptyDashboard(today, startOfCurrentMonth);
        }

        BigDecimal monthlyTurnover = defaultZero(dashboardMetricsRepository.sumInvoiceTotalHtForStatusesBetween(
                userId.get(), MONTHLY_REVENUE_STATUSES, startOfCurrentMonth, startOfNextMonth));
        BigDecimal annualTurnover = defaultZero(dashboardMetricsRepository.sumInvoiceTotalHtForStatusesBetween(
                userId.get(), List.of(InvoiceStatus.PAID), startOfCurrentYear, startOfNextYear));
        BigDecimal pendingPayments = defaultZero(dashboardMetricsRepository.sumInvoiceTotalHtForStatus(
                userId.get(), InvoiceStatus.SENT));

        List<MonthlyStat> revenueHistory = buildRevenueHistory(userId.get(), startOfCurrentMonth);
        List<ClientRevenueShare> clientDistribution = buildClientDistribution(userId.get(), startOfCurrentYear, startOfNextYear);
        List<InvoiceSummary> overdueInvoices = safeList(dashboardMetricsRepository.findOverdueInvoices(userId.get(), today)).stream()
                .map(invoiceEntity -> dashboardMapper.toDomain(invoiceEntity, today))
                .toList();
        List<MissionSummary> expiringMissions = safeList(dashboardMetricsRepository.findExpiringMissions(
                userId.get(), MissionStatus.ONGOING, today.plusDays(15))).stream()
                .map(dashboardMapper::toDomain)
                .toList();
        TaxEstimation taxEstimation = buildTaxEstimation(userId.get(), today);

        return new Dashboard(
                monthlyTurnover,
                annualTurnover,
                pendingPayments,
                revenueHistory,
                clientDistribution,
                overdueInvoices,
                expiringMissions,
                taxEstimation
        );
    }

    private java.util.Optional<Long> resolveUserId(String username) {
        if (username == null || username.isBlank()) {
            throw new BadRequestException("Username is required");
        }

        return userRepository.findByNameIgnoreCase(username)
                .map(UserEntity::getId);
    }

    private List<MonthlyStat> buildRevenueHistory(Long userId, LocalDate startOfCurrentMonth) {
        LocalDate start = startOfCurrentMonth.minusMonths(11);
        LocalDate endExclusive = startOfCurrentMonth.plusMonths(1);
        List<MonthlyRevenueAggregate> aggregates = safeList(dashboardMetricsRepository.findMonthlyRevenueHistory(
                userId, MONTHLY_REVENUE_STATUSES, start, endExclusive)).stream()
                .map(dashboardMapper::toDomain)
                .toList();

        Map<YearMonth, BigDecimal> paidByMonth = new HashMap<>();
        Map<YearMonth, BigDecimal> sentByMonth = new HashMap<>();
        for (MonthlyRevenueAggregate aggregate : aggregates) {
            YearMonth yearMonth = YearMonth.of(aggregate.year(), aggregate.month());
            if (aggregate.status() == InvoiceStatus.PAID) {
                paidByMonth.put(yearMonth, defaultZero(aggregate.amount()));
            } else if (aggregate.status() == InvoiceStatus.SENT) {
                sentByMonth.put(yearMonth, defaultZero(aggregate.amount()));
            }
        }

        return start.datesUntil(endExclusive, java.time.Period.ofMonths(1))
                .map(YearMonth::from)
                .map(yearMonth -> new MonthlyStat(
                        yearMonth.format(YEAR_MONTH_FORMATTER),
                        paidByMonth.getOrDefault(yearMonth, BigDecimal.ZERO),
                        sentByMonth.getOrDefault(yearMonth, BigDecimal.ZERO)
                ))
                .toList();
    }

    private List<ClientRevenueShare> buildClientDistribution(Long userId, LocalDate startOfCurrentYear, LocalDate startOfNextYear) {
        List<ClientRevenueShare> aggregates = safeList(dashboardMetricsRepository.findClientRevenueDistribution(
                        userId, CLIENT_DISTRIBUTION_STATUSES, startOfCurrentYear, startOfNextYear)).stream()
                .map(dashboardMapper::toDomain)
                .map(share -> new ClientRevenueShare(
                        share.clientName(),
                        defaultZero(share.amount())
                ))
                .toList();

        return getTopFiveClient(aggregates);
    }

    private static @NonNull List<ClientRevenueShare> getTopFiveClient(List<ClientRevenueShare> aggregates) {
        List<ClientRevenueShare> topFive = new java.util.ArrayList<>();
        BigDecimal othersTotal = BigDecimal.ZERO;
        for (int i = 0; i < aggregates.size(); i++) {
            ClientRevenueShare share = aggregates.get(i);
            if (i < 5) {
                topFive.add(share);
            } else {
                othersTotal = othersTotal.add(share.amount());
            }
        }

        if (othersTotal.compareTo(BigDecimal.ZERO) > 0) {
            topFive.add(new ClientRevenueShare("Autres", othersTotal));
        }
        return topFive;
    }

    private TaxEstimation buildTaxEstimation(Long userId, LocalDate today) {
        return dashboardMetricsRepository.findFiscalConfigByUserId(userId)
                .map(dashboardMapper::toDomain)
                .map(config -> calculateTaxFromConfig(userId, today, config))
                .orElseGet(() -> new TaxEstimation(BigDecimal.ZERO, today, "Aucune configuration fiscale"));
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
            return "Estimation fiscale trimestrielle T" + quarter + " " + start.getYear();
        }
        return "Estimation fiscale mensuelle " + start.format(MONTH_YEAR_FRENCH_FORMATTER);
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? java.util.List.of() : list;
    }

    private Dashboard buildEmptyDashboard(LocalDate today, LocalDate startOfCurrentMonth) {
        return new Dashboard(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                buildEmptyRevenueHistory(startOfCurrentMonth),
                java.util.List.of(),
                java.util.List.of(),
                java.util.List.of(),
                buildEmptyTaxEstimation(today)
        );
    }

    private List<MonthlyStat> buildEmptyRevenueHistory(LocalDate startOfCurrentMonth) {
        LocalDate start = startOfCurrentMonth.minusMonths(11);
        LocalDate endExclusive = startOfCurrentMonth.plusMonths(1);
        return start.datesUntil(endExclusive, java.time.Period.ofMonths(1))
                .map(YearMonth::from)
                .map(yearMonth -> new MonthlyStat(
                        yearMonth.format(YEAR_MONTH_FORMATTER),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO
                ))
                .toList();
    }

    private TaxEstimation buildEmptyTaxEstimation(LocalDate today) {
        return new TaxEstimation(BigDecimal.ZERO, today, "Aucune configuration fiscale");
    }

    private record DateRange(LocalDate start, LocalDate endExclusive) {
    }
}
