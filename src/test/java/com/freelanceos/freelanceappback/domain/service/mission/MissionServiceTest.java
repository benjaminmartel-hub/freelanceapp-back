package com.freelanceos.freelanceappback.domain.service.mission;

import com.freelanceos.freelanceappback.domain.model.mission.BillingType;
import com.freelanceos.freelanceappback.domain.model.mission.Mission;
import com.freelanceos.freelanceappback.domain.model.mission.MissionDetail;
import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;
import com.freelanceos.freelanceappback.domain.ports.out.InvoiceRepository;
import com.freelanceos.freelanceappback.domain.ports.out.MissionRepository;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.MissionEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.MissionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MissionServiceTest {

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private MissionMapper missionMapper;

    @InjectMocks
    private MissionService missionService;

    @Test
    void getAllShouldReturnMappedMissions() {
        UserEntity user = new UserEntity(1L, "demo", "demo@freelanceos.com");
        MissionEntity entity = buildMissionEntity(user, 1L, LocalDate.now().minusDays(5), LocalDate.now().plusDays(5));
        Mission mission = buildMission(entity);

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(missionRepository.findByUserId(1L)).thenReturn(List.of(entity));
        when(missionMapper.toDomain(entity)).thenReturn(mission);

        List<Mission> result = missionService.execute("demo");

        assertThat(result).containsExactly(mission);
        verify(missionRepository).findByUserId(1L);
    }

    @Test
    void getDetailShouldIncludeInvoiceIds() {
        UserEntity user = new UserEntity(1L, "demo", "demo@freelanceos.com");
        MissionEntity entity = buildMissionEntity(user, 1L, LocalDate.now().minusDays(2), LocalDate.now().plusDays(8));
        Mission mission = buildMission(entity);

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(missionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(entity));
        when(missionMapper.toDomain(entity)).thenReturn(mission);
        when(invoiceRepository.findIdsByUserIdAndClientName(1L, "Maison Beldi")).thenReturn(List.of(10L, 11L));

        Optional<MissionDetail> result = missionService.execute("demo", 1L);

        assertThat(result).isPresent();
        assertThat(result.get().invoiceIds()).containsExactly(10L, 11L);
    }

    @Test
    void createShouldComputeTotalBudgetWhenMissing() {
        UserEntity user = new UserEntity(1L, "demo", "demo@freelanceos.com");
        Mission toCreate = new Mission(null, null, "Audit", "Maison Beldi", "contact@maisonbeldi.com",
                BigDecimal.valueOf(600), 10, null, LocalDate.now().minusDays(1), LocalDate.now().plusDays(9),
                MissionStatus.ONGOING, BillingType.TJM, "Notes");
        MissionEntity savedEntity = buildMissionEntity(user, 1L, toCreate.startDate(), toCreate.endDate());
        Mission savedMission = buildMission(savedEntity);

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(missionRepository.existsOverlappingOngoingMission(eq(1L), any(LocalDate.class), any(LocalDate.class), eq(null)))
                .thenReturn(false);
        when(missionMapper.toEntity(any(Mission.class), eq(user))).thenReturn(savedEntity);
        when(missionRepository.save(any(MissionEntity.class))).thenReturn(savedEntity);
        when(missionMapper.toDomain(savedEntity)).thenReturn(savedMission);

        missionService.execute("demo", toCreate);

        ArgumentCaptor<Mission> missionCaptor = ArgumentCaptor.forClass(Mission.class);
        verify(missionMapper).toEntity(missionCaptor.capture(), eq(user));
        assertThat(missionCaptor.getValue().totalBudgetEstimated()).isEqualByComparingTo(BigDecimal.valueOf(6000));
    }

    @Test
    void createShouldRejectWhenOverlapping() {
        UserEntity user = new UserEntity(1L, "demo", "demo@freelanceos.com");
        Mission toCreate = new Mission(null, null, "Audit", "Maison Beldi", "contact@maisonbeldi.com",
                BigDecimal.valueOf(600), 10, BigDecimal.valueOf(6000), LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(9), MissionStatus.ONGOING, BillingType.TJM, "Notes");

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(missionRepository.existsOverlappingOngoingMission(eq(1L), any(LocalDate.class), any(LocalDate.class), eq(null)))
                .thenReturn(true);

        assertThatThrownBy(() -> missionService.execute("demo", toCreate))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Overlapping ongoing mission");
        verify(missionRepository, never()).save(any(MissionEntity.class));
    }

    @Test
    void updateShouldReturnEmptyWhenMissing() {
        UserEntity user = new UserEntity(1L, "demo", "demo@freelanceos.com");
        Mission toUpdate = new Mission(1L, null, "Audit", "Maison Beldi", "contact@maisonbeldi.com",
                BigDecimal.valueOf(600), 10, BigDecimal.valueOf(6000), LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(9), MissionStatus.ONGOING, BillingType.TJM, "Notes");

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(missionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        Optional<Mission> result = missionService.execute("demo", 1L, toUpdate);

        assertThat(result).isEmpty();
        verify(missionRepository, never()).update(eq(1L), any(MissionEntity.class));
    }

    @Test
    void updateShouldRejectWhenOverlapping() {
        UserEntity user = new UserEntity(1L, "demo", "demo@freelanceos.com");
        MissionEntity existing = buildMissionEntity(user, 1L, LocalDate.now().minusDays(2), LocalDate.now().plusDays(8));
        Mission toUpdate = new Mission(1L, null, "Audit", "Maison Beldi", "contact@maisonbeldi.com",
                BigDecimal.valueOf(600), 10, BigDecimal.valueOf(6000), LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(9), MissionStatus.ONGOING, BillingType.TJM, "Notes");

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(missionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(existing));
        when(missionRepository.existsOverlappingOngoingMission(eq(1L), any(LocalDate.class), any(LocalDate.class), eq(1L)))
                .thenReturn(true);

        assertThatThrownBy(() -> missionService.execute("demo", 1L, toUpdate))
                .isInstanceOf(IllegalStateException.class);
        verify(missionRepository, never()).update(eq(1L), any(MissionEntity.class));
    }

    @Test
    void findExpiringShouldFilterPastMissions() {
        UserEntity user = new UserEntity(1L, "demo", "demo@freelanceos.com");
        MissionEntity future = buildMissionEntity(user, 1L, LocalDate.now().minusDays(2), LocalDate.now().plusDays(5));
        MissionEntity past = buildMissionEntity(user, 2L, LocalDate.now().minusDays(10), LocalDate.now().minusDays(1));
        Mission futureMission = buildMission(future);

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(missionRepository.findExpiringMissions(eq(1L), eq(MissionStatus.ONGOING), any(LocalDate.class)))
                .thenReturn(List.of(future, past));
        when(missionMapper.toDomain(future)).thenReturn(futureMission);

        List<Mission> result = missionService.findExpiringMissions("demo", 15);

        assertThat(result).containsExactly(futureMission);
    }

    @Test
    void calculateProfitabilityShouldComputeNet() {
        Mission mission = new Mission(1L, 1L, "Audit", "Maison Beldi", "contact@maisonbeldi.com",
                BigDecimal.valueOf(600), 10, BigDecimal.valueOf(6000), LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(9), MissionStatus.ONGOING, BillingType.TJM, "Notes");

        BigDecimal result = missionService.calculateProfitability(mission, 5, BigDecimal.valueOf(500));

        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(2500));
    }

    private MissionEntity buildMissionEntity(UserEntity user, Long id, LocalDate startDate, LocalDate endDate) {
        return new MissionEntity(id, user, "Audit", "Maison Beldi", "contact@maisonbeldi.com",
                BigDecimal.valueOf(600), 10, BigDecimal.valueOf(6000), startDate, endDate,
                MissionStatus.ONGOING, BillingType.TJM, "Notes");
    }

    private Mission buildMission(MissionEntity entity) {
        return new Mission(entity.getId(), entity.getUser().getId(), entity.getTitle(), entity.getClientName(),
                entity.getClientContactEmail(), entity.getDailyRate(), entity.getExpectedDuration(),
                entity.getTotalBudgetEstimated(), entity.getStartDate(), entity.getEndDate(),
                entity.getStatus(), entity.getBillingType(), entity.getInternalNotes());
    }
}
