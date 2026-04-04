package com.freelanceos.freelanceappback.infrastructure.persistence.projection;

import com.freelanceos.freelanceappback.domain.model.dashboard.InvoiceStatus;

import java.math.BigDecimal;

public interface MissionInvoiceSummaryProjection {
    Long getId();

    String getNumber();

    BigDecimal getAmount();

    InvoiceStatus getStatus();
}
