package com.freelanceos.freelanceappback.domain.model.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TaxEstimation(BigDecimal amountToPay, LocalDate deadline, String label) {
}
