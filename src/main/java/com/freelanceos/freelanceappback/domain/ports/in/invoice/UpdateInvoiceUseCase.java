package com.freelanceos.freelanceappback.domain.ports.in.invoice;

import com.freelanceos.freelanceappback.domain.model.invoice.Invoice;

import java.util.Optional;

public interface UpdateInvoiceUseCase {
    Optional<Invoice> execute(String username, Long id, Invoice invoiceToUpdate);
}
