package com.freelanceos.freelanceappback.application.rest.dto.invoice;

import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceListResponse(Long id,
                                  String number,
                                  LocalDate issueDate,
                                  LocalDate dueDate,
                                  BigDecimal totalHt,
                                  BigDecimal vatRate,
                                  BigDecimal totalTtc,
                                  InvoiceStatus status,
                                  Long missionId,
                                  String missionTitle,
                                  String clientName) {
}
