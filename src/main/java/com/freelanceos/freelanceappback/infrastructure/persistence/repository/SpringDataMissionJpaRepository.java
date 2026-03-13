package com.freelanceos.freelanceappback.infrastructure.persistence.repository;

import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpringDataMissionJpaRepository extends JpaRepository<MissionEntity, Long> {
    List<MissionEntity> findByUserIdOrderByStartDateDesc(Long userId);

    Optional<MissionEntity> findByIdAndUserId(Long id, Long userId);

    @Query("""
            select m
            from MissionEntity m
            where m.user.id = :userId
              and m.status = :status
              and m.endDate < :endDateLimit
            order by m.endDate asc
            """)
    List<MissionEntity> findExpiringMissions(@Param("userId") Long userId,
                                             @Param("status") MissionStatus status,
                                             @Param("endDateLimit") LocalDate endDateLimit);

    @Query("""
            select count(m) > 0
            from MissionEntity m
            where m.user.id = :userId
              and m.status = :status
              and (:excludeId is null or m.id <> :excludeId)
              and m.startDate <= :endDate
              and m.endDate >= :startDate
            """)
    boolean existsOverlappingMissions(@Param("userId") Long userId,
                                      @Param("status") MissionStatus status,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate,
                                      @Param("excludeId") Long excludeId);
}
