package com.freelanceos.freelanceappback.domain.service.invoice;

import com.freelanceos.freelanceappback.domain.exception.NotFoundException;
import com.freelanceos.freelanceappback.domain.model.invoice.Invoice;
import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStatus;
import com.freelanceos.freelanceappback.domain.model.mission.BillingType;
import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;
import com.freelanceos.freelanceappback.domain.ports.out.InvoiceRepository;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.ClientEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.InvoiceEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.MissionEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.InvoiceMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {
    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void executeShouldListInvoicesForResolvedUserOnly() {
        UserEntity user = new UserEntity(1L, "demo", "demo@example.com");
        InvoiceEntity invoiceEntity = buildInvoiceEntity(user);
        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(invoiceRepository.findByUserId(1L)).thenReturn(List.of(invoiceEntity));

        InvoiceService service = new InvoiceService(invoiceRepository, userRepository, new InvoiceMapper());

        List<Invoice> invoices = service.execute("demo");

        assertThat(invoices).hasSize(1);
        assertThat(invoices.get(0).userId()).isEqualTo(1L);
        assertThat(invoices.get(0).mission().client().name()).isEqualTo("Maison Beldi");
    }

    @Test
    void executeDetailShouldReturnEmptyWhenInvoiceDoesNotBelongToUser() {
        UserEntity user = new UserEntity(1L, "demo", "demo@example.com");
        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(invoiceRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        InvoiceService service = new InvoiceService(invoiceRepository, userRepository, new InvoiceMapper());

        assertThat(service.execute("demo", 99L)).isEmpty();
    }

    @Test
    void executeShouldThrowWhenUserIsUnknown() {
        when(userRepository.findByNameIgnoreCase("missing")).thenReturn(Optional.empty());

        InvoiceService service = new InvoiceService(invoiceRepository, userRepository, new InvoiceMapper());

        assertThatThrownBy(() -> service.execute("missing"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found");
    }

    private InvoiceEntity buildInvoiceEntity(UserEntity user) {
        ClientEntity client = new ClientEntity(20L, user, "Maison Beldi", "contact@example.com");
        MissionEntity mission = new MissionEntity(10L, user, client, "Audit",
                BigDecimal.valueOf(600), 10, BigDecimal.valueOf(6000),
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31),
                MissionStatus.ONGOING, BillingType.TJM, "Notes", "EUR");
        return new InvoiceEntity(1L, user, mission, "FAC-2026-001", InvoiceStatus.SENT,
                LocalDate.of(2026, 1, 5), LocalDate.of(2026, 2, 5),
                BigDecimal.valueOf(1000), BigDecimal.valueOf(0.2000), BigDecimal.valueOf(1200));
    }
}
