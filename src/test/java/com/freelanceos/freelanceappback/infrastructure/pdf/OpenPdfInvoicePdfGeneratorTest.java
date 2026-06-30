package com.freelanceos.freelanceappback.infrastructure.pdf;

import com.freelanceos.freelanceappback.domain.model.client.ClientSummary;
import com.freelanceos.freelanceappback.domain.model.invoice.Invoice;
import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStatus;
import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;
import com.freelanceos.freelanceappback.domain.model.mission.MissionSummaryForInvoice;
import com.freelanceos.freelanceappback.infrastructure.persistence.adapter.pdf.OpenPdfInvoicePdfGenerator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class OpenPdfInvoicePdfGeneratorTest {
    @Test
    void generateShouldReturnPdfBytes() {
        OpenPdfInvoicePdfGenerator generator = new OpenPdfInvoicePdfGenerator();

        byte[] pdf = generator.generate(new Invoice(
                1L,
                1L,
                "FAC-2026-0001",
                LocalDate.of(2026, 1, 5),
                LocalDate.of(2026, 2, 5),
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(1200),
                InvoiceStatus.SENT,
                new MissionSummaryForInvoice(
                        10L,
                        "Audit technique",
                        new ClientSummary(20L, "Maison Beldi"),
                        MissionStatus.ONGOING,
                        "EUR"
                )
        ));

        assertThat(pdf).isNotEmpty();
        assertThat(new String(pdf, 0, 4, StandardCharsets.US_ASCII)).isEqualTo("%PDF");
    }
}
