package com.freelanceos.freelanceappback.domain.model.mission;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Mission(Long id,
                      Long userId,
                      String title,
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
