package com.freelanceos.freelanceappback.domain.model.invoice;

import com.freelanceos.freelanceappback.domain.model.dashboard.InvoiceStatus;

import java.math.BigDecimal;

public record MissionInvoice(Long id,
                             String number,
                             BigDecimal amount,
                             InvoiceStatus status) {
}
