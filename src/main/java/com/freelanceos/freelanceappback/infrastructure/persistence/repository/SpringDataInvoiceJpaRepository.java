package com.freelanceos.freelanceappback.infrastructure.persistence.repository;

import com.freelanceos.freelanceappback.domain.model.dashboard.InvoiceStatus;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.InvoiceEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.projection.MonthlyRevenueAggregateProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface SpringDataInvoiceJpaRepository extends JpaRepository<InvoiceEntity, Long> {
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
            select sum(i.totalHt)
            from InvoiceEntity i
            where i.user.id = :userId
              and i.status = :status
            """)
    BigDecimal sumTotalHtByUserAndStatus(@Param("userId") Long userId, @Param("status") InvoiceStatus status);

    @Query("""
            select year(i.dueDate) as year,
                   month(i.dueDate) as month,
                   sum(i.totalHt) as amount
            from InvoiceEntity i
            where i.user.id = :userId
              and i.status in :statuses
              and i.dueDate >= :startDate
              and i.dueDate < :endDateExclusive
            group by year(i.dueDate), month(i.dueDate)
            order by year(i.dueDate), month(i.dueDate)
            """)
    List<MonthlyRevenueAggregateProjection> findMonthlyRevenueHistory(@Param("userId") Long userId,
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

}
