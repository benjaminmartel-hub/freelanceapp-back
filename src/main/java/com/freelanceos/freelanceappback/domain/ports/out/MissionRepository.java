package com.freelanceos.freelanceappback.domain.ports.out;

import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.MissionEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MissionRepository {
    List<MissionEntity> findByUserId(Long userId);

    Optional<MissionEntity> findByIdAndUserId(Long id, Long userId);

    MissionEntity save(MissionEntity mission);

    Optional<MissionEntity> update(Long id, MissionEntity mission);

    boolean existsOverlappingOngoingMission(Long userId, LocalDate startDate, LocalDate endDate, Long excludeId);

    List<MissionEntity> findExpiringMissions(Long userId, MissionStatus status, LocalDate endDateLimit);
}
