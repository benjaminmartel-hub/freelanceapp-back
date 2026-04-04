package com.freelanceos.freelanceappback.infrastructure.persistence.mapper;

import com.freelanceos.freelanceappback.domain.model.dashboard.FiscalConfigSettings;
import com.freelanceos.freelanceappback.domain.model.dashboard.InvoiceSummary;
import com.freelanceos.freelanceappback.domain.model.dashboard.ClientRevenueShare;
import com.freelanceos.freelanceappback.domain.model.dashboard.MissionSummary;
import com.freelanceos.freelanceappback.domain.model.dashboard.MonthlyRevenueAggregate;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.FiscalConfigEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.InvoiceEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.MissionEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.projection.ClientRevenueAggregateProjection;
import com.freelanceos.freelanceappback.infrastructure.persistence.projection.MonthlyRevenueAggregateProjection;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class DashboardMapper {
    public MonthlyRevenueAggregate toDomain(MonthlyRevenueAggregateProjection aggregateEntity) {
        return new MonthlyRevenueAggregate(
                aggregateEntity.getYear(),
                aggregateEntity.getMonth(),
                aggregateEntity.getStatus(),
                aggregateEntity.getAmount()
        );
    }

    public ClientRevenueShare toDomain(ClientRevenueAggregateProjection aggregateEntity) {
        return new ClientRevenueShare(
                aggregateEntity.getClientName(),
                aggregateEntity.getAmount()
        );
    }

    public InvoiceSummary toDomain(InvoiceEntity invoiceEntity, LocalDate today) {
        return new InvoiceSummary(
                invoiceEntity.getId(),
                invoiceEntity.getMission().getClient().getName(),
                invoiceEntity.getTotalTtc(),
                invoiceEntity.getDueDate(),
                ChronoUnit.DAYS.between(invoiceEntity.getDueDate(), today)
        );
    }

    public MissionSummary toDomain(MissionEntity missionEntity) {
        return new MissionSummary(
                missionEntity.getId(),
                missionEntity.getTitle(),
                missionEntity.getClient().getName(),
                missionEntity.getEndDate()
        );
    }

    public FiscalConfigSettings toDomain(FiscalConfigEntity configEntity) {
        return new FiscalConfigSettings(
                configEntity.getTaxRate(),
                configEntity.isVatEnabled(),
                configEntity.getDeclarationPeriod()
        );
    }
}
