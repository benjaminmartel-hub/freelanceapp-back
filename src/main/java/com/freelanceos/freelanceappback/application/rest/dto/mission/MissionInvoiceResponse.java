package com.freelanceos.freelanceappback.application.rest.dto.mission;

import com.freelanceos.freelanceappback.domain.model.dashboard.InvoiceStatus;

import java.math.BigDecimal;

public record MissionInvoiceResponse(Long id,
                                     String number,
                                     BigDecimal amount,
                                     InvoiceStatus status) {
}
