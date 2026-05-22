package com.freelanceos.freelanceappback.application.rest.mapper;

import com.freelanceos.freelanceappback.application.rest.dto.mission.MissionDetailResponse;
import com.freelanceos.freelanceappback.application.rest.dto.mission.MissionFinancialsResponse;
import com.freelanceos.freelanceappback.application.rest.dto.mission.MissionClientResponse;
import com.freelanceos.freelanceappback.application.rest.dto.mission.InvoiceSummaryForMissionResponse;
import com.freelanceos.freelanceappback.application.rest.dto.mission.MissionListResponse;
import com.freelanceos.freelanceappback.application.rest.dto.mission.MissionPeriodResponse;
import com.freelanceos.freelanceappback.application.rest.dto.mission.MissionRequest;
import com.freelanceos.freelanceappback.domain.model.client.ClientSummary;
import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceSummaryForMission;
import com.freelanceos.freelanceappback.domain.model.mission.Mission;
import com.freelanceos.freelanceappback.domain.model.mission.MissionDetail;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class MissionMapperRest {
    public Mission toDomain(MissionRequest request) {
        return new Mission(
                null,
                null,
                request.title(),
                new ClientSummary(request.clientId(), null),
                request.dailyRate(),
                request.expectedDuration(),
                request.totalBudgetEstimated(),
                request.startDate(),
                request.endDate(),
                request.status(),
                request.billingType(),
                request.internalNotes(),
                request.currency()
        );
    }

    public Mission toDomain(Long id, MissionRequest request) {
        return new Mission(
                id,
                null,
                request.title(),
                new ClientSummary(request.clientId(), null),
                request.dailyRate(),
                request.expectedDuration(),
                request.totalBudgetEstimated(),
                request.startDate(),
                request.endDate(),
                request.status(),
                request.billingType(),
                request.internalNotes(),
                request.currency()
        );
    }

    public MissionListResponse toList(Mission mission) {
        return new MissionListResponse(
                mission.id(),
                mission.title(),
                new MissionClientResponse(mission.client().id(), mission.client().name()),
                mission.dailyRate(),
                mission.currency(),
                mission.status(),
                mission.endDate(),
                calculateTimeProgressPercent(mission.startDate(), mission.endDate(), LocalDate.now())
        );
    }

    public MissionDetailResponse toDetail(MissionDetail missionDetail) {
        MissionClientResponse client = new MissionClientResponse(
                missionDetail.client().id(),
                missionDetail.client().name()
        );
        MissionFinancialsResponse financials = new MissionFinancialsResponse(
                missionDetail.dailyRate(),
                missionDetail.totalBudgetEstimated(),
                missionDetail.totalInvoiced(),
                missionDetail.currency()
        );
        MissionPeriodResponse period = new MissionPeriodResponse(
                missionDetail.startDate(),
                missionDetail.endDate(),
                calculateTimeProgressPercent(missionDetail.startDate(), missionDetail.endDate(), LocalDate.now())
        );
        return new MissionDetailResponse(
                missionDetail.id(),
                missionDetail.title(),
                missionDetail.status(),
                client,
                financials,
                missionDetail.expectedDuration(),
                period,
                missionDetail.internalNotes(),
                missionDetail.invoices().stream()
                        .map(this::toInvoice)
                        .toList()
        );
    }

    public MissionDetailResponse toDetail(Mission mission, List<InvoiceSummaryForMission> invoices, BigDecimal totalInvoiced) {
        MissionClientResponse client = new MissionClientResponse(mission.client().id(), mission.client().name());
        MissionFinancialsResponse financials = new MissionFinancialsResponse(
                mission.dailyRate(),
                mission.totalBudgetEstimated(),
                totalInvoiced,
                mission.currency()
        );
        MissionPeriodResponse period = new MissionPeriodResponse(
                mission.startDate(),
                mission.endDate(),
                calculateTimeProgressPercent(mission.startDate(), mission.endDate(), LocalDate.now())
        );
        return new MissionDetailResponse(
                mission.id(),
                mission.title(),
                mission.status(),
                client,
                financials,
                mission.expectedDuration(),
                period,
                mission.internalNotes(),
                invoices.stream()
                        .map(this::toInvoice)
                        .toList()
        );
    }

    private InvoiceSummaryForMissionResponse toInvoice(InvoiceSummaryForMission invoice) {
        return new InvoiceSummaryForMissionResponse(
                invoice.id(),
                invoice.number(),
                invoice.amount(),
                invoice.status()
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
