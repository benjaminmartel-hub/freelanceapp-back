package com.freelanceos.freelanceappback.infrastructure.persistence.mapper;

import com.freelanceos.freelanceappback.domain.model.client.ClientSummary;
import com.freelanceos.freelanceappback.domain.model.invoice.Invoice;
import com.freelanceos.freelanceappback.domain.model.mission.MissionSummaryForInvoice;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.InvoiceEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.MissionEntity;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper {
    public Invoice toDomain(InvoiceEntity invoiceEntity) {
        MissionEntity mission = invoiceEntity.getMission();
        return new Invoice(
                invoiceEntity.getId(),
                invoiceEntity.getUser().getId(),
                invoiceEntity.getNumber(),
                invoiceEntity.getIssueDate(),
                invoiceEntity.getDueDate(),
                invoiceEntity.getTotalHt(),
                invoiceEntity.getVatRate(),
                invoiceEntity.getTotalTtc(),
                invoiceEntity.getStatus(),
                new MissionSummaryForInvoice(
                        mission.getId(),
                        mission.getTitle(),
                        new ClientSummary(
                                mission.getClient().getId(),
                                mission.getClient().getName()
                        ),
                        mission.getStatus(),
                        mission.getCurrency()
                )
        );
    }
}
