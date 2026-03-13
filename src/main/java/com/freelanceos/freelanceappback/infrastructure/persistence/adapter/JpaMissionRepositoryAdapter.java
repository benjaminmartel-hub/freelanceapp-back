package com.freelanceos.freelanceappback.infrastructure.persistence.adapter;

import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;
import com.freelanceos.freelanceappback.domain.ports.out.MissionRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.MissionEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.repository.SpringDataMissionJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaMissionRepositoryAdapter implements MissionRepository {
    private final SpringDataMissionJpaRepository missionJpaRepository;

    public JpaMissionRepositoryAdapter(SpringDataMissionJpaRepository missionJpaRepository) {
        this.missionJpaRepository = missionJpaRepository;
    }

    @Override
    public List<MissionEntity> findByUserId(Long userId) {
        return missionJpaRepository.findByUserIdOrderByStartDateDesc(userId);
    }

    @Override
    public Optional<MissionEntity> findByIdAndUserId(Long id, Long userId) {
        return missionJpaRepository.findByIdAndUserId(id, userId);
    }

    @Override
    public MissionEntity save(MissionEntity mission) {
        mission.setId(null);
        return missionJpaRepository.save(mission);
    }

    @Override
    public Optional<MissionEntity> update(Long id, MissionEntity mission) {
        if (!missionJpaRepository.existsById(id)) {
            return Optional.empty();
        }
        mission.setId(id);
        return Optional.of(missionJpaRepository.save(mission));
    }

    @Override
    public boolean existsOverlappingOngoingMission(Long userId, LocalDate startDate, LocalDate endDate, Long excludeId) {
        return missionJpaRepository.existsOverlappingMissions(userId, MissionStatus.ONGOING, startDate, endDate, excludeId);
    }

    @Override
    public List<MissionEntity> findExpiringMissions(Long userId, MissionStatus status, LocalDate endDateLimit) {
        return missionJpaRepository.findExpiringMissions(userId, status, endDateLimit);
    }
}
