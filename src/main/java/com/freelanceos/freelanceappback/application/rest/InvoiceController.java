package com.freelanceos.freelanceappback.application.rest;

import com.freelanceos.freelanceappback.application.rest.dto.invoice.InvoiceCreateRequest;
import com.freelanceos.freelanceappback.application.rest.dto.invoice.InvoiceDetailResponse;
import com.freelanceos.freelanceappback.application.rest.dto.invoice.InvoiceListResponse;
import com.freelanceos.freelanceappback.application.rest.dto.invoice.InvoiceStatsResponse;
import com.freelanceos.freelanceappback.application.rest.dto.invoice.InvoiceUpdateRequest;
import com.freelanceos.freelanceappback.application.rest.mapper.InvoiceMapperRest;
import com.freelanceos.freelanceappback.domain.exception.BadRequestException;
import com.freelanceos.freelanceappback.domain.exception.ConflictException;
import com.freelanceos.freelanceappback.domain.exception.NotFoundException;
import com.freelanceos.freelanceappback.domain.model.invoice.GeneratedInvoicePdf;
import com.freelanceos.freelanceappback.domain.model.invoice.Invoice;
import com.freelanceos.freelanceappback.domain.ports.in.invoice.CreateInvoiceUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.invoice.GenerateInvoicePdfUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.invoice.GetAllInvoicesUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.invoice.GetInvoiceDetailUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.invoice.GetInvoiceStatsUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.invoice.UpdateInvoiceUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping({"/invoices"})
public class InvoiceController {
    private final CreateInvoiceUseCase createInvoiceUseCase;
    private final UpdateInvoiceUseCase updateInvoiceUseCase;
    private final GetAllInvoicesUseCase getAllInvoicesUseCase;
    private final GetInvoiceDetailUseCase getInvoiceDetailUseCase;
    private final GetInvoiceStatsUseCase getInvoiceStatsUseCase;
    private final GenerateInvoicePdfUseCase generateInvoicePdfUseCase;
    private final InvoiceMapperRest invoiceMapperRest;
    private final AuthenticatedUserResolver authenticatedUserResolver;

    public InvoiceController(CreateInvoiceUseCase createInvoiceUseCase,
                             UpdateInvoiceUseCase updateInvoiceUseCase,
                             GetAllInvoicesUseCase getAllInvoicesUseCase,
                             GetInvoiceDetailUseCase getInvoiceDetailUseCase,
                             GetInvoiceStatsUseCase getInvoiceStatsUseCase,
                             GenerateInvoicePdfUseCase generateInvoicePdfUseCase,
                             InvoiceMapperRest invoiceMapperRest,
                             AuthenticatedUserResolver authenticatedUserResolver) {
        this.createInvoiceUseCase = createInvoiceUseCase;
        this.updateInvoiceUseCase = updateInvoiceUseCase;
        this.getAllInvoicesUseCase = getAllInvoicesUseCase;
        this.getInvoiceDetailUseCase = getInvoiceDetailUseCase;
        this.getInvoiceStatsUseCase = getInvoiceStatsUseCase;
        this.generateInvoicePdfUseCase = generateInvoicePdfUseCase;
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

    @GetMapping("/{id}/download")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id, Principal principal) {
        String username = authenticatedUserResolver.resolve(principal);
        try {
            GeneratedInvoicePdf pdf = generateInvoicePdfUseCase.execute(username, id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pdf.filename() + "\"")
                    .body(pdf.content());
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (BadRequestException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public InvoiceDetailResponse createInvoice(@Valid @RequestBody InvoiceCreateRequest request, Principal principal) {
        String username = authenticatedUserResolver.resolve(principal);
        try {
            Invoice invoiceToCreate = invoiceMapperRest.toDomain(request);
            return invoiceMapperRest.toDetail(createInvoiceUseCase.execute(username, invoiceToCreate));
        } catch (BadRequestException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public InvoiceDetailResponse updateInvoice(@PathVariable Long id,
                                               @Valid @RequestBody InvoiceUpdateRequest request,
                                               Principal principal) {
        String username = authenticatedUserResolver.resolve(principal);
        try {
            Invoice invoiceToUpdate = invoiceMapperRest.toDomain(id, request);
            return updateInvoiceUseCase.execute(username, id, invoiceToUpdate)
                    .map(invoiceMapperRest::toDetail)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
        } catch (ConflictException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        } catch (BadRequestException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
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
