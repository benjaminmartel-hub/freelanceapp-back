package com.freelanceos.freelanceappback.domain.ports.out;

import com.freelanceos.freelanceappback.infrastructure.persistence.projection.MissionInvoiceSummaryProjection;

import java.math.BigDecimal;
import java.util.List;

public interface InvoiceRepository {
    List<MissionInvoiceSummaryProjection> findSummariesByUserIdAndMissionId(Long userId, Long missionId);

    BigDecimal sumTotalHtByUserIdAndMissionId(Long userId, Long missionId);
}
