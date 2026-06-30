package com.freelanceos.freelanceappback.domain.service.invoice;

import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStatus;
import com.freelanceos.freelanceappback.domain.ports.out.InvoiceRepository;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetInvoiceStatsServiceTest {
    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void executeShouldAggregateInvoiceKpisByStatus() {
        when(userRepository.findByNameIgnoreCase("demo"))
                .thenReturn(Optional.of(new UserEntity(1L, "demo", "demo@example.com")));
        when(invoiceRepository.sumTotalHtByUserIdAndStatus(1L, InvoiceStatus.PAID)).thenReturn(BigDecimal.valueOf(2500));
        when(invoiceRepository.sumTotalHtByUserIdAndStatus(1L, InvoiceStatus.SENT)).thenReturn(BigDecimal.valueOf(1200));
        when(invoiceRepository.sumTotalHtByUserIdAndStatus(1L, InvoiceStatus.OVERDUE)).thenReturn(null);

        GetInvoiceStatsService service = new GetInvoiceStatsService(invoiceRepository, userRepository);

        var stats = service.execute("demo");

        assertThat(stats.totalPaid()).isEqualByComparingTo(BigDecimal.valueOf(2500));
        assertThat(stats.totalPending()).isEqualByComparingTo(BigDecimal.valueOf(1200));
        assertThat(stats.totalOverdue()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
