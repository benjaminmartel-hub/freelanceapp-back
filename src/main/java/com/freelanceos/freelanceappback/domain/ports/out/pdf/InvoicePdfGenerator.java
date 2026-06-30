package com.freelanceos.freelanceappback.domain.ports.out.pdf;

import com.freelanceos.freelanceappback.domain.model.invoice.Invoice;

public interface InvoicePdfGenerator {
    byte[] generate(Invoice invoice);
}
