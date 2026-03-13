package com.freelanceos.freelanceappback.application.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelanceos.freelanceappback.application.rest.dto.mission.MissionRequest;
import com.freelanceos.freelanceappback.application.rest.mapper.MissionMapperRest;
import com.freelanceos.freelanceappback.domain.model.mission.BillingType;
import com.freelanceos.freelanceappback.domain.model.mission.Mission;
import com.freelanceos.freelanceappback.domain.model.mission.MissionDetail;
import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;
import com.freelanceos.freelanceappback.domain.ports.in.mission.CreateMissionUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.mission.GetAllMissionsUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.mission.GetMissionDetailUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.mission.UpdateMissionUseCase;
import com.freelanceos.freelanceappback.infrastructure.security.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MissionController.class)
@Import(MissionControllerTest.ControllerTestConfig.class)
class MissionControllerTest {

    @TestConfiguration
    static class ControllerTestConfig {
        @Bean
        MissionMapperRest missionMapperRest() {
            return new MissionMapperRest();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateMissionUseCase createMissionUseCase;

    @MockitoBean
    private UpdateMissionUseCase updateMissionUseCase;

    @MockitoBean
    private GetAllMissionsUseCase getAllMissionsUseCase;

    @MockitoBean
    private GetMissionDetailUseCase getMissionDetailUseCase;

    @MockitoBean
    private JwtTokenService jwtTokenService;

    @Test
    @WithMockUser(username = "demo")
    void getMissionsShouldReturnList() throws Exception {
        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now().plusDays(5);
        Mission mission = new Mission(1L, 1L, "Audit", "Maison Beldi", "contact@maisonbeldi.com",
                BigDecimal.valueOf(600), 10, BigDecimal.valueOf(6000), start, end, MissionStatus.ONGOING,
                BillingType.TJM, "Notes");

        when(getAllMissionsUseCase.execute("demo")).thenReturn(List.of(mission));

        mockMvc.perform(get("/missions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].client").value("Maison Beldi"))
                .andExpect(jsonPath("$[0].dailyRate").value(600))
                .andExpect(jsonPath("$[0].status").value("ONGOING"))
                .andExpect(jsonPath("$[0].timeProgressPercent").value(50));
    }

    @Test
    @WithMockUser(username = "demo")
    void getMissionByIdShouldReturnDetail() throws Exception {
        MissionDetail detail = new MissionDetail(1L, 1L, "Audit", "Maison Beldi", "contact@maisonbeldi.com",
                BigDecimal.valueOf(600), 10, BigDecimal.valueOf(6000), LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(8), MissionStatus.ONGOING, BillingType.TJM, "Notes", List.of(10L, 11L));

        when(getMissionDetailUseCase.execute("demo", 1L)).thenReturn(Optional.of(detail));

        mockMvc.perform(get("/missions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.clientName").value("Maison Beldi"))
                .andExpect(jsonPath("$.invoiceIds[0]").value(10))
                .andExpect(jsonPath("$.status").value("ONGOING"));
    }

    @Test
    @WithMockUser(username = "demo")
    void createMissionShouldReturnCreatedDetail() throws Exception {
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(9);
        MissionRequest request = new MissionRequest("Audit", "Maison Beldi", "contact@maisonbeldi.com",
                BigDecimal.valueOf(600), 10, BigDecimal.valueOf(6000), startDate,
                endDate, MissionStatus.ONGOING, BillingType.TJM, "Notes");
        Mission created = new Mission(1L, 1L, "Audit", "Maison Beldi", "contact@maisonbeldi.com",
                BigDecimal.valueOf(600), 10, BigDecimal.valueOf(6000), startDate, endDate,
                MissionStatus.ONGOING, BillingType.TJM, "Notes");

        when(createMissionUseCase.execute(eq("demo"), any(Mission.class))).thenReturn(created);

        mockMvc.perform(post("/missions")
                        .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildRequestJson("Audit", 600, 10, 6000, startDate, endDate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.clientName").value("Maison Beldi"))
                .andExpect(jsonPath("$.invoiceIds").isArray());
    }

    @Test
    @WithMockUser(username = "demo")
    void updateMissionShouldReturnUpdatedDetail() throws Exception {
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(9);
        MissionRequest request = new MissionRequest("Audit Updated", "Maison Beldi", "contact@maisonbeldi.com",
                BigDecimal.valueOf(650), 10, BigDecimal.valueOf(6500), startDate,
                endDate, MissionStatus.ONGOING, BillingType.TJM, "Notes");
        Mission updated = new Mission(1L, 1L, "Audit Updated", "Maison Beldi", "contact@maisonbeldi.com",
                BigDecimal.valueOf(650), 10, BigDecimal.valueOf(6500), startDate, endDate,
                MissionStatus.ONGOING, BillingType.TJM, "Notes");

        when(updateMissionUseCase.execute(eq("demo"), eq(1L), any(Mission.class))).thenReturn(Optional.of(updated));

        mockMvc.perform(put("/missions/1")
                        .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildRequestJson("Audit Updated", 650, 10, 6500, startDate, endDate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.dailyRate").value(650));
    }

    private String buildRequestJson(String title,
                                    int dailyRate,
                                    int expectedDuration,
                                    int totalBudgetEstimated,
                                    LocalDate startDate,
                                    LocalDate endDate) {
        return """
                {
                  "title": "%s",
                  "clientName": "Maison Beldi",
                  "clientContactEmail": "contact@maisonbeldi.com",
                  "dailyRate": %d,
                  "expectedDuration": %d,
                  "totalBudgetEstimated": %d,
                  "startDate": "%s",
                  "endDate": "%s",
                  "status": "ONGOING",
                  "billingType": "TJM",
                  "internalNotes": "Notes"
                }
                """.formatted(title, dailyRate, expectedDuration, totalBudgetEstimated, startDate, endDate);
    }
}
