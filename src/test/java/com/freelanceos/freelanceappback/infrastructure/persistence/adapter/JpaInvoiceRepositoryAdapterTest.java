package com.freelanceos.freelanceappback.infrastructure.persistence.adapter;

import com.freelanceos.freelanceappback.domain.model.dashboard.InvoiceStatus;
import com.freelanceos.freelanceappback.domain.model.mission.BillingType;
import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;
import com.freelanceos.freelanceappback.domain.ports.out.InvoiceRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.InvoiceEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.ClientEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.MissionEntity;
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
    void findSummariesByUserIdAndMissionIdShouldReturnMatchingIds() {
        UserEntity user = new UserEntity(null, "demo", "demo@freelanceos.com");
        entityManager.persist(user);
        ClientEntity client = new ClientEntity(null, user, "Maison Beldi", "contact@maisonbeldi.com");
        entityManager.persist(client);
        entityManager.flush();
        MissionEntity mission = new MissionEntity(null, user, client, "Audit",
                BigDecimal.valueOf(600), 10, BigDecimal.valueOf(6000),
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(5),
                MissionStatus.ONGOING, BillingType.TJM, "Notes", "EUR");
        entityManager.persist(mission);
        entityManager.flush();
        InvoiceEntity invoice1 = new InvoiceEntity(null, user, mission, "INV-001", InvoiceStatus.SENT,
                LocalDate.now().plusDays(5), BigDecimal.valueOf(1000), BigDecimal.valueOf(1200));
        InvoiceEntity invoice2 = new InvoiceEntity(null, user, mission, "INV-002", InvoiceStatus.PAID,
                LocalDate.now().minusDays(5), BigDecimal.valueOf(2000), BigDecimal.valueOf(2400));
        entityManager.persist(invoice1);
        entityManager.persist(invoice2);
        entityManager.flush();

        var result = invoiceRepository.findSummariesByUserIdAndMissionId(user.getId(), mission.getId());

        assertThat(result).extracting(summary -> summary.getId()).containsExactly(invoice1.getId(), invoice2.getId());
    }

    @Test
    void findSummariesByUserIdAndMissionIdShouldReturnEmptyWhenMissing() {
        UserEntity user = new UserEntity(null, "demo", "demo@freelanceos.com");
        entityManager.persist(user);
        entityManager.flush();

        var result = invoiceRepository.findSummariesByUserIdAndMissionId(user.getId(), 999L);

        assertThat(result).isEmpty();
    }
}
