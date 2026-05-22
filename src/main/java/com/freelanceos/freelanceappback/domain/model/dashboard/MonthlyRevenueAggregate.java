package com.freelanceos.freelanceappback.domain.model.dashboard;

import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStatus;

import java.math.BigDecimal;

public record MonthlyRevenueAggregate(int year, int month, InvoiceStatus status, BigDecimal amount) {
}
