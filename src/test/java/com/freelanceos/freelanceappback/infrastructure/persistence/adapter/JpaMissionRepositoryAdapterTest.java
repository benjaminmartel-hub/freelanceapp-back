package com.freelanceos.freelanceappback.infrastructure.persistence.adapter;

import com.freelanceos.freelanceappback.domain.model.mission.BillingType;
import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;
import com.freelanceos.freelanceappback.domain.ports.out.MissionRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never"
})
@Import(JpaMissionRepositoryAdapter.class)
class JpaMissionRepositoryAdapterTest {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void saveAndFindByUserIdShouldReturnMissions() {
        UserEntity user = new UserEntity(null, "demo", "demo@freelanceos.com");
        entityManager.persist(user);
        entityManager.flush();
        missionRepository.save(buildMissionEntity(user, null, "Mission A", LocalDate.now().minusDays(5), LocalDate.now().plusDays(5)));
        missionRepository.save(buildMissionEntity(user, null, "Mission B", LocalDate.now().minusDays(10), LocalDate.now().plusDays(2)));

        List<MissionEntity> missions = missionRepository.findByUserId(user.getId());

        assertThat(missions).hasSize(2);
        assertThat(missions).extracting(MissionEntity::getId).allMatch(id -> id != null);
    }

    @Test
    void findByIdAndUserIdShouldReturnMission() {
        UserEntity user = new UserEntity(null, "demo", "demo@freelanceos.com");
        entityManager.persist(user);
        entityManager.flush();
        MissionEntity saved = missionRepository.save(buildMissionEntity(user, null, "Mission A", LocalDate.now(), LocalDate.now().plusDays(5)));

        Optional<MissionEntity> result = missionRepository.findByIdAndUserId(saved.getId(), user.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Mission A");
    }

    @Test
    void updateShouldReturnUpdatedMission() {
        UserEntity user = new UserEntity(null, "demo", "demo@freelanceos.com");
        entityManager.persist(user);
        entityManager.flush();
        MissionEntity saved = missionRepository.save(buildMissionEntity(user, null, "Mission A", LocalDate.now(), LocalDate.now().plusDays(5)));
        MissionEntity update = buildMissionEntity(user, null, "Mission Updated", LocalDate.now(), LocalDate.now().plusDays(10));

        Optional<MissionEntity> result = missionRepository.update(saved.getId(), update);

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Mission Updated");
        assertThat(result.get().getEndDate()).isEqualTo(LocalDate.now().plusDays(10));
    }

    @Test
    void existsOverlappingOngoingMissionShouldDetectOverlap() {
        UserEntity user = new UserEntity(null, "demo", "demo@freelanceos.com");
        entityManager.persist(user);
        entityManager.flush();
        missionRepository.save(buildMissionEntity(user, null, "Mission A", LocalDate.now().minusDays(2), LocalDate.now().plusDays(5)));

        boolean overlaps = missionRepository.existsOverlappingOngoingMission(
                user.getId(), LocalDate.now().minusDays(1), LocalDate.now().plusDays(2), null);
        boolean noOverlap = missionRepository.existsOverlappingOngoingMission(
                user.getId(), LocalDate.now().plusDays(6), LocalDate.now().plusDays(10), null);

        assertThat(overlaps).isTrue();
        assertThat(noOverlap).isFalse();
    }

    @Test
    void findExpiringMissionsShouldReturnSortedByEndDate() {
        UserEntity user = new UserEntity(null, "demo", "demo@freelanceos.com");
        entityManager.persist(user);
        entityManager.flush();
        MissionEntity later = missionRepository.save(buildMissionEntity(user, null, "Later", LocalDate.now(), LocalDate.now().plusDays(10)));
        MissionEntity sooner = missionRepository.save(buildMissionEntity(user, null, "Sooner", LocalDate.now(), LocalDate.now().plusDays(3)));

        List<MissionEntity> result = missionRepository.findExpiringMissions(user.getId(), MissionStatus.ONGOING, LocalDate.now().plusDays(15));

        assertThat(result).containsExactly(sooner, later);
    }

    private MissionEntity buildMissionEntity(UserEntity user, Long id, String title, LocalDate startDate, LocalDate endDate) {
        return new MissionEntity(id, user, title, "Maison Beldi", "contact@maisonbeldi.com",
                BigDecimal.valueOf(600), 10, BigDecimal.valueOf(6000), startDate, endDate,
                MissionStatus.ONGOING, BillingType.TJM, "Notes");
    }
}
