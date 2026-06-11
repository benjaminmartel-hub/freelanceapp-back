package com.freelanceos.freelanceappback.application.rest;

import com.freelanceos.freelanceappback.application.rest.mapper.InvoiceMapperRest;
import com.freelanceos.freelanceappback.domain.model.client.ClientSummary;
import com.freelanceos.freelanceappback.domain.model.invoice.Invoice;
import com.freelanceos.freelanceappback.domain.model.mission.MissionSummaryForInvoice;
import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStatus;
import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStats;
import com.freelanceos.freelanceappback.domain.model.mission.MissionStatus;
import com.freelanceos.freelanceappback.domain.ports.in.invoice.CreateInvoiceUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.invoice.GetAllInvoicesUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.invoice.GetInvoiceDetailUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.invoice.GetInvoiceStatsUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.invoice.UpdateInvoiceUseCase;
import com.freelanceos.freelanceappback.infrastructure.security.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InvoiceController.class)
@Import(InvoiceControllerTest.ControllerTestConfig.class)
class InvoiceControllerTest {

    @TestConfiguration
    static class ControllerTestConfig {
        @Bean
        InvoiceMapperRest invoiceMapperRest() {
            return new InvoiceMapperRest();
        }

        @Bean
        AuthenticatedUserResolver authenticatedUserResolver() {
            return new AuthenticatedUserResolver();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateInvoiceUseCase createInvoiceUseCase;

    @MockitoBean
    private UpdateInvoiceUseCase updateInvoiceUseCase;

    @MockitoBean
    private GetAllInvoicesUseCase getAllInvoicesUseCase;

    @MockitoBean
    private GetInvoiceDetailUseCase getInvoiceDetailUseCase;

    @MockitoBean
    private GetInvoiceStatsUseCase getInvoiceStatsUseCase;

    @MockitoBean
    private JwtTokenService jwtTokenService;

    @Test
    @WithMockUser(username = "demo")
    void getInvoicesShouldReturnLightList() throws Exception {
        Invoice invoice = buildInvoice(1L, "FAC-2026-001", InvoiceStatus.SENT);
        when(getAllInvoicesUseCase.execute("demo")).thenReturn(List.of(invoice));

        mockMvc.perform(get("/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].number").value("FAC-2026-001"))
                .andExpect(jsonPath("$[0].status").value("SENT"))
                .andExpect(jsonPath("$[0].totalHt").value(1000))
                .andExpect(jsonPath("$[0].vatRate").value(20))
                .andExpect(jsonPath("$[0].missionId").value(10))
                .andExpect(jsonPath("$[0].missionTitle").value("Audit"))
                .andExpect(jsonPath("$[0].clientName").value("Maison Beldi"));
    }

    @Test
    @WithMockUser(username = "demo")
    void getInvoiceByIdShouldReturnDetail() throws Exception {
        when(getInvoiceDetailUseCase.execute("demo", 1L))
                .thenReturn(Optional.of(buildInvoice(1L, "FAC-2026-001", InvoiceStatus.PAID)));

        mockMvc.perform(get("/invoices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.number").value("FAC-2026-001"))
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.vatRate").value(20))
                .andExpect(jsonPath("$.mission.id").value(10))
                .andExpect(jsonPath("$.mission.title").value("Audit"))
                .andExpect(jsonPath("$.mission.client.id").value(20))
                .andExpect(jsonPath("$.mission.client.name").value("Maison Beldi"));
    }

    @Test
    @WithMockUser(username = "demo")
    void getInvoiceByIdShouldReturnNotFoundWhenUseCaseReturnsEmpty() throws Exception {
        when(getInvoiceDetailUseCase.execute("demo", 99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/invoices/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "demo")
    void getInvoiceStatsShouldReturnKpis() throws Exception {
        when(getInvoiceStatsUseCase.execute("demo"))
                .thenReturn(new InvoiceStats(
                        BigDecimal.valueOf(2500),
                        BigDecimal.valueOf(1200),
                        BigDecimal.valueOf(700)
                ));

        mockMvc.perform(get("/invoices/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPaid").value(2500))
                .andExpect(jsonPath("$.totalPending").value(1200))
                .andExpect(jsonPath("$.totalOverdue").value(700));
    }

    private Invoice buildInvoice(Long id, String number, InvoiceStatus status) {
        return new Invoice(
                id,
                1L,
                number,
                LocalDate.of(2026, 1, 5),
                LocalDate.of(2026, 2, 5),
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(1200),
                status,
                new MissionSummaryForInvoice(
                        10L,
                        "Audit",
                        new ClientSummary(20L, "Maison Beldi"),
                        MissionStatus.ONGOING,
                        "EUR"
                )
        );
    }
}
