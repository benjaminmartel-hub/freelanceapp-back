package com.freelanceos.freelanceappback.domain.model.dashboard;

import java.math.BigDecimal;

public record MonthlyRevenueAggregate(int year, int month, BigDecimal amount) {
}
