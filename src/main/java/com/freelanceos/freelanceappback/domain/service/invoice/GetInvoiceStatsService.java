package com.freelanceos.freelanceappback.domain.service.invoice;

import com.freelanceos.freelanceappback.domain.exception.BadRequestException;
import com.freelanceos.freelanceappback.domain.exception.NotFoundException;
import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStatus;
import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStats;
import com.freelanceos.freelanceappback.domain.ports.in.invoice.GetInvoiceStatsUseCase;
import com.freelanceos.freelanceappback.domain.ports.out.InvoiceRepository;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class GetInvoiceStatsService implements GetInvoiceStatsUseCase {
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;

    public GetInvoiceStatsService(InvoiceRepository invoiceRepository,
                                    UserRepository userRepository) {
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
    }

    @Override
    public InvoiceStats execute(String username) {
        Long userId = resolveUserId(username);
        return new InvoiceStats(
                defaultZero(invoiceRepository.sumTotalHtByUserIdAndStatus(userId, InvoiceStatus.PAID)),
                defaultZero(invoiceRepository.sumTotalHtByUserIdAndStatus(userId, InvoiceStatus.SENT)),
                defaultZero(invoiceRepository.sumTotalHtByUserIdAndStatus(userId, InvoiceStatus.OVERDUE))
        );
    }

    private Long resolveUserId(String username) {
        if (username == null || username.isBlank()) {
            throw new BadRequestException("Username is required");
        }
        return userRepository.findByNameIgnoreCase(username)
                .map(UserEntity::getId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
