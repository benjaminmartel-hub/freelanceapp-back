package com.freelanceos.freelanceappback.domain.ports.in.invoice;

import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStats;

public interface GetInvoiceStatsUseCase {
    InvoiceStats execute(String username);
}
