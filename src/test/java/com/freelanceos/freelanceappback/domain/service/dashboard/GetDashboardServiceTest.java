package com.freelanceos.freelanceappback.domain.service.dashboard;

import com.freelanceos.freelanceappback.domain.model.dashboard.ClientRevenueShare;
import com.freelanceos.freelanceappback.domain.model.dashboard.Dashboard;
import com.freelanceos.freelanceappback.domain.model.dashboard.InvoiceStatus;
import com.freelanceos.freelanceappback.domain.model.dashboard.MonthlyStat;
import com.freelanceos.freelanceappback.domain.ports.out.DashboardMetricsRepository;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.DashboardMapper;
import com.freelanceos.freelanceappback.infrastructure.persistence.projection.ClientRevenueAggregateProjection;
import com.freelanceos.freelanceappback.infrastructure.persistence.projection.MonthlyRevenueAggregateProjection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetDashboardServiceTest {

    @Mock
    private DashboardMetricsRepository dashboardMetricsRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void executeShouldReturnEmptyDashboardWhenNoData() {
        when(userRepository.findByNameIgnoreCase("demo"))
                .thenReturn(Optional.of(new UserEntity(1L, "demo", "demo@example.com")));
        when(dashboardMetricsRepository.sumInvoiceTotalHtForStatusesBetween(any(Long.class), anyList(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(null);
        when(dashboardMetricsRepository.sumInvoiceTotalHtForStatus(1L, InvoiceStatus.SENT)).thenReturn(null);
        when(dashboardMetricsRepository.findMonthlyRevenueHistory(any(Long.class), anyList(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());
        when(dashboardMetricsRepository.findClientRevenueDistribution(any(Long.class), anyList(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());
        when(dashboardMetricsRepository.findOverdueInvoices(any(Long.class), any(LocalDate.class))).thenReturn(List.of());
        when(dashboardMetricsRepository.findExpiringMissions(any(Long.class),
                any(com.freelanceos.freelanceappback.domain.model.dashboard.MissionStatus.class), any(LocalDate.class)))
                .thenReturn(List.of());
        when(dashboardMetricsRepository.findFiscalConfigByUserId(1L)).thenReturn(Optional.empty());

        GetDashboardService service = new GetDashboardService(dashboardMetricsRepository, userRepository, new DashboardMapper());

        Dashboard dashboard = service.execute("demo");

        assertThat(dashboard.monthlyTurnover()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(dashboard.annualTurnover()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(dashboard.pendingPayments()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(dashboard.revenueHistory()).hasSize(12);
        for (MonthlyStat stat : dashboard.revenueHistory()) {
            assertThat(stat.paid()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(stat.sent()).isEqualByComparingTo(BigDecimal.ZERO);
        }
        assertThat(dashboard.clientDistribution()).isEmpty();
        assertThat(dashboard.overdueInvoices()).isEmpty();
        assertThat(dashboard.expiringMissions()).isEmpty();
        assertThat(dashboard.nextTaxDeadline().amountToPay()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void executeShouldAggregateClientDistributionTopFiveAndOthers() {
        when(userRepository.findByNameIgnoreCase("demo"))
                .thenReturn(Optional.of(new UserEntity(1L, "demo", "demo@example.com")));
        when(dashboardMetricsRepository.sumInvoiceTotalHtForStatusesBetween(any(Long.class), anyList(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);
        when(dashboardMetricsRepository.sumInvoiceTotalHtForStatus(1L, InvoiceStatus.SENT)).thenReturn(BigDecimal.ZERO);
        when(dashboardMetricsRepository.findMonthlyRevenueHistory(any(Long.class), anyList(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());
        when(dashboardMetricsRepository.findOverdueInvoices(any(Long.class), any(LocalDate.class))).thenReturn(List.of());
        when(dashboardMetricsRepository.findExpiringMissions(any(Long.class),
                any(com.freelanceos.freelanceappback.domain.model.dashboard.MissionStatus.class), any(LocalDate.class)))
                .thenReturn(List.of());
        when(dashboardMetricsRepository.findFiscalConfigByUserId(1L)).thenReturn(Optional.empty());

        List<ClientRevenueAggregateProjection> projections = List.of(
                new ClientRevenueAggregate("Client A", BigDecimal.valueOf(300)),
                new ClientRevenueAggregate("Client B", BigDecimal.valueOf(250)),
                new ClientRevenueAggregate("Client C", BigDecimal.valueOf(200)),
                new ClientRevenueAggregate("Client D", BigDecimal.valueOf(150)),
                new ClientRevenueAggregate("Client E", BigDecimal.valueOf(100)),
                new ClientRevenueAggregate("Client F", BigDecimal.valueOf(80)),
                new ClientRevenueAggregate("Client G", BigDecimal.valueOf(70))
        );
        when(dashboardMetricsRepository.findClientRevenueDistribution(any(Long.class), anyList(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(projections);

        GetDashboardService service = new GetDashboardService(dashboardMetricsRepository, userRepository, new DashboardMapper());

        Dashboard dashboard = service.execute("demo");

        assertThat(dashboard.clientDistribution()).hasSize(6);
        assertThat(dashboard.clientDistribution().get(0)).isEqualTo(new ClientRevenueShare("Client A", BigDecimal.valueOf(300)));
        assertThat(dashboard.clientDistribution().get(4)).isEqualTo(new ClientRevenueShare("Client E", BigDecimal.valueOf(100)));
        assertThat(dashboard.clientDistribution().get(5)).isEqualTo(new ClientRevenueShare("Autres", BigDecimal.valueOf(150)));
    }

    private record ClientRevenueAggregate(String clientName, BigDecimal amount) implements ClientRevenueAggregateProjection {
        @Override
        public String getClientName() {
            return clientName;
        }

        @Override
        public BigDecimal getAmount() {
            return amount;
        }
    }

    @SuppressWarnings("unused")
    private record MonthlyRevenueAggregate(int year, int month, InvoiceStatus status, BigDecimal amount)
            implements MonthlyRevenueAggregateProjection {
        @Override
        public Integer getYear() {
            return year;
        }

        @Override
        public Integer getMonth() {
            return month;
        }

        @Override
        public InvoiceStatus getStatus() {
            return status;
        }

        @Override
        public BigDecimal getAmount() {
            return amount;
        }
    }
}
