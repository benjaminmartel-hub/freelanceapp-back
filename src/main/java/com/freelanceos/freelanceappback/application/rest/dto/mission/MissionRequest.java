package com.freelanceos.freelanceappback.application.rest.dto.mission;

import com.freelanceos.freelanceappback.domain.model.mission.BillingType;
import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MissionRequest(String title,
                             String clientName,
                             String clientContactEmail,
                             BigDecimal dailyRate,
                             Integer expectedDuration,
                             BigDecimal totalBudgetEstimated,
                             LocalDate startDate,
                             LocalDate endDate,
                             MissionStatus status,
                             BillingType billingType,
                             String internalNotes) {
}
