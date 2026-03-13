package com.freelanceos.freelanceappback.infrastructure.persistence.adapter;

import com.freelanceos.freelanceappback.domain.model.dashboard.InvoiceStatus;
import com.freelanceos.freelanceappback.domain.ports.out.InvoiceRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.InvoiceEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never"
})
@Import(JpaInvoiceRepositoryAdapter.class)
class JpaInvoiceRepositoryAdapterTest {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findIdsByUserIdAndClientNameShouldReturnMatchingIds() {
        UserEntity user = new UserEntity(null, "demo", "demo@freelanceos.com");
        entityManager.persist(user);
        entityManager.flush();
        InvoiceEntity invoice1 = new InvoiceEntity(null, user, "Maison Beldi", InvoiceStatus.SENT,
                LocalDate.now().plusDays(5), BigDecimal.valueOf(1000), BigDecimal.valueOf(1200));
        InvoiceEntity invoice2 = new InvoiceEntity(null, user, "Maison Beldi", InvoiceStatus.PAID,
                LocalDate.now().minusDays(5), BigDecimal.valueOf(2000), BigDecimal.valueOf(2400));
        entityManager.persist(invoice1);
        entityManager.persist(invoice2);
        entityManager.flush();

        List<Long> result = invoiceRepository.findIdsByUserIdAndClientName(user.getId(), "Maison Beldi");

        assertThat(result).containsExactly(invoice1.getId(), invoice2.getId());
    }

    @Test
    void findIdsByUserIdAndClientNameShouldReturnEmptyWhenMissing() {
        UserEntity user = new UserEntity(null, "demo", "demo@freelanceos.com");
        entityManager.persist(user);
        entityManager.flush();

        List<Long> result = invoiceRepository.findIdsByUserIdAndClientName(user.getId(), "Unknown");

        assertThat(result).isEmpty();
    }
}
