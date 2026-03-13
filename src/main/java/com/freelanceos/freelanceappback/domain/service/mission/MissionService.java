package com.freelanceos.freelanceappback.domain.service.mission;

import com.freelanceos.freelanceappback.domain.model.mission.Mission;
import com.freelanceos.freelanceappback.domain.model.mission.MissionDetail;
import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;
import com.freelanceos.freelanceappback.domain.ports.in.mission.CreateMissionUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.mission.GetAllMissionsUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.mission.GetMissionDetailUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.mission.UpdateMissionUseCase;
import com.freelanceos.freelanceappback.domain.ports.out.InvoiceRepository;
import com.freelanceos.freelanceappback.domain.ports.out.MissionRepository;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.MissionEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.MissionMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MissionService implements CreateMissionUseCase,
        UpdateMissionUseCase,
        GetAllMissionsUseCase,
        GetMissionDetailUseCase {

    private final MissionRepository missionRepository;
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final MissionMapper missionMapper;

    public MissionService(MissionRepository missionRepository,
                          UserRepository userRepository,
                          InvoiceRepository invoiceRepository,
                          MissionMapper missionMapper) {
        this.missionRepository = missionRepository;
        this.userRepository = userRepository;
        this.invoiceRepository = invoiceRepository;
        this.missionMapper = missionMapper;
    }

    @Override
    public List<Mission> execute(String username) {
        Long userId = resolveUserId(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return missionRepository.findByUserId(userId).stream()
                .map(missionMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<MissionDetail> execute(String username, Long id) {
        Long userId = resolveUserId(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return missionRepository.findByIdAndUserId(id, userId)
                .map(missionMapper::toDomain)
                .map(mission -> new MissionDetail(
                        mission.id(),
                        mission.userId(),
                        mission.title(),
                        mission.clientName(),
                        mission.clientContactEmail(),
                        mission.dailyRate(),
                        mission.expectedDuration(),
                        mission.totalBudgetEstimated(),
                        mission.startDate(),
                        mission.endDate(),
                        mission.status(),
                        mission.billingType(),
                        mission.internalNotes(),
                        invoiceRepository.findIdsByUserIdAndClientName(userId, mission.clientName())
                ));
    }

    @Override
    public Mission execute(String username, Mission missionToCreate) {
        UserEntity user = resolveUser(username);
        Mission normalized = normalizeMission(missionToCreate, user.getId());
        validateNoOverlap(user.getId(), normalized.startDate(), normalized.endDate(), null, normalized.status());
        MissionEntity saved = missionRepository.save(missionMapper.toEntity(normalized, user));
        return missionMapper.toDomain(saved);
    }

    @Override
    public Optional<Mission> execute(String username, Long id, Mission missionToUpdate) {
        Long userId = resolveUserId(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Optional<MissionEntity> existing = missionRepository.findByIdAndUserId(id, userId);
        if (existing.isEmpty()) {
            return Optional.empty();
        }

        Mission normalized = normalizeMission(missionToUpdate, userId);
        validateNoOverlap(userId, normalized.startDate(), normalized.endDate(), id, normalized.status());
        MissionEntity updated = missionRepository.update(id, missionMapper.toEntity(normalized, existing.get().getUser()))
                .orElseThrow(() -> new IllegalStateException("Mission not found"));
        return Optional.of(missionMapper.toDomain(updated));
    }

    public BigDecimal calculateProfitability(Mission mission, int daysWorked, BigDecimal estimatedCharges) {
        BigDecimal dailyRate = mission.dailyRate() == null ? BigDecimal.ZERO : mission.dailyRate();
        BigDecimal charges = estimatedCharges == null ? BigDecimal.ZERO : estimatedCharges;
        BigDecimal revenue = dailyRate.multiply(BigDecimal.valueOf(Math.max(daysWorked, 0)));
        return revenue.subtract(charges);
    }

    public List<Mission> findExpiringMissions(String username, int daysThreshold) {
        Long userId = resolveUserId(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(daysThreshold);
        return missionRepository.findExpiringMissions(userId, MissionStatus.ONGOING, limit).stream()
                .filter(mission -> !mission.getEndDate().isBefore(today))
                .map(missionMapper::toDomain)
                .toList();
    }

    private Mission normalizeMission(Mission mission, Long userId) {
        BigDecimal totalBudget = mission.totalBudgetEstimated();
        if (totalBudget == null && mission.dailyRate() != null && mission.expectedDuration() != null) {
            totalBudget = mission.dailyRate().multiply(BigDecimal.valueOf(mission.expectedDuration()));
        }

        return new Mission(
                mission.id(),
                userId,
                mission.title(),
                mission.clientName(),
                mission.clientContactEmail(),
                mission.dailyRate(),
                mission.expectedDuration(),
                totalBudget,
                mission.startDate(),
                mission.endDate(),
                mission.status(),
                mission.billingType(),
                mission.internalNotes()
        );
    }

    private void validateNoOverlap(Long userId,
                                   LocalDate startDate,
                                   LocalDate endDate,
                                   Long excludeId,
                                   MissionStatus status) {
        if (status != MissionStatus.ONGOING || startDate == null || endDate == null) {
            return;
        }
        boolean overlap = missionRepository.existsOverlappingOngoingMission(userId, startDate, endDate, excludeId);
        if (overlap) {
            throw new IllegalStateException("Overlapping ongoing mission detected");
        }
    }

    private UserEntity resolveUser(String username) {
        return userRepository.findByNameIgnoreCase(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private Optional<Long> resolveUserId(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }

        return userRepository.findByNameIgnoreCase(username)
                .map(UserEntity::getId);
    }
}
