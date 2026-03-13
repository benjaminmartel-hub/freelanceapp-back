package com.freelanceos.freelanceappback.domain.ports.out;

import java.util.List;

public interface InvoiceRepository {
    List<Long> findIdsByUserIdAndClientName(Long userId, String clientName);
}
