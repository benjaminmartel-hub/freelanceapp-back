package com.freelanceos.freelanceappback.domain.ports.in.invoice;

import com.freelanceos.freelanceappback.domain.model.invoice.Invoice;

public interface CreateInvoiceUseCase {
    Invoice execute(String username, Invoice invoiceToCreate);
}
