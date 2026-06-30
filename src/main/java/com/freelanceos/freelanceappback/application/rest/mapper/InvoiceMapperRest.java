package com.freelanceos.freelanceappback.application.rest.mapper;

import com.freelanceos.freelanceappback.application.rest.dto.invoice.InvoiceCreateRequest;
import com.freelanceos.freelanceappback.application.rest.dto.invoice.InvoiceDetailResponse;
import com.freelanceos.freelanceappback.application.rest.dto.invoice.InvoiceListResponse;
import com.freelanceos.freelanceappback.application.rest.dto.invoice.InvoiceStatsResponse;
import com.freelanceos.freelanceappback.application.rest.dto.invoice.InvoiceUpdateRequest;
import com.freelanceos.freelanceappback.application.rest.dto.mission.MissionClientResponse;
import com.freelanceos.freelanceappback.application.rest.dto.mission.MissionSummaryForInvoiceResponse;
import com.freelanceos.freelanceappback.domain.model.invoice.Invoice;
import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStats;
import com.freelanceos.freelanceappback.domain.model.mission.MissionSummaryForInvoice;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapperRest {
    public Invoice toDomain(InvoiceCreateRequest request) {
        return new Invoice(
                null,
                null,
                null,
                request.issueDate(),
                request.dueDate(),
                request.totalHt(),
                request.vatRate(),
                request.totalTtc(),
                null,
                new MissionSummaryForInvoice(request.missionId(), null, null, null, null)
        );
    }

    public Invoice toDomain(Long id, InvoiceUpdateRequest request) {
        return new Invoice(
                id,
                null,
                null,
                request.issueDate(),
                request.dueDate(),
                request.totalHt(),
                request.vatRate(),
                request.totalTtc(),
                null,
                new MissionSummaryForInvoice(request.missionId(), null, null, null, null)
        );
    }

    public InvoiceListResponse toList(Invoice invoice) {
        MissionSummaryForInvoice mission = invoice.mission();
        return new InvoiceListResponse(
                invoice.id(),
                invoice.number(),
                invoice.issueDate(),
                invoice.dueDate(),
                invoice.totalHt(),
                invoice.vatRate(),
                invoice.totalTtc(),
                invoice.status(),
                mission.id(),
                mission.title(),
                mission.client().name()
        );
    }

    public InvoiceDetailResponse toDetail(Invoice invoice) {
        return new InvoiceDetailResponse(
                invoice.id(),
                invoice.number(),
                invoice.issueDate(),
                invoice.dueDate(),
                invoice.totalHt(),
                invoice.vatRate(),
                invoice.totalTtc(),
                invoice.status(),
                toMission(invoice.mission())
        );
    }

    public InvoiceStatsResponse toStats(InvoiceStats stats) {
        return new InvoiceStatsResponse(
                stats.totalPaid(),
                stats.totalPending(),
                stats.totalOverdue()
        );
    }

    private MissionSummaryForInvoiceResponse toMission(MissionSummaryForInvoice mission) {
        return new MissionSummaryForInvoiceResponse(
                mission.id(),
                mission.title(),
                new MissionClientResponse(mission.client().id(), mission.client().name()),
                mission.status(),
                mission.currency()
        );
    }
}
