package com.freelanceos.freelanceappback.domain.model.mission;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.freelanceos.freelanceappback.domain.model.client.ClientSummary;
import com.freelanceos.freelanceappback.domain.model.invoice.MissionInvoice;

import java.util.List;

public record MissionDetail(Long id,
                            Long userId,
                            String title,
                            ClientSummary client,
                            BigDecimal dailyRate,
                            Integer expectedDuration,
                            BigDecimal totalBudgetEstimated,
                            BigDecimal totalInvoiced,
                            String currency,
                            LocalDate startDate,
                            LocalDate endDate,
                            MissionStatus status,
                            BillingType billingType,
                            String internalNotes,
                            List<MissionInvoice> invoices) {
}
