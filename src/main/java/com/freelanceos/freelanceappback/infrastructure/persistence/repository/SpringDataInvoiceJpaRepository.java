package com.freelanceos.freelanceappback.infrastructure.persistence.repository;

import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStatus;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.InvoiceEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.projection.ClientRevenueAggregateProjection;
import com.freelanceos.freelanceappback.infrastructure.persistence.projection.InvoiceSummaryForMissionProjection;
import com.freelanceos.freelanceappback.infrastructure.persistence.projection.MonthlyRevenueAggregateProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpringDataInvoiceJpaRepository extends JpaRepository<InvoiceEntity, Long> {
    @Query("""
            select i
            from InvoiceEntity i
            join fetch i.mission m
            join fetch m.client
            where i.user.id = :userId
            order by i.issueDate desc, i.id desc
            """)
    List<InvoiceEntity> findByUserIdWithMissionAndClient(@Param("userId") Long userId);

    @Query("""
            select i
            from InvoiceEntity i
            join fetch i.mission m
            join fetch m.client
            where i.id = :id
              and i.user.id = :userId
            """)
    Optional<InvoiceEntity> findByIdAndUserIdWithMissionAndClient(@Param("id") Long id, @Param("userId") Long userId);

    @Query("""
            select count(i)
            from InvoiceEntity i
            where year(i.issueDate) = :year
            """)
    long countByIssueYear(@Param("year") int year);

    @Query("""
            select sum(i.totalHt)
            from InvoiceEntity i
            where i.user.id = :userId
              and i.status in :statuses
              and i.dueDate >= :startDate
              and i.dueDate < :endDateExclusive
            """)
    BigDecimal sumTotalHtByUserAndStatusesAndDateRange(@Param("userId") Long userId,
                                                       @Param("statuses") List<InvoiceStatus> statuses,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDateExclusive") LocalDate endDateExclusive);

    @Query("""
            select coalesce(sum(i.totalHt), 0)
            from InvoiceEntity i
            where i.user.id = :userId
              and i.status = :status
            """)
    BigDecimal sumTotalHtByUserAndStatus(@Param("userId") Long userId, @Param("status") InvoiceStatus status);

    @Query("""
            select year(i.dueDate) as year,
                   month(i.dueDate) as month,
                   i.status as status,
                   sum(i.totalHt) as amount
            from InvoiceEntity i
            where i.user.id = :userId
              and i.status in :statuses
              and i.dueDate >= :startDate
              and i.dueDate < :endDateExclusive
            group by year(i.dueDate), month(i.dueDate), i.status
            order by year(i.dueDate), month(i.dueDate), i.status
            """)
    List<MonthlyRevenueAggregateProjection> findMonthlyRevenueHistory(@Param("userId") Long userId,
                                                                      @Param("statuses") List<InvoiceStatus> statuses,
                                                                      @Param("startDate") LocalDate startDate,
                                                                      @Param("endDateExclusive") LocalDate endDateExclusive);

    @Query("""
            select i.mission.client.name as clientName,
                   sum(i.totalHt) as amount
            from InvoiceEntity i
            where i.user.id = :userId
              and i.status in :statuses
              and i.dueDate >= :startDate
              and i.dueDate < :endDateExclusive
            group by i.mission.client.name
            order by sum(i.totalHt) desc
            """)
    List<ClientRevenueAggregateProjection> findClientRevenueDistribution(
            @Param("userId") Long userId,
            @Param("statuses") List<InvoiceStatus> statuses,
            @Param("startDate") LocalDate startDate,
            @Param("endDateExclusive") LocalDate endDateExclusive);

    @Query("""
            select i
            from InvoiceEntity i
            where i.user.id = :userId
              and i.status = :status
              and i.dueDate < :today
            order by i.dueDate asc
            """)
    List<InvoiceEntity> findOverdueInvoices(@Param("userId") Long userId,
                                            @Param("status") InvoiceStatus status,
                                            @Param("today") LocalDate today);

    @Query("""
            select i.id as id,
                   i.number as number,
                   i.totalHt as amount,
                   i.status as status
            from InvoiceEntity i
            where i.user.id = :userId
              and i.mission.id = :missionId
            order by i.dueDate desc
            """)
    List<InvoiceSummaryForMissionProjection> findInvoiceSummariesForMission(@Param("userId") Long userId,
                                                                            @Param("missionId") Long missionId);

    @Query("""
            select coalesce(sum(i.totalHt), 0)
            from InvoiceEntity i
            where i.user.id = :userId
              and i.mission.id = :missionId
            """)
    BigDecimal sumTotalHtByUserIdAndMissionId(@Param("userId") Long userId,
                                              @Param("missionId") Long missionId);

}
