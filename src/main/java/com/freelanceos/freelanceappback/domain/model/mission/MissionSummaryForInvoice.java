package com.freelanceos.freelanceappback.domain.model.mission;

import com.freelanceos.freelanceappback.domain.model.client.ClientSummary;

public record MissionSummaryForInvoice(Long id,
                                       String title,
                                       ClientSummary client,
                                       MissionStatus status,
                                       String currency) {
}
