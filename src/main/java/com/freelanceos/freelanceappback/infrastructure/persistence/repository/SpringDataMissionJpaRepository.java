package com.freelanceos.freelanceappback.infrastructure.persistence.repository;

import com.freelanceos.freelanceappback.domain.model.dashboard.MissionStatus;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SpringDataMissionJpaRepository extends JpaRepository<MissionEntity, Long> {
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
}
