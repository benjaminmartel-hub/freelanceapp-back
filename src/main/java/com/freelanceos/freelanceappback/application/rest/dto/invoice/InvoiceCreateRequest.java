package com.freelanceos.freelanceappback.application.rest.dto.invoice;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceCreateRequest(
        @NotNull Long missionId,
        @NotNull LocalDate issueDate,
        @NotNull LocalDate dueDate,
        @NotNull @Positive BigDecimal totalHt,
        @NotNull @Positive BigDecimal vatRate,
        @Positive BigDecimal totalTtc
) {
}
