package com.freelanceos.freelanceappback.application.rest.dto.mission;

import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;

import java.util.List;

public record MissionDetailResponse(Long id,
                                    String title,
                                    MissionStatus status,
                                    MissionClientResponse client,
                                    MissionFinancialsResponse financials,
                                    Integer expectedDuration,
                                    MissionPeriodResponse period,
                                    String internalNotes,
                                    List<MissionInvoiceResponse> invoices) {
}
