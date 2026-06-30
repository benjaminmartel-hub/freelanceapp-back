package com.freelanceos.freelanceappback.domain.model.invoice;

import java.math.BigDecimal;

public record InvoiceSummaryForMission(Long id,
                                       String number,
                                       BigDecimal amount,
                                       InvoiceStatus status) {
}
