package com.freelanceos.freelanceappback.domain.ports.in.mission;

import com.freelanceos.freelanceappback.domain.model.mission.Mission;

import java.util.List;

public interface GetAllMissionsUseCase {
    List<Mission> execute(String username);
}
