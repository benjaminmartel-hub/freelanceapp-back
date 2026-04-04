package com.freelanceos.freelanceappback.domain.ports.out;

import com.freelanceos.freelanceappback.domain.model.dashboard.InvoiceStatus;
import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.FiscalConfigEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.InvoiceEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.MissionEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.projection.ClientRevenueAggregateProjection;
import com.freelanceos.freelanceappback.infrastructure.persistence.projection.MonthlyRevenueAggregateProjection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DashboardMetricsRepository {
    BigDecimal sumInvoiceTotalHtForStatusesBetween(Long userId,
                                                   List<InvoiceStatus> statuses,
                                                   LocalDate startDate,
                                                   LocalDate endDateExclusive);

    BigDecimal sumInvoiceTotalHtForStatus(Long userId, InvoiceStatus status);

    List<MonthlyRevenueAggregateProjection> findMonthlyRevenueHistory(Long userId,
                                                                      List<InvoiceStatus> statuses,
                                                                      LocalDate startDate,
                                                                      LocalDate endDateExclusive);

    List<ClientRevenueAggregateProjection> findClientRevenueDistribution(Long userId,
                                                                          List<InvoiceStatus> statuses,
                                                                          LocalDate startDate,
                                                                          LocalDate endDateExclusive);

    List<InvoiceEntity> findOverdueInvoices(Long userId, LocalDate today);

    List<MissionEntity> findExpiringMissions(Long userId, MissionStatus status, LocalDate endDateLimit);

    Optional<FiscalConfigEntity> findFiscalConfigByUserId(Long userId);
}
