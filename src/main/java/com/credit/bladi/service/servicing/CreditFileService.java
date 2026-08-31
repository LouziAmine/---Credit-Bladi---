package com.credit.bladi.service.servicing;

import static com.credit.bladi.exception.CreditFileException.CreditFileExceptionType.CREDIT_FILE_CLOSED;
import static com.credit.bladi.exception.CreditFileException.CreditFileExceptionType.CREDIT_FILE_NOT_FOUND;
import static com.credit.bladi.exception.CreditFileException.CreditFileExceptionType.INSTALLMENT_NOT_FOUND;

import com.credit.bladi.exception.CreditFileException;
import com.credit.bladi.model.audit.AuditChanges;
import com.credit.bladi.model.audit.AuditTarget;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.CreditFileStatus;
import com.credit.bladi.model.enums.NotificationType;
import com.credit.bladi.model.investment.InvestmentFinancialSnapshot;
import com.credit.bladi.model.mortgage.MortgageAmortizationSnapshot;
import com.credit.bladi.model.notification.LocalizedText;
import com.credit.bladi.model.notification.NotificationMessage;
import com.credit.bladi.model.servicing.CreditFileModel;
import com.credit.bladi.model.servicing.InstallmentModel;
import com.credit.bladi.model.servicing.LateInterestPolicy;
import com.credit.bladi.model.servicing.PaymentBeneficiary;
import com.credit.bladi.model.servicing.ScheduledInstallment;
import com.credit.bladi.model.user.User;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rib;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.repository.servicing.ICreditFileRepository;
import com.credit.bladi.repository.servicing.IInstallmentRepository;
import com.credit.bladi.repository.servicing.IPaymentRepository;
import com.credit.bladi.repository.user.IUserRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.investment.IInvestmentCreditService;
import com.credit.bladi.service.mortgage.IMortgageCreditService;
import com.credit.bladi.service.notification.AfterCommitNotifications;
import com.credit.bladi.service.notification.DemandContactNotifier;
import com.credit.bladi.service.notification.INotificationService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreditFileService implements ICreditFileService {
    private static final Logger LOGGER = LogManager.getLogger(CreditFileService.class);

    private static final int GRACE_PERIOD_DAYS = 30;

    private static final int REMINDER_INTERVAL_DAYS = 3;

    private static final int OVERDUE_REMINDER_PAGE_SIZE = 200;

    private final ICreditFileRepository creditFileRepository;

    private final IInstallmentRepository installmentRepository;

    private final IPaymentRepository paymentRepository;

    private final IWorkflowEngineService workflowEngineService;

    private final IInvestmentCreditService investmentCreditService;

    private final IMortgageCreditService mortgageCreditService;

    private final DemandContactNotifier demandContactNotifier;

    private final IAuditService auditService;

    private final ICurrentActorProvider currentActorProvider;

    private final IUserRepository userRepository;

    private final INotificationService notificationService;

    private final PaymentBeneficiary beneficiary;

    private final Clock clock;

    private final AfterCommitNotifications afterCommit;

    private final OverdueInstallmentProcessor overdueInstallmentProcessor;

    private final LateInterestPolicy lateInterestPolicy;

    public CreditFileService(ICreditFileRepository creditFileRepository,
            IInstallmentRepository installmentRepository, IPaymentRepository paymentRepository,
            IWorkflowEngineService workflowEngineService, IInvestmentCreditService investmentCreditService,
            IMortgageCreditService mortgageCreditService,
            DemandContactNotifier demandContactNotifier, IAuditService auditService,
            ICurrentActorProvider currentActorProvider, IUserRepository userRepository,
            INotificationService notificationService, PaymentBeneficiary beneficiary,
            Clock clock, AfterCommitNotifications afterCommit, LateInterestPolicy lateInterestPolicy,
            OverdueInstallmentProcessor overdueInstallmentProcessor) {
        this.overdueInstallmentProcessor = overdueInstallmentProcessor;
        this.creditFileRepository = creditFileRepository;
        this.installmentRepository = installmentRepository;
        this.paymentRepository = paymentRepository;
        this.workflowEngineService = workflowEngineService;
        this.investmentCreditService = investmentCreditService;
        this.mortgageCreditService = mortgageCreditService;
        this.demandContactNotifier = demandContactNotifier;
        this.auditService = auditService;
        this.currentActorProvider = currentActorProvider;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.beneficiary = beneficiary;
        this.clock = clock;
        this.afterCommit = afterCommit;
        this.lateInterestPolicy = lateInterestPolicy;
    }

    @Override
    @Transactional
    public CreditFileModel openCreditFile(Long demandId, String triggeredByUsername) {
        return creditFileRepository.findByDemandId(demandId)
            .orElseGet(() -> createCreditFile(demandId, triggeredByUsername));
    }

    private CreditFileModel createCreditFile(Long demandId, String triggeredByUsername) {
        DemandModel demand = workflowEngineService.getDemand(demandId);

        String reference = "DOS-" + demand.reference().replaceFirst("^DEM-", "");
        CreditFileModel creditFile = creditFileRepository.create(demandId, reference, demand.completedAt(),
            demand.amount(), triggeredByUsername);

        LocalDate firstDueDate = firstDueDate(demand.completedAt());
        List<ScheduledInstallment> schedule = resolveSchedule(demand, demandId, firstDueDate);
        installmentRepository.createSchedule(creditFile.id(), schedule);

        auditService.log(triggeredByUsername, AuditAction.CREDIT_FILE_OPENED, AuditTarget.demand(demand.publicId()),
            "demandId=" + demandId + ", creditFileId=" + creditFile.id() + ", reference=" + reference
                + ", disbursedAmount=" + demand.amount().amount() + ", installments=" + schedule.size());

        LocalizedText title = LocalizedText.of("creditFileOpened.title");
        LocalizedText body = LocalizedText.of("creditFileOpened.body", demand.amount(), demand.reference(),
            firstDueDate);
        afterCommit.send(() -> demandContactNotifier.notify(demand, NotificationType.CREDIT_FILE_OPENED, title, body));

        return creditFile;
    }

    private LocalDate firstDueDate(Instant disbursedAt) {
        return LocalDate.ofInstant(disbursedAt, clock.getZone()).plusDays(GRACE_PERIOD_DAYS);
    }

    private List<ScheduledInstallment> resolveSchedule(DemandModel demand, Long demandId, LocalDate firstDueDate) {
        return switch (demand.demandType()) {
            case ENTERPRISE -> {
                InvestmentFinancialSnapshot snapshot = investmentCreditService.getAmortizationSchedule(demandId,
                    null, null, null);
                yield toSchedule(snapshot.amortizationSchedule(), row -> row.installmentNumber(), row -> row.payment(),
                    firstDueDate);
            }
            case MORTGAGE -> {
                MortgageAmortizationSnapshot snapshot = mortgageCreditService.getAmortizationSchedule(demandId,
                    null, null, null);
                yield snapshot.amortizationSchedule() != null
                    ? toSchedule(snapshot.amortizationSchedule(), row -> row.installmentNumber(), row -> row.payment(),
                        firstDueDate)
                    : toSchedule(snapshot.mourabahaAmortizationSchedule(), row -> row.installmentNumber(),
                        row -> row.payment(), firstDueDate);
            }
        };
    }

    private static <R> List<ScheduledInstallment> toSchedule(List<R> rows, ToIntFunction<R> installmentNumber,
            Function<R, Money> payment, LocalDate firstDueDate) {
        return rows.stream()
            .map(row -> new ScheduledInstallment(firstDueDate.plusMonths(installmentNumber.applyAsInt(row) - 1L),
                payment.apply(row)))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CreditFileModel getByDemandId(Long demandId) {
        return creditFileRepository.findByDemandId(demandId)
            .orElseThrow(() -> CreditFileException.of(CREDIT_FILE_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstallmentModel> getInstallments(Long creditFileId) {
        CreditFileModel creditFile = creditFileRepository.findById(creditFileId)
            .orElseThrow(() -> CreditFileException.of(CREDIT_FILE_NOT_FOUND));
        DemandModel demand = workflowEngineService.getDemand(creditFile.demandId());
        LocalDate today = LocalDate.now(clock);
        return installmentRepository.findByCreditFileId(creditFileId).stream()
            .map(installment -> installment.isSettled()
                ? installment
                : installment.withLatePenalty(lateInterestOwed(installment, demand, today)))
            .toList();
    }

    private Money lateInterestOwed(InstallmentModel installment, DemandModel demand, LocalDate today) {
        if (demand.isMourabaha()) {
            return Money.of(BigDecimal.ZERO.setScale(2));
        }
        return lateInterestPolicy.accrued(installment, demand.applicableRate(),
            installment.lateInterestEndDate(today, clock.getZone()));
    }

    @Override
    @Transactional(readOnly = true)
    public Long resolveInstallmentId(UUID publicId) {
        return installmentRepository.findIdByPublicId(publicId)
            .orElseThrow(() -> CreditFileException.of(INSTALLMENT_NOT_FOUND));
    }

    @Override
    @Transactional
    public InstallmentModel declarePayment(Long demandId, Long installmentId, String sourceRib) {
        Rib rib = Rib.of(sourceRib);
        CreditFileModel creditFile = lockByDemandId(demandId);
        if (!creditFile.isActive()) {
            throw CreditFileException.of(CREDIT_FILE_CLOSED);
        }
        InstallmentModel installment = requireInstallmentOf(creditFile, installmentId);
        String username = currentActorProvider.getCurrentUsername();

        InstallmentModel transition = installment.declare(username, rib, clock.instant());
        InstallmentModel declared = installmentRepository.declare(installmentId, transition.declaredBy(),
            transition.declaredSourceRib(), transition.declaredAt());
        DemandModel demand = workflowEngineService.getDemand(demandId);

        auditService.log(username, AuditAction.PAYMENT_DECLARED, AuditTarget.demand(demand.publicId()),
            "demandId=" + demandId + ", creditFileId=" + creditFile.id() + ", installmentId=" + installmentId
                + ", amount=" + installment.amount().amount() + ", sourceRib=" + rib.masked() + ", "
                + AuditChanges.of().field("status", installment.status(), transition.status()));

        managerNotification(creditFile, demand, declared)
            .ifPresent(notification -> afterCommit.send(() -> notificationService.notify(notification)));
        return declared;
    }

    @Override
    @Transactional
    public InstallmentModel validatePayment(Long demandId, Long installmentId) {
        CreditFileModel creditFile = lockByDemandId(demandId);
        InstallmentModel installment = requireInstallmentOf(creditFile, installmentId);
        String username = currentActorProvider.getCurrentUsername();
        DemandModel demand = workflowEngineService.getDemand(demandId);

        Money lateInterest = lateInterestOwed(installment, demand, LocalDate.now(clock));
        InstallmentModel transition = installment.validate(clock.instant(), lateInterest);
        InstallmentModel paid = installmentRepository.markPaid(installmentId, transition.paidAt(), lateInterest);
        paymentRepository.create(installmentId, transition.totalDue(), transition.paidAt());

        auditService.log(username, AuditAction.PAYMENT_VALIDATED, AuditTarget.demand(demand.publicId()),
            "demandId=" + demandId + ", creditFileId=" + creditFile.id() + ", installmentId=" + installmentId
                + ", amount=" + installment.amount().amount() + ", lateInterest=" + lateInterest.amount() + ", "
                + AuditChanges.of().field("status", installment.status(), transition.status()));

        closeIfFullyRepaid(creditFile, demand, username);

        notifyContactAfterCommit(demand, NotificationType.PAYMENT_RECEIVED, LocalizedText.of("paymentReceived.title"),
            LocalizedText.of("paymentReceived.body", transition.totalDue(), installment.sequenceNumber(),
                demand.reference()));
        return paid;
    }

    private void closeIfFullyRepaid(CreditFileModel creditFile, DemandModel demand, String username) {
        if (creditFile.isActive() && !installmentRepository.hasUnsettledInstallments(creditFile.id())
                && creditFileRepository.closeIfActive(creditFile.id())) {
            auditService.log(username, AuditAction.CREDIT_FILE_CLOSED, AuditTarget.demand(demand.publicId()),
                "creditFileId=" + creditFile.id() + ", reference=" + creditFile.reference() + ", "
                    + AuditChanges.of().field("status", CreditFileStatus.ACTIVE, CreditFileStatus.CLOSED));
        }
    }

    @Override
    @Transactional
    public InstallmentModel rejectPayment(Long demandId, Long installmentId, String reason) {
        CreditFileModel creditFile = lockByDemandId(demandId);
        InstallmentModel installment = requireInstallmentOf(creditFile, installmentId);

        InstallmentModel transition = installment.reject(LocalDate.now(clock));
        InstallmentModel reverted = installmentRepository.reject(installmentId, transition.status());
        DemandModel demand = workflowEngineService.getDemand(demandId);

        auditService.log(currentActorProvider.getCurrentUsername(), AuditAction.PAYMENT_REJECTED,
            AuditTarget.demand(demand.publicId()),
            "demandId=" + demandId + ", creditFileId=" + creditFile.id() + ", installmentId=" + installmentId
                + ", reason=" + reason + ", "
                + AuditChanges.of().field("status", installment.status(), transition.status()));

        notifyContactAfterCommit(demand, NotificationType.PAYMENT_REJECTED, LocalizedText.of("paymentRejected.title"),
            LocalizedText.of("paymentRejected.body", installment.sequenceNumber(), demand.reference(), reason));
        return reverted;
    }

    private CreditFileModel lockByDemandId(Long demandId) {
        return creditFileRepository.findByDemandIdForUpdate(demandId)
            .orElseThrow(() -> CreditFileException.of(CREDIT_FILE_NOT_FOUND));
    }

    private InstallmentModel requireInstallmentOf(CreditFileModel creditFile, Long installmentId) {
        InstallmentModel installment = installmentRepository.findById(installmentId)
            .orElseThrow(() -> CreditFileException.of(INSTALLMENT_NOT_FOUND));
        installment.assertBelongsTo(creditFile);
        return installment;
    }

    private void notifyContactAfterCommit(DemandModel demand, NotificationType type, LocalizedText title,
            LocalizedText body) {
        afterCommit.send(() -> demandContactNotifier.notify(demand, type, title, body));
    }

    private Optional<NotificationMessage> managerNotification(CreditFileModel creditFile, DemandModel demand,
            InstallmentModel installment) {
        String managerUsername = creditFile.managedByUsername();
        if (managerUsername == null) {
            LOGGER.warn("PAYMENT_DECLARED_NO_MANAGER creditFileId={} installmentId={}", creditFile.id(),
                installment.id());
            return Optional.empty();
        }
        User manager = userRepository.findByUsername(managerUsername).orElse(null);
        if (manager == null) {
            LOGGER.warn("PAYMENT_DECLARED_MANAGER_NOT_FOUND username={}", managerUsername);
            return Optional.empty();
        }

        LocalizedText body = LocalizedText.of("paymentDeclaredManager.body", demand.contactName(), installment.amount(),
            installment.sequenceNumber(), demand.reference(), installment.dueDate(),
            installment.declaredSourceRib(), beneficiary.name(), beneficiary.rib().value());

        return Optional.of(new NotificationMessage(manager.getUsername(), manager.getEmail(), null,
            manager.getPreferredLanguage(), NotificationType.PAYMENT_DECLARED,
            LocalizedText.of("paymentDeclaredManager.title"), body,
            "/admin/demands/" + demand.publicId()));
    }

    @Override
    public void sendOverdueReminders() {
        Instant now = clock.instant();
        LocalDate today = LocalDate.now(clock);
        Instant reminderCutoff = now.minus(REMINDER_INTERVAL_DAYS, ChronoUnit.DAYS);

        int page = 0;
        List<InstallmentModel> batch;
        do {
            batch = installmentRepository.findOverdueUnpaid(today, page, OVERDUE_REMINDER_PAGE_SIZE);
            Map<Long, CreditFileModel> creditFilesById = creditFileRepository
                .findAllById(batch.stream().map(InstallmentModel::creditFileId).distinct().toList())
                .stream()
                .collect(Collectors.toMap(CreditFileModel::id, Function.identity()));
            Map<Long, DemandModel> demandsById = workflowEngineService.getDemands(
                creditFilesById.values().stream().map(CreditFileModel::demandId).distinct().toList());
            for (InstallmentModel installment : batch) {
                processOverdueInstallment(installment, now, reminderCutoff, creditFilesById, demandsById);
            }
            page++;
        } while (batch.size() == OVERDUE_REMINDER_PAGE_SIZE);
    }

    private void processOverdueInstallment(InstallmentModel installment, Instant now, Instant reminderCutoff,
            Map<Long, CreditFileModel> creditFilesById, Map<Long, DemandModel> demandsById) {
        try {
            CreditFileModel creditFile = creditFilesById.get(installment.creditFileId());
            if (creditFile == null) {
                throw new IllegalStateException("No credit file found for id " + installment.creditFileId()
                    + " (installmentId=" + installment.id() + ")");
            }
            DemandModel demand = demandsById.get(creditFile.demandId());
            if (demand == null) {
                throw new IllegalStateException("No demand found for id " + creditFile.demandId()
                    + " (creditFileId=" + creditFile.id() + ")");
            }
            overdueInstallmentProcessor.process(installment, demand, now, reminderCutoff);
        } catch (RuntimeException e) {
            LOGGER.error("OVERDUE_REMINDER_PROCESSING_FAILED installmentId={}", installment.id(), e);
        }
    }
}
