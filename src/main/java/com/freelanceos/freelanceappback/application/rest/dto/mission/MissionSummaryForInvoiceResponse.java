package com.freelanceos.freelanceappback.application.rest.dto.mission;

import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;

public record MissionSummaryForInvoiceResponse(Long id,
                                               String title,
                                               MissionClientResponse client,
                                               MissionStatus status,
                                               String currency) {
}
