package com.freelanceos.freelanceappback.application.rest;

import com.freelanceos.freelanceappback.application.rest.dto.dashboard.DashboardResponse;
import com.freelanceos.freelanceappback.application.rest.mapper.DashboardMapperRest;
import com.freelanceos.freelanceappback.domain.model.dashboard.ClientRevenueShare;
import com.freelanceos.freelanceappback.domain.model.dashboard.Dashboard;
import com.freelanceos.freelanceappback.domain.model.dashboard.MonthlyStat;
import com.freelanceos.freelanceappback.domain.model.dashboard.TaxEstimation;
import com.freelanceos.freelanceappback.domain.ports.in.dashboard.GetDashboardUseCase;
import com.freelanceos.freelanceappback.infrastructure.security.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@Import(DashboardControllerTest.ControllerTestConfig.class)
class DashboardControllerTest {

    @TestConfiguration
    static class ControllerTestConfig {
        @Bean
        DashboardMapperRest dashboardMapperRest() {
            return new DashboardMapperRest();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetDashboardUseCase getDashboardUseCase;

    @MockitoBean
    private JwtTokenService jwtTokenService;

    @Test
    void meShouldReturnDashboardForAuthenticatedUser() throws Exception {
        Dashboard dashboard = new Dashboard(
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(250),
                List.of(new MonthlyStat("2026-03", BigDecimal.valueOf(800), BigDecimal.valueOf(200))),
                List.of(new ClientRevenueShare("Client A", BigDecimal.valueOf(1000))),
                List.of(),
                List.of(),
                new TaxEstimation(BigDecimal.ZERO, LocalDate.of(2026, 3, 16), "Estimation fiscale mensuelle mars 2026")
        );
        when(getDashboardUseCase.execute("demo")).thenReturn(dashboard);

        mockMvc.perform(get("/dashboard/me")
                        .principal(() -> "demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyTurnover").value(1000))
                .andExpect(jsonPath("$.revenueHistory[0].month").value("2026-03"))
                .andExpect(jsonPath("$.clientDistribution[0].clientName").value("Client A"));
    }

    @Test
    void meShouldReturnUnauthorizedWhenMissingAuthentication() throws Exception {
        mockMvc.perform(get("/dashboard/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meShouldReturnNotFoundWhenUserMissing() throws Exception {
        when(getDashboardUseCase.execute("missing")).thenThrow(new IllegalArgumentException("User not found"));

        mockMvc.perform(get("/dashboard/me")
                        .principal(() -> "missing"))
                .andExpect(status().isNotFound());
    }
}
