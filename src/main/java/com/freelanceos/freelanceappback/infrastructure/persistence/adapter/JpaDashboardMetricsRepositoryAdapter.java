package com.freelanceos.freelanceappback.infrastructure.persistence.adapter;

import com.freelanceos.freelanceappback.domain.model.dashboard.InvoiceStatus;
import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;
import com.freelanceos.freelanceappback.domain.ports.out.DashboardMetricsRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.FiscalConfigEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.InvoiceEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.MissionEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.projection.MonthlyRevenueAggregateProjection;
import com.freelanceos.freelanceappback.infrastructure.persistence.repository.SpringDataFiscalConfigJpaRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.repository.SpringDataInvoiceJpaRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.repository.SpringDataMissionJpaRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.projection.ClientRevenueAggregateProjection;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaDashboardMetricsRepositoryAdapter implements DashboardMetricsRepository {
    private final SpringDataInvoiceJpaRepository invoiceJpaRepository;
    private final SpringDataMissionJpaRepository missionJpaRepository;
    private final SpringDataFiscalConfigJpaRepository fiscalConfigJpaRepository;

    public JpaDashboardMetricsRepositoryAdapter(SpringDataInvoiceJpaRepository invoiceJpaRepository,
                                                SpringDataMissionJpaRepository missionJpaRepository,
                                                SpringDataFiscalConfigJpaRepository fiscalConfigJpaRepository) {
        this.invoiceJpaRepository = invoiceJpaRepository;
        this.missionJpaRepository = missionJpaRepository;
        this.fiscalConfigJpaRepository = fiscalConfigJpaRepository;
    }

    @Override
    public BigDecimal sumInvoiceTotalHtForStatusesBetween(Long userId,
                                                          List<InvoiceStatus> statuses,
                                                          LocalDate startDate,
                                                          LocalDate endDateExclusive) {
        return invoiceJpaRepository.sumTotalHtByUserAndStatusesAndDateRange(userId, statuses, startDate, endDateExclusive);
    }

    @Override
    public BigDecimal sumInvoiceTotalHtForStatus(Long userId, InvoiceStatus status) {
        return invoiceJpaRepository.sumTotalHtByUserAndStatus(userId, status);
    }

    @Override
    public List<MonthlyRevenueAggregateProjection> findMonthlyRevenueHistory(Long userId,
                                                                             List<InvoiceStatus> statuses,
                                                                             LocalDate startDate,
                                                                             LocalDate endDateExclusive) {
        return invoiceJpaRepository.findMonthlyRevenueHistory(userId, statuses, startDate, endDateExclusive);
    }

    @Override
    public List<ClientRevenueAggregateProjection> findClientRevenueDistribution(Long userId,
                                                                                 List<InvoiceStatus> statuses,
                                                                                 LocalDate startDate,
                                                                                 LocalDate endDateExclusive) {
        return invoiceJpaRepository.findClientRevenueDistribution(userId, statuses, startDate, endDateExclusive);
    }

    @Override
    public List<InvoiceEntity> findOverdueInvoices(Long userId, LocalDate today) {
        return invoiceJpaRepository.findOverdueInvoices(userId, InvoiceStatus.SENT, today);
    }

    @Override
    public List<MissionEntity> findExpiringMissions(Long userId, MissionStatus status, LocalDate endDateLimit) {
        return missionJpaRepository.findExpiringMissions(userId, status, endDateLimit);
    }

    @Override
    public Optional<FiscalConfigEntity> findFiscalConfigByUserId(Long userId) {
        return fiscalConfigJpaRepository.findByUserId(userId);
    }
}
