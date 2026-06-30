package com.freelanceos.freelanceappback.infrastructure.persistence.projection;

import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStatus;

import java.math.BigDecimal;

public interface MonthlyRevenueAggregateProjection {
    Integer getYear();

    Integer getMonth();

    InvoiceStatus getStatus();

    BigDecimal getAmount();
}
