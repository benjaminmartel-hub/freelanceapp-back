package com.freelanceos.freelanceappback.application.rest.dto.mission;

import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;

import java.math.BigDecimal;

public record MissionList(Long id,
                          String client,
                          BigDecimal dailyRate,
                          MissionStatus status,
                          Integer timeProgressPercent) {
}
