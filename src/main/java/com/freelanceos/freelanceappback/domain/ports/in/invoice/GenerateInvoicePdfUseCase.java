package com.freelanceos.freelanceappback.domain.ports.in.invoice;

import com.freelanceos.freelanceappback.domain.model.invoice.GeneratedInvoicePdf;

import java.util.Optional;

public interface GenerateInvoicePdfUseCase {
    Optional<GeneratedInvoicePdf> execute(String username, Long id);
}
