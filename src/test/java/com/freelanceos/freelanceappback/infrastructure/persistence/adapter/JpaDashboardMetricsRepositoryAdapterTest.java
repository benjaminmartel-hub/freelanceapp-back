package com.freelanceos.freelanceappback.infrastructure.persistence.adapter;

import com.freelanceos.freelanceappback.domain.model.dashboard.InvoiceStatus;
import com.freelanceos.freelanceappback.domain.ports.out.DashboardMetricsRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.InvoiceEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.projection.ClientRevenueAggregateProjection;
import com.freelanceos.freelanceappback.infrastructure.persistence.repository.SpringDataInvoiceJpaRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.repository.SpringDataUserJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never"
})
@Import(JpaDashboardMetricsRepositoryAdapter.class)
class JpaDashboardMetricsRepositoryAdapterTest {

    @Autowired
    private DashboardMetricsRepository dashboardMetricsRepository;

    @Autowired
    private SpringDataUserJpaRepository userJpaRepository;

    @Autowired
    private SpringDataInvoiceJpaRepository invoiceJpaRepository;

    @Test
    void findClientRevenueDistributionShouldGroupAndOrderByAmount() {
        UserEntity user = userJpaRepository.save(new UserEntity(null, "demo", "demo@example.com"));
        UserEntity other = userJpaRepository.save(new UserEntity(null, "other", "other@example.com"));

        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate endExclusive = LocalDate.of(2027, 1, 1);

        invoiceJpaRepository.save(new InvoiceEntity(null, user, "Client A", InvoiceStatus.PAID,
                LocalDate.of(2026, 2, 10), BigDecimal.valueOf(200), BigDecimal.valueOf(240)));
        invoiceJpaRepository.save(new InvoiceEntity(null, user, "Client A", InvoiceStatus.SENT,
                LocalDate.of(2026, 3, 10), BigDecimal.valueOf(150), BigDecimal.valueOf(180)));
        invoiceJpaRepository.save(new InvoiceEntity(null, user, "Client B", InvoiceStatus.PAID,
                LocalDate.of(2026, 4, 10), BigDecimal.valueOf(100), BigDecimal.valueOf(120)));
        invoiceJpaRepository.save(new InvoiceEntity(null, user, "Client C", InvoiceStatus.DRAFT,
                LocalDate.of(2026, 5, 10), BigDecimal.valueOf(999), BigDecimal.valueOf(999)));
        invoiceJpaRepository.save(new InvoiceEntity(null, other, "Client A", InvoiceStatus.PAID,
                LocalDate.of(2026, 6, 10), BigDecimal.valueOf(1000), BigDecimal.valueOf(1200)));

        List<ClientRevenueAggregateProjection> result = dashboardMetricsRepository.findClientRevenueDistribution(
                user.getId(),
                List.of(InvoiceStatus.PAID, InvoiceStatus.SENT),
                start,
                endExclusive
        );

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getClientName()).isEqualTo("Client A");
        assertThat(result.get(0).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(350));
        assertThat(result.get(1).getClientName()).isEqualTo("Client B");
        assertThat(result.get(1).getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }
}
