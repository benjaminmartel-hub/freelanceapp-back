package com.freelanceos.freelanceappback.infrastructure.persistence.adapter;

import com.freelanceos.freelanceappback.domain.ports.out.InvoiceRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.projection.MissionInvoiceSummaryProjection;
import com.freelanceos.freelanceappback.infrastructure.persistence.repository.SpringDataInvoiceJpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class JpaInvoiceRepositoryAdapter implements InvoiceRepository {
    private final SpringDataInvoiceJpaRepository invoiceJpaRepository;

    public JpaInvoiceRepositoryAdapter(SpringDataInvoiceJpaRepository invoiceJpaRepository) {
        this.invoiceJpaRepository = invoiceJpaRepository;
    }

    @Override
    public List<MissionInvoiceSummaryProjection> findSummariesByUserIdAndMissionId(Long userId, Long missionId) {
        return invoiceJpaRepository.findMissionInvoiceSummaries(userId, missionId);
    }

    @Override
    public BigDecimal sumTotalHtByUserIdAndMissionId(Long userId, Long missionId) {
        return invoiceJpaRepository.sumTotalHtByUserIdAndMissionId(userId, missionId);
    }

}
