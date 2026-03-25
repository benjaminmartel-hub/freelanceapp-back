package com.freelanceos.freelanceappback.domain.model.mission;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.freelanceos.freelanceappback.domain.model.client.ClientSummary;

public record Mission(Long id,
                      Long userId,
                      String title,
                      ClientSummary client,
                      BigDecimal dailyRate,
                      Integer expectedDuration,
                      BigDecimal totalBudgetEstimated,
                      LocalDate startDate,
                      LocalDate endDate,
                      MissionStatus status,
                      BillingType billingType,
                      String internalNotes,
                      String currency) {
}
