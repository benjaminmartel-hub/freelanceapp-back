package com.freelanceos.freelanceappback.domain.service.mission;

import com.freelanceos.freelanceappback.domain.model.mission.BillingType;
import com.freelanceos.freelanceappback.domain.model.client.ClientSummary;
import com.freelanceos.freelanceappback.domain.model.dashboard.InvoiceStatus;
import com.freelanceos.freelanceappback.domain.model.invoice.MissionInvoice;
import com.freelanceos.freelanceappback.domain.model.mission.Mission;
import com.freelanceos.freelanceappback.domain.model.mission.MissionDetail;
import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;
import com.freelanceos.freelanceappback.domain.ports.out.ClientRepository;
import com.freelanceos.freelanceappback.domain.ports.out.InvoiceRepository;
import com.freelanceos.freelanceappback.domain.ports.out.MissionRepository;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.domain.exception.ConflictException;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.ClientEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.MissionEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.MissionMapper;
import com.freelanceos.freelanceappback.infrastructure.persistence.projection.MissionInvoiceSummaryProjection;
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
    private ClientRepository clientRepository;

    @Mock
    private MissionMapper missionMapper;

    @InjectMocks
    private MissionService missionService;

    @Test
    void getAllShouldReturnMappedMissions() {
        UserEntity user = new UserEntity(1L, "demo", "demo@freelanceos.com");
        ClientEntity client = buildClient(user, 1L, "Maison Beldi");
        MissionEntity entity = buildMissionEntity(user, client, 1L, LocalDate.now().minusDays(5), LocalDate.now().plusDays(5));
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
        ClientEntity client = buildClient(user, 1L, "Maison Beldi");
        MissionEntity entity = buildMissionEntity(user, client, 1L, LocalDate.now().minusDays(2), LocalDate.now().plusDays(8));
        Mission mission = buildMission(entity);

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(missionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(entity));
        when(missionMapper.toDomain(entity)).thenReturn(mission);
        when(invoiceRepository.findSummariesByUserIdAndMissionId(1L, 1L)).thenReturn(List.of(
                buildProjection(10L, "INV-001", BigDecimal.valueOf(1000), InvoiceStatus.PAID),
                buildProjection(11L, "INV-002", BigDecimal.valueOf(500), InvoiceStatus.SENT)
        ));
        when(missionMapper.toDomain(any(MissionInvoiceSummaryProjection.class)))
                .thenAnswer(invocation -> {
                    MissionInvoiceSummaryProjection projection = invocation.getArgument(0);
                    return new MissionInvoice(
                            projection.getId(),
                            projection.getNumber(),
                            projection.getAmount(),
                            projection.getStatus()
                    );
                });
        when(invoiceRepository.sumTotalHtByUserIdAndMissionId(1L, 1L)).thenReturn(BigDecimal.valueOf(1500));
        when(missionMapper.toDomain(eq(mission), any(List.class), eq(BigDecimal.valueOf(1500))))
                .thenReturn(new MissionDetail(
                        mission.id(),
                        mission.userId(),
                        mission.title(),
                        mission.client(),
                        mission.dailyRate(),
                        mission.expectedDuration(),
                        mission.totalBudgetEstimated(),
                        BigDecimal.valueOf(1500),
                        mission.currency(),
                        mission.startDate(),
                        mission.endDate(),
                        mission.status(),
                        mission.billingType(),
                        mission.internalNotes(),
                        List.of(
                                new MissionInvoice(10L, "INV-001", BigDecimal.valueOf(1000), InvoiceStatus.PAID),
                                new MissionInvoice(11L, "INV-002", BigDecimal.valueOf(500), InvoiceStatus.SENT)
                        )
                ));

        Optional<MissionDetail> result = missionService.execute("demo", 1L);

        assertThat(result).isPresent();
        assertThat(result.get().invoices()).extracting(MissionInvoice::id).containsExactly(10L, 11L);
        assertThat(result.get().totalInvoiced()).isEqualByComparingTo(BigDecimal.valueOf(1500));
    }

    @Test
    void createShouldComputeTotalBudgetWhenMissing() {
        UserEntity user = new UserEntity(1L, "demo", "demo@freelanceos.com");
        ClientSummary client = new ClientSummary(1L, "Maison Beldi");
        Mission toCreate = new Mission(null, null, "Audit", client,
                BigDecimal.valueOf(600), 10, null, LocalDate.now().minusDays(1), LocalDate.now().plusDays(9),
                MissionStatus.ONGOING, BillingType.TJM, "Notes", "EUR");
        ClientEntity clientEntity = buildClient(user, 1L, "Maison Beldi");
        MissionEntity savedEntity = buildMissionEntity(user, clientEntity, 1L, toCreate.startDate(), toCreate.endDate());
        Mission savedMission = buildMission(savedEntity);

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(clientRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(clientEntity));
        when(missionRepository.existsOverlappingOngoingMission(eq(1L), any(LocalDate.class), any(LocalDate.class), eq(null)))
                .thenReturn(false);
        stubNormalizeMissionMapper();
        when(missionMapper.toEntity(any(Mission.class), eq(user), eq(clientEntity))).thenReturn(savedEntity);
        when(missionRepository.save(any(MissionEntity.class))).thenReturn(savedEntity);
        when(missionMapper.toDomain(savedEntity)).thenReturn(savedMission);

        missionService.execute("demo", toCreate);

        ArgumentCaptor<Mission> missionCaptor = ArgumentCaptor.forClass(Mission.class);
        verify(missionMapper).toEntity(missionCaptor.capture(), eq(user), eq(clientEntity));
        assertThat(missionCaptor.getValue().totalBudgetEstimated()).isEqualByComparingTo(BigDecimal.valueOf(6000));
    }

    @Test
    void createShouldRejectWhenOverlapping() {
        UserEntity user = new UserEntity(1L, "demo", "demo@freelanceos.com");
        ClientSummary client = new ClientSummary(1L, "Maison Beldi");
        Mission toCreate = new Mission(null, null, "Audit", client,
                BigDecimal.valueOf(600), 10, BigDecimal.valueOf(6000), LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(9), MissionStatus.ONGOING, BillingType.TJM, "Notes", "EUR");
        ClientEntity clientEntity = buildClient(user, 1L, "Maison Beldi");

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(clientRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(clientEntity));
        stubNormalizeMissionMapper();
        when(missionRepository.existsOverlappingOngoingMission(eq(1L), any(LocalDate.class), any(LocalDate.class), eq(null)))
                .thenReturn(true);

        assertThatThrownBy(() -> missionService.execute("demo", toCreate))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Overlapping ongoing mission");
        verify(missionRepository, never()).save(any(MissionEntity.class));
    }

    @Test
    void updateShouldReturnEmptyWhenMissing() {
        UserEntity user = new UserEntity(1L, "demo", "demo@freelanceos.com");
        Mission toUpdate = new Mission(1L, null, "Audit", new ClientSummary(1L, "Maison Beldi"),
                BigDecimal.valueOf(600), 10, BigDecimal.valueOf(6000), LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(9), MissionStatus.ONGOING, BillingType.TJM, "Notes", "EUR");

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(missionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        Optional<Mission> result = missionService.execute("demo", 1L, toUpdate);

        assertThat(result).isEmpty();
        verify(missionRepository, never()).update(eq(1L), any(MissionEntity.class));
    }

    @Test
    void updateShouldRejectWhenOverlapping() {
        UserEntity user = new UserEntity(1L, "demo", "demo@freelanceos.com");
        ClientEntity client = buildClient(user, 1L, "Maison Beldi");
        MissionEntity existing = buildMissionEntity(user, client, 1L, LocalDate.now().minusDays(2), LocalDate.now().plusDays(8));
        Mission toUpdate = new Mission(1L, null, "Audit", new ClientSummary(1L, "Maison Beldi"),
                BigDecimal.valueOf(600), 10, BigDecimal.valueOf(6000), LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(9), MissionStatus.ONGOING, BillingType.TJM, "Notes", "EUR");

        when(userRepository.findByNameIgnoreCase("demo")).thenReturn(Optional.of(user));
        when(clientRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(client));
        when(missionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(existing));
        stubNormalizeMissionMapper();
        when(missionRepository.existsOverlappingOngoingMission(eq(1L), any(LocalDate.class), any(LocalDate.class), eq(1L)))
                .thenReturn(true);

        assertThatThrownBy(() -> missionService.execute("demo", 1L, toUpdate))
                .isInstanceOf(ConflictException.class);
        verify(missionRepository, never()).update(eq(1L), any(MissionEntity.class));
    }

    @Test
    void findExpiringShouldFilterPastMissions() {
        UserEntity user = new UserEntity(1L, "demo", "demo@freelanceos.com");
        ClientEntity client = buildClient(user, 1L, "Maison Beldi");
        MissionEntity future = buildMissionEntity(user, client, 1L, LocalDate.now().minusDays(2), LocalDate.now().plusDays(5));
        MissionEntity past = buildMissionEntity(user, client, 2L, LocalDate.now().minusDays(10), LocalDate.now().minusDays(1));
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
        Mission mission = new Mission(1L, 1L, "Audit", new ClientSummary(1L, "Maison Beldi"),
                BigDecimal.valueOf(600), 10, BigDecimal.valueOf(6000), LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(9), MissionStatus.ONGOING, BillingType.TJM, "Notes", "EUR");

        BigDecimal result = missionService.calculateProfitability(mission, 5, BigDecimal.valueOf(500));

        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(2500));
    }

    private ClientEntity buildClient(UserEntity user, Long id, String name) {
        return new ClientEntity(id, user, name, "contact@maisonbeldi.com");
    }

    private MissionEntity buildMissionEntity(UserEntity user, ClientEntity client, Long id, LocalDate startDate, LocalDate endDate) {
        return new MissionEntity(id, user, client, "Audit",
                BigDecimal.valueOf(600), 10, BigDecimal.valueOf(6000), startDate, endDate,
                MissionStatus.ONGOING, BillingType.TJM, "Notes", "EUR");
    }

    private Mission buildMission(MissionEntity entity) {
        return new Mission(entity.getId(), entity.getUser().getId(), entity.getTitle(),
                new ClientSummary(entity.getClient().getId(), entity.getClient().getName()),
                entity.getDailyRate(), entity.getExpectedDuration(),
                entity.getTotalBudgetEstimated(), entity.getStartDate(), entity.getEndDate(),
                entity.getStatus(), entity.getBillingType(), entity.getInternalNotes(), entity.getCurrency());
    }

    private void stubNormalizeMissionMapper() {
        when(missionMapper.toDomain(any(Mission.class), any(Long.class), any(ClientEntity.class), any(BigDecimal.class), any(String.class)))
                .thenAnswer(invocation -> {
                    Mission mission = invocation.getArgument(0);
                    Long userId = invocation.getArgument(1);
                    ClientEntity client = invocation.getArgument(2);
                    BigDecimal totalBudget = invocation.getArgument(3);
                    String currency = invocation.getArgument(4);
                    return new Mission(
                            mission.id(),
                            userId,
                            mission.title(),
                            new ClientSummary(client.getId(), client.getName()),
                            mission.dailyRate(),
                            mission.expectedDuration(),
                            totalBudget,
                            mission.startDate(),
                            mission.endDate(),
                            mission.status(),
                            mission.billingType(),
                            mission.internalNotes(),
                            currency
                    );
                });
    }

    private MissionInvoiceSummaryProjection buildProjection(Long id,
                                                            String number,
                                                            BigDecimal amount,
                                                            InvoiceStatus status) {
        return new MissionInvoiceSummaryProjection() {
            @Override
            public Long getId() {
                return id;
            }

            @Override
            public String getNumber() {
                return number;
            }

            @Override
            public BigDecimal getAmount() {
                return amount;
            }

            @Override
            public InvoiceStatus getStatus() {
                return status;
            }
        };
    }
}
