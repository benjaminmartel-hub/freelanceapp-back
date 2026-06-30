package com.freelanceos.freelanceappback.domain.model.invoice;

import java.math.BigDecimal;

public record InvoiceStats(BigDecimal totalPaid,
                           BigDecimal totalPending,
                           BigDecimal totalOverdue) {
}
