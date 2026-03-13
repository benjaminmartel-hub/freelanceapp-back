package com.freelanceos.freelanceappback.infrastructure.persistence.entity;

import com.freelanceos.freelanceappback.domain.model.mission.BillingType;
import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "missions")
public class MissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String title;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "client_contact_email")
    private String clientContactEmail;

    @Column(name = "daily_rate", nullable = false, precision = 19, scale = 2)
    private BigDecimal dailyRate;

    @Column(name = "expected_duration", nullable = false)
    private Integer expectedDuration;

    @Column(name = "total_budget_estimated", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalBudgetEstimated;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_type", nullable = false)
    private BillingType billingType;

    @Column(name = "internal_notes", columnDefinition = "TEXT")
    private String internalNotes;

    public MissionEntity() {
    }

    public MissionEntity(Long id,
                         UserEntity user,
                         String title,
                         String clientName,
                         String clientContactEmail,
                         BigDecimal dailyRate,
                         Integer expectedDuration,
                         BigDecimal totalBudgetEstimated,
                         LocalDate startDate,
                         LocalDate endDate,
                         MissionStatus status,
                         BillingType billingType,
                         String internalNotes) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.clientName = clientName;
        this.clientContactEmail = clientContactEmail;
        this.dailyRate = dailyRate;
        this.expectedDuration = expectedDuration;
        this.totalBudgetEstimated = totalBudgetEstimated;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.billingType = billingType;
        this.internalNotes = internalNotes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientContactEmail() {
        return clientContactEmail;
    }

    public void setClientContactEmail(String clientContactEmail) {
        this.clientContactEmail = clientContactEmail;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public Integer getExpectedDuration() {
        return expectedDuration;
    }

    public void setExpectedDuration(Integer expectedDuration) {
        this.expectedDuration = expectedDuration;
    }

    public BigDecimal getTotalBudgetEstimated() {
        return totalBudgetEstimated;
    }

    public void setTotalBudgetEstimated(BigDecimal totalBudgetEstimated) {
        this.totalBudgetEstimated = totalBudgetEstimated;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
    }

    public BillingType getBillingType() {
        return billingType;
    }

    public void setBillingType(BillingType billingType) {
        this.billingType = billingType;
    }

    public String getInternalNotes() {
        return internalNotes;
    }

    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }
}
