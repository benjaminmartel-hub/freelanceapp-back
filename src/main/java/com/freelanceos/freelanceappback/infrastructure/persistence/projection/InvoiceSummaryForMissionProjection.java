package com.freelanceos.freelanceappback.infrastructure.persistence.projection;

import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStatus;

import java.math.BigDecimal;

public interface InvoiceSummaryForMissionProjection {
    Long getId();

    String getNumber();

    BigDecimal getAmount();

    InvoiceStatus getStatus();
}
