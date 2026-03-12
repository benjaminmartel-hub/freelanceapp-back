package com.freelanceos.freelanceappback.domain.model.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceSummary(Long id, String clientName, BigDecimal amount, LocalDate dueDate, long daysOverdue) {
}
