package com.freelanceos.freelanceappback.domain.ports.in.mission;

import com.freelanceos.freelanceappback.domain.model.mission.Mission;

public interface CreateMissionUseCase {
    Mission execute(String username, Mission missionToCreate);
}
