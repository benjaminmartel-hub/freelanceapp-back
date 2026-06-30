package com.freelanceos.freelanceappback.infrastructure.persistence.mapper;

import com.freelanceos.freelanceappback.domain.model.client.ClientSummary;
import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceSummaryForMission;
import com.freelanceos.freelanceappback.domain.model.mission.Mission;
import com.freelanceos.freelanceappback.domain.model.mission.MissionDetail;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.ClientEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.MissionEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.projection.InvoiceSummaryForMissionProjection;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class MissionMapper {
    public MissionEntity toEntity(Mission mission, UserEntity userEntity, ClientEntity clientEntity) {
        return new MissionEntity(
                mission.id(),
                userEntity,
                clientEntity,
                mission.title(),
                mission.dailyRate(),
                mission.expectedDuration(),
                mission.totalBudgetEstimated(),
                mission.startDate(),
                mission.endDate(),
                mission.status(),
                mission.billingType(),
                mission.internalNotes(),
                mission.currency()
        );
    }

    public Mission toDomain(MissionEntity missionEntity) {
        return new Mission(
                missionEntity.getId(),
                missionEntity.getUser().getId(),
                missionEntity.getTitle(),
                new ClientSummary(
                        missionEntity.getClient().getId(),
                        missionEntity.getClient().getName()
                ),
                missionEntity.getDailyRate(),
                missionEntity.getExpectedDuration(),
                missionEntity.getTotalBudgetEstimated(),
                missionEntity.getStartDate(),
                missionEntity.getEndDate(),
                missionEntity.getStatus(),
                missionEntity.getBillingType(),
                missionEntity.getInternalNotes(),
                missionEntity.getCurrency()
        );
    }

    public MissionDetail toDomain(Mission mission, List<InvoiceSummaryForMission> invoices, BigDecimal totalInvoiced) {
        return new MissionDetail(
                mission.id(),
                mission.userId(),
                mission.title(),
                mission.client(),
                mission.dailyRate(),
                mission.expectedDuration(),
                mission.totalBudgetEstimated(),
                totalInvoiced,
                mission.currency(),
                mission.startDate(),
                mission.endDate(),
                mission.status(),
                mission.billingType(),
                mission.internalNotes(),
                invoices
        );
    }

    public InvoiceSummaryForMission toInvoiceSummaryForMission(InvoiceSummaryForMissionProjection projection) {
        return new InvoiceSummaryForMission(
                projection.getId(),
                projection.getNumber(),
                projection.getAmount(),
                projection.getStatus()
        );
    }

    public Mission toDomain(Mission mission, Long userId, ClientEntity clientEntity, BigDecimal totalBudget, String currency) {
        return new Mission(
                mission.id(),
                userId,
                mission.title(),
                new ClientSummary(clientEntity.getId(), clientEntity.getName()),
                mission.dailyRate(),
                mission.expectedDuration(),
                totalBudget,
                mission.startDate(),
                mission.endDate(),
                mission.status(),
                mission.billingType(),
                mission.internalNotes(),
                currency
        );
    }
}
