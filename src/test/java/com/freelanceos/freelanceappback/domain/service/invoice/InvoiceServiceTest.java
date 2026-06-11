package com.freelanceos.freelanceappback.domain.service.invoice;

import com.freelanceos.freelanceappback.domain.exception.ConflictException;
import com.freelanceos.freelanceappback.domain.exception.NotFoundException;
import com.freelanceos.freelanceappback.domain.model.invoice.Invoice;
import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStatus;
import com.freelanceos.freelanceappback.domain.model.mission.MissionSummaryForInvoice;
import com.freelanceos.freelanceappback.domain.model.mission.BillingType;
import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;
import com.freelanceos.freelanceappback.domain.ports.out.InvoiceRepository;
import com.freelanceos.freelanceappback.domain.ports.out.MissionRepository;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.ClientEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.InvoiceEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.MissionEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.InvoiceMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {
    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void executeShouldListInvoicesForResolvedUserOnly() {
        UserEntity user = new UserEntity(1L, "demo", "demo@example.com");
        InvoiceEntity invoiceEntity = buildInvoiceEntity(user);
        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(invoiceRepository.findByUserId(1L)).thenReturn(List.of(invoiceEntity));

        InvoiceService service = new InvoiceService(invoiceRepository, missionRepository, userRepository, new InvoiceMapper());

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

        InvoiceService service = new InvoiceService(invoiceRepository, missionRepository, userRepository, new InvoiceMapper());

        assertThat(service.execute("demo", 99L)).isEmpty();
    }

    @Test
    void executeShouldThrowWhenUserIsUnknown() {
        when(userRepository.findByNameIgnoreCase("missing")).thenReturn(Optional.empty());

        InvoiceService service = new InvoiceService(invoiceRepository, missionRepository, userRepository, new InvoiceMapper());

        assertThatThrownBy(() -> service.execute("missing"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void createShouldGenerateNumberAndPersistDraftInvoice() {
        UserEntity user = new UserEntity(1L, "demo", "demo@example.com");
        Invoice invoiceToCreate = buildInvoiceToPersist(null, InvoiceStatus.DRAFT);
        InvoiceEntity saved = buildInvoiceEntity(user);
        saved.setStatus(InvoiceStatus.DRAFT);

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(missionRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(saved.getMission()));
        when(invoiceRepository.findHighestInvoiceNumberForYear(2026)).thenReturn(Optional.of("FAC-2026-0008"));
        when(invoiceRepository.save(any(InvoiceEntity.class))).thenAnswer(invocation -> {
            InvoiceEntity invoice = invocation.getArgument(0);
            invoice.setId(99L);
            return invoice;
        });

        InvoiceService service = new InvoiceService(invoiceRepository, missionRepository, userRepository, new InvoiceMapper());

        Invoice created = service.execute("demo", invoiceToCreate);

        assertThat(created.id()).isEqualTo(99L);
        assertThat(created.number()).isEqualTo("FAC-2026-0009");
        assertThat(created.status()).isEqualTo(InvoiceStatus.DRAFT);
        assertThat(created.vatRate()).isEqualByComparingTo(BigDecimal.valueOf(20));
        assertThat(created.totalTtc()).isEqualByComparingTo(BigDecimal.valueOf(1200).setScale(2));

        ArgumentCaptor<InvoiceEntity> invoiceCaptor = ArgumentCaptor.forClass(InvoiceEntity.class);
        verify(invoiceRepository).save(invoiceCaptor.capture());
        assertThat(invoiceCaptor.getValue().getVatRate()).isEqualByComparingTo(BigDecimal.valueOf(20));
        assertThat(invoiceCaptor.getValue().getNumber()).isEqualTo("FAC-2026-0009");
    }

    @Test
    void createShouldStartInvoiceNumberAtOneWhenYearHasNoInvoice() {
        UserEntity user = new UserEntity(1L, "demo", "demo@example.com");
        Invoice invoiceToCreate = buildInvoiceToPersist(null, InvoiceStatus.DRAFT);
        InvoiceEntity saved = buildInvoiceEntity(user);
        saved.setStatus(InvoiceStatus.DRAFT);

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(missionRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(saved.getMission()));
        when(invoiceRepository.findHighestInvoiceNumberForYear(2026)).thenReturn(Optional.empty());
        when(invoiceRepository.save(any(InvoiceEntity.class))).thenAnswer(invocation -> {
            InvoiceEntity invoice = invocation.getArgument(0);
            invoice.setId(99L);
            return invoice;
        });

        InvoiceService service = new InvoiceService(invoiceRepository, missionRepository, userRepository, new InvoiceMapper());

        Invoice created = service.execute("demo", invoiceToCreate);

        assertThat(created.number()).isEqualTo("FAC-2026-0001");
    }

    @Test
    void updateShouldRejectLockedInvoice() {
        UserEntity user = new UserEntity(1L, "demo", "demo@example.com");
        InvoiceEntity existing = buildInvoiceEntity(user);
        existing.setStatus(InvoiceStatus.SENT);

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(invoiceRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(existing));

        InvoiceService service = new InvoiceService(invoiceRepository, missionRepository, userRepository, new InvoiceMapper());

        assertThatThrownBy(() -> service.execute("demo", 1L, buildInvoiceToPersist(1L, null)))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Facture verrouillee");
    }

    private Invoice buildInvoiceToPersist(Long id, InvoiceStatus status) {
        return new Invoice(
                id,
                null,
                null,
                LocalDate.of(2026, 1, 5),
                LocalDate.of(2026, 2, 5),
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(20),
                null,
                status,
                new MissionSummaryForInvoice(10L, null, null, null, null)
        );
    }

    private InvoiceEntity buildInvoiceEntity(UserEntity user) {
        ClientEntity client = new ClientEntity(20L, user, "Maison Beldi", "contact@example.com");
        MissionEntity mission = new MissionEntity(10L, user, client, "Audit",
                BigDecimal.valueOf(600), 10, BigDecimal.valueOf(6000),
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31),
                MissionStatus.ONGOING, BillingType.TJM, "Notes", "EUR");
        return new InvoiceEntity(1L, user, mission, "FAC-2026-001", InvoiceStatus.SENT,
                LocalDate.of(2026, 1, 5), LocalDate.of(2026, 2, 5),
                BigDecimal.valueOf(1000), BigDecimal.valueOf(20), BigDecimal.valueOf(1200));
    }
}
