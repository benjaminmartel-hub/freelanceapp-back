package com.freelanceos.freelanceappback.domain.service.invoice;

import com.freelanceos.freelanceappback.domain.exception.BadRequestException;
import com.freelanceos.freelanceappback.domain.exception.NotFoundException;
import com.freelanceos.freelanceappback.domain.model.invoice.GeneratedInvoicePdf;
import com.freelanceos.freelanceappback.domain.ports.in.invoice.GenerateInvoicePdfUseCase;
import com.freelanceos.freelanceappback.domain.ports.out.pdf.InvoicePdfGenerator;
import com.freelanceos.freelanceappback.domain.ports.out.InvoiceRepository;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.InvoiceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class GenerateInvoicePdfService implements GenerateInvoicePdfUseCase {
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final InvoiceMapper invoiceMapper;
    private final InvoicePdfGenerator invoicePdfGenerator;

    public GenerateInvoicePdfService(InvoiceRepository invoiceRepository,
                                     UserRepository userRepository,
                                     InvoiceMapper invoiceMapper,
                                     InvoicePdfGenerator invoicePdfGenerator) {
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.invoiceMapper = invoiceMapper;
        this.invoicePdfGenerator = invoicePdfGenerator;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GeneratedInvoicePdf> execute(String username, Long id) {
        Long userId = resolveUserId(username);
        return invoiceRepository.findByIdAndUserId(id, userId)
                .map(invoiceMapper::toDomain)
                .map(invoice -> new GeneratedInvoicePdf(filename(invoice.number()), invoicePdfGenerator.generate(invoice)));
    }

    private Long resolveUserId(String username) {
        if (username == null || username.isBlank()) {
            throw new BadRequestException("Username is required");
        }
        return userRepository.findByNameIgnoreCase(username)
                .map(UserEntity::getId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private String filename(String invoiceNumber) {
        return invoiceNumber.replaceAll("[^A-Za-z0-9._-]", "_") + ".pdf";
    }
}
