package com.freelanceos.freelanceappback.application.rest;

import com.freelanceos.freelanceappback.application.rest.dto.mission.MissionDetailResponse;
import com.freelanceos.freelanceappback.application.rest.dto.mission.MissionListResponse;
import com.freelanceos.freelanceappback.application.rest.dto.mission.MissionRequest;
import com.freelanceos.freelanceappback.application.rest.mapper.MissionMapperRest;
import com.freelanceos.freelanceappback.domain.exception.BadRequestException;
import com.freelanceos.freelanceappback.domain.exception.ConflictException;
import com.freelanceos.freelanceappback.domain.exception.NotFoundException;
import com.freelanceos.freelanceappback.domain.model.mission.Mission;
import com.freelanceos.freelanceappback.domain.ports.in.mission.CreateMissionUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.mission.GetAllMissionsUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.mission.GetMissionDetailUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.mission.UpdateMissionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;

import java.security.Principal;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/missions")
public class MissionController {
    private final CreateMissionUseCase createMissionUseCase;
    private final UpdateMissionUseCase updateMissionUseCase;
    private final GetAllMissionsUseCase getAllMissionsUseCase;
    private final GetMissionDetailUseCase getMissionDetailUseCase;
    private final MissionMapperRest missionMapperRest;
    private final AuthenticatedUserResolver authenticatedUserResolver;

    public MissionController(CreateMissionUseCase createMissionUseCase,
                             UpdateMissionUseCase updateMissionUseCase,
                             GetAllMissionsUseCase getAllMissionsUseCase,
                             GetMissionDetailUseCase getMissionDetailUseCase,
                             MissionMapperRest missionMapperRest,
                             AuthenticatedUserResolver authenticatedUserResolver) {
        this.createMissionUseCase = createMissionUseCase;
        this.updateMissionUseCase = updateMissionUseCase;
        this.getAllMissionsUseCase = getAllMissionsUseCase;
        this.getMissionDetailUseCase = getMissionDetailUseCase;
        this.missionMapperRest = missionMapperRest;
        this.authenticatedUserResolver = authenticatedUserResolver;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<MissionListResponse> getMissions(Principal principal) {
        String username = authenticatedUserResolver.resolve(principal);
        try {
            return getAllMissionsUseCase.execute(username).stream()
                    .map(missionMapperRest::toList)
                    .toList();
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (BadRequestException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public MissionDetailResponse getMissionById(@PathVariable Long id, Principal principal) {
        String username = authenticatedUserResolver.resolve(principal);
        try {
            return getMissionDetailUseCase.execute(username, id)
                    .map(missionMapperRest::toDetail)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mission not found"));
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (BadRequestException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public MissionDetailResponse createMission(@Valid @RequestBody MissionRequest request, Principal principal) {
        String username = authenticatedUserResolver.resolve(principal);
        try {
            Mission missionToCreate = missionMapperRest.toDomain(request);
            Mission created = createMissionUseCase.execute(username, missionToCreate);
            return missionMapperRest.toDetail(created, java.util.List.of(), BigDecimal.ZERO);
        } catch (ConflictException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        } catch (BadRequestException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public MissionDetailResponse updateMission(@PathVariable Long id, @Valid @RequestBody MissionRequest request, Principal principal) {
        String username = authenticatedUserResolver.resolve(principal);
        try {
            Mission missionToUpdate = missionMapperRest.toDomain(id, request);
            return updateMissionUseCase.execute(username, id, missionToUpdate)
                    .map(updated -> missionMapperRest.toDetail(updated, java.util.List.of(), BigDecimal.ZERO))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mission not found"));
        } catch (ConflictException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        } catch (BadRequestException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }
}
