package com.freelanceos.freelanceappback.domain.ports.in.mission;

import com.freelanceos.freelanceappback.domain.model.mission.MissionDetail;

import java.util.Optional;

public interface GetMissionDetailUseCase {
    Optional<MissionDetail> execute(String username, Long id);
}
