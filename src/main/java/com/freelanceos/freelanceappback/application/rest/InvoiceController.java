package com.freelanceos.freelanceappback.application.rest;

import com.freelanceos.freelanceappback.application.rest.dto.invoice.InvoiceDetailResponse;
import com.freelanceos.freelanceappback.application.rest.dto.invoice.InvoiceListResponse;
import com.freelanceos.freelanceappback.application.rest.dto.invoice.InvoiceStatsResponse;
import com.freelanceos.freelanceappback.application.rest.mapper.InvoiceMapperRest;
import com.freelanceos.freelanceappback.domain.exception.BadRequestException;
import com.freelanceos.freelanceappback.domain.exception.NotFoundException;
import com.freelanceos.freelanceappback.domain.ports.in.invoice.GetAllInvoicesUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.invoice.GetInvoiceDetailUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.invoice.GetInvoiceStatsUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {
    private final GetAllInvoicesUseCase getAllInvoicesUseCase;
    private final GetInvoiceDetailUseCase getInvoiceDetailUseCase;
    private final GetInvoiceStatsUseCase getInvoiceStatsUseCase;
    private final InvoiceMapperRest invoiceMapperRest;
    private final AuthenticatedUserResolver authenticatedUserResolver;

    public InvoiceController(GetAllInvoicesUseCase getAllInvoicesUseCase,
                             GetInvoiceDetailUseCase getInvoiceDetailUseCase,
                             GetInvoiceStatsUseCase getInvoiceStatsUseCase,
                             InvoiceMapperRest invoiceMapperRest,
                             AuthenticatedUserResolver authenticatedUserResolver) {
        this.getAllInvoicesUseCase = getAllInvoicesUseCase;
        this.getInvoiceDetailUseCase = getInvoiceDetailUseCase;
        this.getInvoiceStatsUseCase = getInvoiceStatsUseCase;
        this.invoiceMapperRest = invoiceMapperRest;
        this.authenticatedUserResolver = authenticatedUserResolver;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<InvoiceListResponse> getInvoices(Principal principal) {
        String username = authenticatedUserResolver.resolve(principal);
        try {
            return getAllInvoicesUseCase.execute(username).stream()
                    .map(invoiceMapperRest::toList)
                    .toList();
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (BadRequestException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public InvoiceDetailResponse getInvoiceById(@PathVariable Long id, Principal principal) {
        String username = authenticatedUserResolver.resolve(principal);
        try {
            return getInvoiceDetailUseCase.execute(username, id)
                    .map(invoiceMapperRest::toDetail)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (BadRequestException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping("/stats")
    @PreAuthorize("isAuthenticated()")
    public InvoiceStatsResponse getInvoiceStats(Principal principal) {
        String username = authenticatedUserResolver.resolve(principal);
        try {
            return invoiceMapperRest.toStats(getInvoiceStatsUseCase.execute(username));
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (BadRequestException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
