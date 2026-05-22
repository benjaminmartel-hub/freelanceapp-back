package com.freelanceos.freelanceappback.infrastructure.persistence.adapter;

import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStatus;
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
    void findInvoiceSummariesForMissionShouldReturnMatchingIds() {
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

        var result = invoiceRepository.findInvoiceSummariesForMission(user.getId(), mission.getId());

        assertThat(result).extracting(summary -> summary.getId()).containsExactly(invoice1.getId(), invoice2.getId());
    }

    @Test
    void findInvoiceSummariesForMissionShouldReturnEmptyWhenMissing() {
        UserEntity user = new UserEntity(null, "demo", "demo@freelanceos.com");
        entityManager.persist(user);
        entityManager.flush();

        var result = invoiceRepository.findInvoiceSummariesForMission(user.getId(), 999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findByUserIdAndFindByIdAndUserIdShouldScopeInvoicesToOwner() {
        UserEntity user = new UserEntity(null, "demo", "demo@freelanceos.com");
        UserEntity other = new UserEntity(null, "other", "other@freelanceos.com");
        entityManager.persist(user);
        entityManager.persist(other);
        ClientEntity client = new ClientEntity(null, user, "Maison Beldi", "contact@maisonbeldi.com");
        ClientEntity otherClient = new ClientEntity(null, other, "Other Client", "other@example.com");
        entityManager.persist(client);
        entityManager.persist(otherClient);
        MissionEntity mission = new MissionEntity(null, user, client, "Audit",
                BigDecimal.valueOf(600), 10, BigDecimal.valueOf(6000),
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(5),
                MissionStatus.ONGOING, BillingType.TJM, "Notes", "EUR");
        MissionEntity otherMission = new MissionEntity(null, other, otherClient, "Other Audit",
                BigDecimal.valueOf(600), 10, BigDecimal.valueOf(6000),
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(5),
                MissionStatus.ONGOING, BillingType.TJM, "Notes", "EUR");
        entityManager.persist(mission);
        entityManager.persist(otherMission);
        entityManager.flush();

        InvoiceEntity invoice = new InvoiceEntity(null, user, mission, "FAC-2026-001", InvoiceStatus.SENT,
                LocalDate.now(), LocalDate.now().plusDays(15),
                BigDecimal.valueOf(1000), BigDecimal.valueOf(0.2000), BigDecimal.valueOf(1200));
        InvoiceEntity otherInvoice = new InvoiceEntity(null, other, otherMission, "FAC-2026-002", InvoiceStatus.PAID,
                LocalDate.now(), LocalDate.now().plusDays(15),
                BigDecimal.valueOf(2000), BigDecimal.valueOf(0.2000), BigDecimal.valueOf(2400));
        entityManager.persist(invoice);
        entityManager.persist(otherInvoice);
        entityManager.flush();

        assertThat(invoiceRepository.findByUserId(user.getId()))
                .extracting(InvoiceEntity::getNumber)
                .containsExactly("FAC-2026-001");
        assertThat(invoiceRepository.findByIdAndUserId(otherInvoice.getId(), user.getId())).isEmpty();
        assertThat(invoiceRepository.findByIdAndUserId(invoice.getId(), user.getId())).isPresent();
        assertThat(invoiceRepository.sumTotalHtByUserIdAndStatus(user.getId(), InvoiceStatus.SENT))
                .isEqualByComparingTo(BigDecimal.valueOf(1000));
    }
}
