package com.freelanceos.freelanceappback.domain.ports.out;

import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStatus;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.InvoiceEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.projection.InvoiceSummaryForMissionProjection;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository {
    List<InvoiceEntity> findByUserId(Long userId);

    Optional<InvoiceEntity> findByIdAndUserId(Long id, Long userId);

    InvoiceEntity save(InvoiceEntity invoice);

    Optional<String> findHighestInvoiceNumberForYear(int year);

    List<InvoiceSummaryForMissionProjection> findInvoiceSummariesForMission(Long userId, Long missionId);

    BigDecimal sumTotalHtByUserIdAndMissionId(Long userId, Long missionId);

    BigDecimal sumTotalHtByUserIdAndStatus(Long userId, InvoiceStatus status);
}
