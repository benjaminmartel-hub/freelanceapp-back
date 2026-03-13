package com.freelanceos.freelanceappback.infrastructure.persistence.mapper;

import com.freelanceos.freelanceappback.domain.model.mission.Mission;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.MissionEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class MissionMapper {
    public MissionEntity toEntity(Mission mission, UserEntity userEntity) {
        return new MissionEntity(
                mission.id(),
                userEntity,
                mission.title(),
                mission.clientName(),
                mission.clientContactEmail(),
                mission.dailyRate(),
                mission.expectedDuration(),
                mission.totalBudgetEstimated(),
                mission.startDate(),
                mission.endDate(),
                mission.status(),
                mission.billingType(),
                mission.internalNotes()
        );
    }

    public Mission toDomain(MissionEntity missionEntity) {
        return new Mission(
                missionEntity.getId(),
                missionEntity.getUser().getId(),
                missionEntity.getTitle(),
                missionEntity.getClientName(),
                missionEntity.getClientContactEmail(),
                missionEntity.getDailyRate(),
                missionEntity.getExpectedDuration(),
                missionEntity.getTotalBudgetEstimated(),
                missionEntity.getStartDate(),
                missionEntity.getEndDate(),
                missionEntity.getStatus(),
                missionEntity.getBillingType(),
                missionEntity.getInternalNotes()
        );
    }
}
