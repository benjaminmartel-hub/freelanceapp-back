package com.freelanceos.freelanceappback.domain.model.dashboard;

import java.time.LocalDate;

public record MissionSummary(Long id, String title, String clientName, LocalDate endDate) {
}
