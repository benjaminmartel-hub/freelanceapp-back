package com.freelanceos.freelanceappback.infrastructure.persistence.projection;

import java.math.BigDecimal;

public interface ClientRevenueAggregateProjection {
    String getClientName();

    BigDecimal getAmount();
}
