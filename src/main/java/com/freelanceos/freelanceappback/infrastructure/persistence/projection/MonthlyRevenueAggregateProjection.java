package com.freelanceos.freelanceappback.infrastructure.persistence.projection;

import java.math.BigDecimal;

public interface MonthlyRevenueAggregateProjection {
    Integer getYear();

    Integer getMonth();

    BigDecimal getAmount();
}
