package com.freelanceos.freelanceappback.application.rest.dto.mission;

import com.freelanceos.freelanceappback.domain.model.mission.BillingType;
import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MissionRequest(
        @NotBlank String title,
        @NotNull Long clientId,
        @NotNull @Positive BigDecimal dailyRate,
        @NotNull @Positive Integer expectedDuration,
        BigDecimal totalBudgetEstimated,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotNull MissionStatus status,
        @NotNull BillingType billingType,
        String internalNotes,
        @NotBlank String currency
) {
}
