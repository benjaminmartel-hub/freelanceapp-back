package com.freelanceos.freelanceappback.application.rest.dto.invoice;

import java.math.BigDecimal;

public record InvoiceStatsResponse(BigDecimal totalPaid,
                                   BigDecimal totalPending,
                                   BigDecimal totalOverdue) {
}
