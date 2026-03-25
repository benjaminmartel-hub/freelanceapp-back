package com.freelanceos.freelanceappback.application.rest.dto.mission;

import java.math.BigDecimal;

public record MissionFinancialsResponse(BigDecimal dailyRate,
                                        BigDecimal totalBudget,
                                        BigDecimal totalInvoiced,
                                        String currency) {
}
