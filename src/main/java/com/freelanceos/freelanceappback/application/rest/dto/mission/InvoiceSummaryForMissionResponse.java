package com.freelanceos.freelanceappback.application.rest.dto.mission;

import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStatus;

import java.math.BigDecimal;

public record InvoiceSummaryForMissionResponse(Long id,
                                     String number,
                                     BigDecimal amount,
                                     InvoiceStatus status) {
}
