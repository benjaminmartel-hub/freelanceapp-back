package com.freelanceos.freelanceappback.infrastructure.persistence.entity;

import com.freelanceos.freelanceappback.domain.model.dashboard.DeclarationPeriod;
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

@Entity
@Table(name = "fiscal_configs")
public class FiscalConfigEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "tax_rate", nullable = false, precision = 10, scale = 4)
    private BigDecimal taxRate;

    @Column(name = "vat_enabled", nullable = false)
    private boolean vatEnabled;

    @Enumerated(EnumType.STRING)
    @Column(name = "declaration_period", nullable = false)
    private DeclarationPeriod declarationPeriod;

    public FiscalConfigEntity() {
    }

    public FiscalConfigEntity(Long id,
                              UserEntity user,
                              BigDecimal taxRate,
                              boolean vatEnabled,
                              DeclarationPeriod declarationPeriod) {
        this.id = id;
        this.user = user;
        this.taxRate = taxRate;
        this.vatEnabled = vatEnabled;
        this.declarationPeriod = declarationPeriod;
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

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public boolean isVatEnabled() {
        return vatEnabled;
    }

    public void setVatEnabled(boolean vatEnabled) {
        this.vatEnabled = vatEnabled;
    }

    public DeclarationPeriod getDeclarationPeriod() {
        return declarationPeriod;
    }

    public void setDeclarationPeriod(DeclarationPeriod declarationPeriod) {
        this.declarationPeriod = declarationPeriod;
    }
}
