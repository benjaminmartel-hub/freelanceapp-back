package com.freelanceos.freelanceappback.application.rest;

import com.freelanceos.freelanceappback.application.rest.dto.mission.MissionDetail;
import com.freelanceos.freelanceappback.application.rest.dto.mission.MissionList;
import com.freelanceos.freelanceappback.application.rest.dto.mission.MissionRequest;
import com.freelanceos.freelanceappback.application.rest.mapper.MissionMapperRest;
import com.freelanceos.freelanceappback.domain.model.mission.Mission;
import com.freelanceos.freelanceappback.domain.ports.in.mission.CreateMissionUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.mission.GetAllMissionsUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.mission.GetMissionDetailUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.mission.UpdateMissionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/missions")
public class MissionController {
    private final CreateMissionUseCase createMissionUseCase;
    private final UpdateMissionUseCase updateMissionUseCase;
    private final GetAllMissionsUseCase getAllMissionsUseCase;
    private final GetMissionDetailUseCase getMissionDetailUseCase;
    private final MissionMapperRest missionMapperRest;

    public MissionController(CreateMissionUseCase createMissionUseCase,
                             UpdateMissionUseCase updateMissionUseCase,
                             GetAllMissionsUseCase getAllMissionsUseCase,
                             GetMissionDetailUseCase getMissionDetailUseCase,
                             MissionMapperRest missionMapperRest) {
        this.createMissionUseCase = createMissionUseCase;
        this.updateMissionUseCase = updateMissionUseCase;
        this.getAllMissionsUseCase = getAllMissionsUseCase;
        this.getMissionDetailUseCase = getMissionDetailUseCase;
        this.missionMapperRest = missionMapperRest;
    }

    @GetMapping
    public List<MissionList> getMissions(Principal principal) {
        String username = resolveUsername(principal);
        return getAllMissionsUseCase.execute(username).stream()
                .map(missionMapperRest::toList)
                .toList();
    }

    @GetMapping("/{id}")
    public MissionDetail getMissionById(@PathVariable Long id, Principal principal) {
        String username = resolveUsername(principal);
        return getMissionDetailUseCase.execute(username, id)
                .map(missionMapperRest::toDetail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mission not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MissionDetail createMission(@RequestBody MissionRequest request, Principal principal) {
        String username = resolveUsername(principal);
        try {
            Mission missionToCreate = missionMapperRest.toDomain(request);
            Mission created = createMissionUseCase.execute(username, missionToCreate);
            return missionMapperRest.toDetail(created, java.util.List.of());
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public MissionDetail updateMission(@PathVariable Long id, @RequestBody MissionRequest request, Principal principal) {
        String username = resolveUsername(principal);
        try {
            Mission missionToUpdate = missionMapperRest.toDomain(id, request);
            return updateMissionUseCase.execute(username, id, missionToUpdate)
                    .map(updated -> missionMapperRest.toDetail(updated, java.util.List.of()))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mission not found"));
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    private String resolveUsername(Principal principal) {
        String username = principal != null ? principal.getName() : null;
        if (username == null || username.isBlank()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                username = authentication.getName();
            }
        }

        if (username == null || username.isBlank() || "anonymousUser".equals(username)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        return username;
    }
}
