package com.freelanceos.freelanceappback.domain.service.invoice;

import com.freelanceos.freelanceappback.domain.exception.BadRequestException;
import com.freelanceos.freelanceappback.domain.exception.NotFoundException;
import com.freelanceos.freelanceappback.domain.model.invoice.Invoice;
import com.freelanceos.freelanceappback.domain.ports.in.invoice.GetAllInvoicesUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.invoice.GetInvoiceDetailUseCase;
import com.freelanceos.freelanceappback.domain.ports.out.InvoiceRepository;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.InvoiceMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService implements GetAllInvoicesUseCase,
        GetInvoiceDetailUseCase {

    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final InvoiceMapper invoiceMapper;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          UserRepository userRepository,
                          InvoiceMapper invoiceMapper) {
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.invoiceMapper = invoiceMapper;
    }

    @Override
    public List<Invoice> execute(String username) {
        Long userId = resolveUserId(username);
        return invoiceRepository.findByUserId(userId).stream()
                .map(invoiceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Invoice> execute(String username, Long id) {
        Long userId = resolveUserId(username);
        return invoiceRepository.findByIdAndUserId(id, userId)
                .map(invoiceMapper::toDomain);
    }

    private Long resolveUserId(String username) {
        if (username == null || username.isBlank()) {
            throw new BadRequestException("Username is required");
        }
        return userRepository.findByNameIgnoreCase(username)
                .map(UserEntity::getId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
