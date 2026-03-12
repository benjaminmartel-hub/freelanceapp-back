package com.freelanceos.freelanceappback.domain.model.dashboard;

import java.math.BigDecimal;

public record FiscalConfigSettings(BigDecimal taxRate, boolean vatEnabled, DeclarationPeriod declarationPeriod) {
}
