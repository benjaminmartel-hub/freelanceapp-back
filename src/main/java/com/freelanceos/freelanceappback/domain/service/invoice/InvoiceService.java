package com.freelanceos.freelanceappback.domain.service.invoice;

import com.freelanceos.freelanceappback.domain.exception.BadRequestException;
import com.freelanceos.freelanceappback.domain.exception.ConflictException;
import com.freelanceos.freelanceappback.domain.exception.NotFoundException;
import com.freelanceos.freelanceappback.domain.model.invoice.Invoice;
import com.freelanceos.freelanceappback.domain.model.invoice.InvoiceStatus;
import com.freelanceos.freelanceappback.domain.ports.in.invoice.CreateInvoiceUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.invoice.GetAllInvoicesUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.invoice.GetInvoiceDetailUseCase;
import com.freelanceos.freelanceappback.domain.ports.in.invoice.UpdateInvoiceUseCase;
import com.freelanceos.freelanceappback.domain.ports.out.InvoiceRepository;
import com.freelanceos.freelanceappback.domain.ports.out.MissionRepository;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.InvoiceEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.MissionEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.infrastructure.persistence.mapper.InvoiceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class InvoiceService implements GetAllInvoicesUseCase,
        GetInvoiceDetailUseCase,
        CreateInvoiceUseCase,
        UpdateInvoiceUseCase {

    private final InvoiceRepository invoiceRepository;
    private final MissionRepository missionRepository;
    private final UserRepository userRepository;
    private final InvoiceMapper invoiceMapper;

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final Pattern INVOICE_NUMBER_PATTERN = Pattern.compile("^FAC-(\\d{4})-(\\d{4})$");

    public InvoiceService(InvoiceRepository invoiceRepository,
                          MissionRepository missionRepository,
                          UserRepository userRepository,
                          InvoiceMapper invoiceMapper) {
        this.invoiceRepository = invoiceRepository;
        this.missionRepository = missionRepository;
        this.userRepository = userRepository;
        this.invoiceMapper = invoiceMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> execute(String username) {
        Long userId = resolveUserId(username);
        return invoiceRepository.findByUserId(userId).stream()
                .map(invoiceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Invoice> execute(String username, Long id) {
        Long userId = resolveUserId(username);
        return invoiceRepository.findByIdAndUserId(id, userId)
                .map(invoiceMapper::toDomain);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Invoice execute(String username, Invoice invoiceToCreate) {
        UserEntity user = resolveUser(username);
        MissionEntity mission = resolveMission(user.getId(), invoiceToCreate);
        validateDates(invoiceToCreate);
        BigDecimal totalTtc = calculateOrValidateTotalTtc(
                invoiceToCreate.totalHt(),
                invoiceToCreate.vatRate(),
                invoiceToCreate.totalTtc()
        );
        String number = generateInvoiceNumber(invoiceToCreate.issueDate().getYear());

        InvoiceEntity invoice = new InvoiceEntity(
                null,
                user,
                mission,
                number,
                InvoiceStatus.DRAFT,
                invoiceToCreate.issueDate(),
                invoiceToCreate.dueDate(),
                invoiceToCreate.totalHt(),
                invoiceToCreate.vatRate(),
                totalTtc
        );

        return invoiceMapper.toDomain(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional
    public Optional<Invoice> execute(String username, Long id, Invoice invoiceToUpdate) {
        UserEntity user = resolveUser(username);
        Optional<InvoiceEntity> existing = invoiceRepository.findByIdAndUserId(id, user.getId());
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        InvoiceEntity invoice = existing.get();
        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new ConflictException("Facture verrouillee");
        }

        MissionEntity mission = resolveMission(user.getId(), invoiceToUpdate);
        validateDates(invoiceToUpdate);
        BigDecimal totalTtc = calculateOrValidateTotalTtc(
                invoiceToUpdate.totalHt(),
                invoiceToUpdate.vatRate(),
                invoiceToUpdate.totalTtc()
        );

        invoice.setMission(mission);
        invoice.setIssueDate(invoiceToUpdate.issueDate());
        invoice.setDueDate(invoiceToUpdate.dueDate());
        invoice.setTotalHt(invoiceToUpdate.totalHt());
        invoice.setVatRate(invoiceToUpdate.vatRate());
        invoice.setTotalTtc(totalTtc);

        return Optional.of(invoiceMapper.toDomain(invoiceRepository.save(invoice)));
    }

    private UserEntity resolveUser(String username) {
        if (username == null || username.isBlank()) {
            throw new BadRequestException("Username is required");
        }
        return userRepository.findByNameIgnoreCase(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Long resolveUserId(String username) {
        return resolveUser(username).getId();
    }

    private MissionEntity resolveMission(Long userId, Invoice invoice) {
        if (invoice.mission() == null || invoice.mission().id() == null) {
            throw new BadRequestException("Mission is required");
        }
        return missionRepository.findByIdAndUserId(invoice.mission().id(), userId)
                .orElseThrow(() -> new NotFoundException("Mission not found"));
    }

    private void validateDates(Invoice invoice) {
        if (invoice.dueDate().isBefore(invoice.issueDate())) {
            throw new BadRequestException("Due date must be after or equal to issue date");
        }
    }

    private BigDecimal calculateOrValidateTotalTtc(BigDecimal totalHt, BigDecimal vatRate, BigDecimal totalTtc) {
        BigDecimal vatRatio = vatRate.divide(ONE_HUNDRED, 4, RoundingMode.HALF_UP);
        BigDecimal expectedTotalTtc = totalHt
                .multiply(BigDecimal.ONE.add(vatRatio))
                .setScale(2, RoundingMode.HALF_UP);
        if (totalTtc != null && totalTtc.setScale(2, RoundingMode.HALF_UP).compareTo(expectedTotalTtc) != 0) {
            throw new BadRequestException(
                    "Total TTC does not match total HT and VAT rate: expected "
                            + expectedTotalTtc
                            + " from totalHt "
                            + totalHt
                            + " and vatRate "
                            + vatRate
                            + ", got "
                            + totalTtc
            );
        }
        return expectedTotalTtc;
    }

    private String generateInvoiceNumber(int year) {
        long nextSequence = invoiceRepository.findHighestInvoiceNumberForYear(year)
                .map(number -> extractInvoiceSequence(number, year) + 1)
                .orElse(1L);
        return "FAC-%d-%04d".formatted(year, nextSequence);
    }

    private long extractInvoiceSequence(String invoiceNumber, int expectedYear) {
        Matcher matcher = INVOICE_NUMBER_PATTERN.matcher(invoiceNumber);
        if (!matcher.matches()) {
            throw new IllegalStateException("Invalid invoice number format: " + invoiceNumber);
        }
        int year = Integer.parseInt(matcher.group(1));
        if (year != expectedYear) {
            throw new IllegalStateException("Invoice number year does not match issue year: " + invoiceNumber);
        }
        return Long.parseLong(matcher.group(2));
    }
}
