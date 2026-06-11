package com.freelanceos.freelanceappback.infrastructure.persistence.adapter;

import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStatus;
import com.freelanceos.freelanceappback.domain.ports.out.InvoiceRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.InvoiceEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.projection.InvoiceSummaryForMissionProjection;
import com.freelanceos.freelanceappback.infrastructure.persistence.repository.SpringDataInvoiceJpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaInvoiceRepositoryAdapter implements InvoiceRepository {
    private final SpringDataInvoiceJpaRepository invoiceJpaRepository;

    public JpaInvoiceRepositoryAdapter(SpringDataInvoiceJpaRepository invoiceJpaRepository) {
        this.invoiceJpaRepository = invoiceJpaRepository;
    }

    @Override
    public List<InvoiceEntity> findByUserId(Long userId) {
        return invoiceJpaRepository.findByUserIdWithMissionAndClient(userId);
    }

    @Override
    public Optional<InvoiceEntity> findByIdAndUserId(Long id, Long userId) {
        return invoiceJpaRepository.findByIdAndUserIdWithMissionAndClient(id, userId);
    }

    @Override
    public InvoiceEntity save(InvoiceEntity invoice) {
        return invoiceJpaRepository.save(invoice);
    }

    @Override
    public long countByIssueYear(int year) {
        return invoiceJpaRepository.countByIssueYear(year);
    }

    @Override
    public List<InvoiceSummaryForMissionProjection> findInvoiceSummariesForMission(Long userId, Long missionId) {
        return invoiceJpaRepository.findInvoiceSummariesForMission(userId, missionId);
    }

    @Override
    public BigDecimal sumTotalHtByUserIdAndMissionId(Long userId, Long missionId) {
        return invoiceJpaRepository.sumTotalHtByUserIdAndMissionId(userId, missionId);
    }

    @Override
    public BigDecimal sumTotalHtByUserIdAndStatus(Long userId, InvoiceStatus status) {
        return invoiceJpaRepository.sumTotalHtByUserAndStatus(userId, status);
    }

}
