package com.freelanceos.freelanceappback.domain.model.invoice;

import com.freelanceos.freelanceappback.domain.model.mission.MissionSummaryForInvoice;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Invoice(Long id,
                      Long userId,
                      String number,
                      LocalDate issueDate,
                      LocalDate dueDate,
                      BigDecimal totalHt,
                      BigDecimal vatRate,
                      BigDecimal totalTtc,
                      InvoiceStatus status,
                      MissionSummaryForInvoice mission) {
}
