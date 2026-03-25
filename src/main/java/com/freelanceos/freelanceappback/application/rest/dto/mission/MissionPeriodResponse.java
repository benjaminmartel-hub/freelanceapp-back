package com.freelanceos.freelanceappback.application.rest.dto.mission;

import java.time.LocalDate;

public record MissionPeriodResponse(LocalDate startDate,
                                    LocalDate endDate,
                                    Integer progressPercent) {
}
