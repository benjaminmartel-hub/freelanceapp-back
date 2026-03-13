package com.freelanceos.freelanceappback.application.rest.mapper;

import com.freelanceos.freelanceappback.application.rest.dto.mission.MissionDetail;
import com.freelanceos.freelanceappback.application.rest.dto.mission.MissionList;
import com.freelanceos.freelanceappback.application.rest.dto.mission.MissionRequest;
import com.freelanceos.freelanceappback.domain.model.mission.Mission;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class MissionMapperRest {
    public Mission toDomain(MissionRequest request) {
        return new Mission(
                null,
                null,
                request.title(),
                request.clientName(),
                request.clientContactEmail(),
                request.dailyRate(),
                request.expectedDuration(),
                request.totalBudgetEstimated(),
                request.startDate(),
                request.endDate(),
                request.status(),
                request.billingType(),
                request.internalNotes()
        );
    }

    public Mission toDomain(Long id, MissionRequest request) {
        return new Mission(
                id,
                null,
                request.title(),
                request.clientName(),
                request.clientContactEmail(),
                request.dailyRate(),
                request.expectedDuration(),
                request.totalBudgetEstimated(),
                request.startDate(),
                request.endDate(),
                request.status(),
                request.billingType(),
                request.internalNotes()
        );
    }

    public MissionList toList(Mission mission) {
        return new MissionList(
                mission.id(),
                mission.clientName(),
                mission.dailyRate(),
                mission.status(),
                calculateTimeProgressPercent(mission.startDate(), mission.endDate(), LocalDate.now())
        );
    }

    public MissionDetail toDetail(com.freelanceos.freelanceappback.domain.model.mission.MissionDetail missionDetail) {
        return new MissionDetail(
                missionDetail.id(),
                missionDetail.title(),
                missionDetail.clientName(),
                missionDetail.clientContactEmail(),
                missionDetail.dailyRate(),
                missionDetail.expectedDuration(),
                missionDetail.totalBudgetEstimated(),
                missionDetail.startDate(),
                missionDetail.endDate(),
                missionDetail.status(),
                missionDetail.billingType(),
                missionDetail.internalNotes(),
                missionDetail.invoiceIds()
        );
    }

    public MissionDetail toDetail(Mission mission, java.util.List<Long> invoiceIds) {
        return new MissionDetail(
                mission.id(),
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
                mission.internalNotes(),
                invoiceIds
        );
    }

    private Integer calculateTimeProgressPercent(LocalDate startDate, LocalDate endDate, LocalDate today) {
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            return null;
        }
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        if (totalDays <= 0) {
            return today.isAfter(endDate) || today.isEqual(endDate) ? 100 : 0;
        }
        long elapsedDays = ChronoUnit.DAYS.between(startDate, today);
        if (elapsedDays <= 0) {
            return 0;
        }
        double ratio = (double) elapsedDays / (double) totalDays;
        int percent = (int) Math.round(ratio * 100.0);
        if (percent < 0) {
            return 0;
        }
        return Math.min(percent, 100);
    }
}
