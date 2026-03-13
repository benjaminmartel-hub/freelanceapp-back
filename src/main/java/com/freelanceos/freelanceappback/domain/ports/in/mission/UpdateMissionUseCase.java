package com.freelanceos.freelanceappback.domain.ports.in.mission;

import com.freelanceos.freelanceappback.domain.model.mission.Mission;

import java.util.Optional;

public interface UpdateMissionUseCase {
    Optional<Mission> execute(String username, Long id, Mission missionToUpdate);
}
