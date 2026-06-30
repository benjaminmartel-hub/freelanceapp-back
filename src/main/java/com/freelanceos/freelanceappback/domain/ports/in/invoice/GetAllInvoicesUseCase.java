package com.freelanceos.freelanceappback.domain.ports.in.invoice;

import com.freelanceos.freelanceappback.domain.model.invoice.Invoice;

import java.util.List;

public interface GetAllInvoicesUseCase {
    List<Invoice> execute(String username);
}
