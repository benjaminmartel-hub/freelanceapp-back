package com.freelanceos.freelanceappback.infrastructure.persistence.adapter;

import com.freelanceos.freelanceappback.domain.ports.out.InvoiceRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.repository.SpringDataInvoiceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaInvoiceRepositoryAdapter implements InvoiceRepository {
    private final SpringDataInvoiceJpaRepository invoiceJpaRepository;

    public JpaInvoiceRepositoryAdapter(SpringDataInvoiceJpaRepository invoiceJpaRepository) {
        this.invoiceJpaRepository = invoiceJpaRepository;
    }

    @Override
    public List<Long> findIdsByUserIdAndClientName(Long userId, String clientName) {
        return invoiceJpaRepository.findIdsByUserIdAndClientName(userId, clientName);
    }
}
