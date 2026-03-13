package com.freelanceos.freelanceappback.domain.model.mission;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record MissionDetail(Long id,
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
                            String internalNotes,
                            List<Long> invoiceIds) {
}
