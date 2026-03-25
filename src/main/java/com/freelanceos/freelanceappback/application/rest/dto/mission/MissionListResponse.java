package com.freelanceos.freelanceappback.application.rest.dto.mission;

import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MissionListResponse(Long id,
                                  String title,
                                  MissionClientResponse client,
                                  BigDecimal dailyRate,
                                  String currency,
                                  MissionStatus status,
                                  LocalDate endDate,
                                  Integer timeProgressPercent) {
}
