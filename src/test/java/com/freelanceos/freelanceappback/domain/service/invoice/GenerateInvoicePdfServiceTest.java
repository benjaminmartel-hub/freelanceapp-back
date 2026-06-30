package com.freelanceos.freelanceappback.domain.service.invoice;

import com.freelanceos.freelanceappback.domain.model.invoice.GeneratedInvoicePdf;
import com.freelanceos.freelanceappback.domain.ports.out.pdf.InvoicePdfGenerator;
import com.freelanceos.freelanceappback.domain.ports.out.InvoiceRepository;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.ClientEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.InvoiceEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.MissionEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.InvoiceMapper;
import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStatus;
import com.freelanceos.freelanceappback.domain.model.mission.BillingType;
import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerateInvoicePdfServiceTest {
    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InvoicePdfGenerator invoicePdfGenerator;

    @Test
    void executeShouldGeneratePdfForInvoiceOwnedByResolvedUser() {
        UserEntity user = new UserEntity(1L, "demo", "demo@example.com");
        InvoiceEntity invoice = buildInvoiceEntity(user);
        byte[] content = "%PDF-test".getBytes();

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(invoiceRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(invoice));
        when(invoicePdfGenerator.generate(any())).thenReturn(content);

        GenerateInvoicePdfService service = new GenerateInvoicePdfService(
                invoiceRepository,
                userRepository,
                new InvoiceMapper(),
                invoicePdfGenerator
        );

        Optional<GeneratedInvoicePdf> result = service.execute("demo", 10L);

        assertThat(result).isPresent();
        assertThat(result.get().filename()).isEqualTo("FAC-2026-0001.pdf");
        assertThat(result.get().content()).isEqualTo(content);
        verify(invoiceRepository).findByIdAndUserId(10L, 1L);
        verify(invoicePdfGenerator).generate(any());
    }

    @Test
    void executeShouldNotGeneratePdfWhenInvoiceDoesNotBelongToUser() {
        UserEntity user = new UserEntity(1L, "demo", "demo@example.com");

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(invoiceRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.empty());

        GenerateInvoicePdfService service = new GenerateInvoicePdfService(
                invoiceRepository,
                userRepository,
                new InvoiceMapper(),
                invoicePdfGenerator
        );

        Optional<GeneratedInvoicePdf> result = service.execute("demo", 10L);

        assertThat(result).isEmpty();
        verify(invoicePdfGenerator, never()).generate(any());
    }

    private InvoiceEntity buildInvoiceEntity(UserEntity user) {
        ClientEntity client = new ClientEntity(20L, user, "Maison Beldi", "contact@example.com");
        MissionEntity mission = new MissionEntity(30L, user, client, "Audit",
                BigDecimal.valueOf(600), 10, BigDecimal.valueOf(6000),
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31),
                MissionStatus.ONGOING, BillingType.TJM, "Notes", "EUR");
        return new InvoiceEntity(10L, user, mission, "FAC-2026-0001", InvoiceStatus.SENT,
                LocalDate.of(2026, 1, 5), LocalDate.of(2026, 2, 5),
                BigDecimal.valueOf(1000), BigDecimal.valueOf(20), BigDecimal.valueOf(1200));
    }
}
