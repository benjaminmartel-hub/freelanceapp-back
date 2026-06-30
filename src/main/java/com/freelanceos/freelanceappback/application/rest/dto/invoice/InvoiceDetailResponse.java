package com.freelanceos.freelanceappback.application.rest.dto.invoice;

import com.freelanceos.freelanceappback.application.rest.dto.mission.MissionSummaryForInvoiceResponse;
import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceDetailResponse(Long id,
                                    String number,
                                    LocalDate issueDate,
                                    LocalDate dueDate,
                                    BigDecimal totalHt,
                                    BigDecimal vatRate,
                                    BigDecimal totalTtc,
                                    InvoiceStatus status,
                                    MissionSummaryForInvoiceResponse mission) {
}
