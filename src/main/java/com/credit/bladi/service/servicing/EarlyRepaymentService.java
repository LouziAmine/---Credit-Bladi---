package com.credit.bladi.service.servicing;

import static com.credit.bladi.exception.CreditFileException.CreditFileExceptionType.CREDIT_FILE_CLOSED;
import static com.credit.bladi.exception.CreditFileException.CreditFileExceptionType.CREDIT_FILE_NOT_FOUND;
import static com.credit.bladi.exception.CreditFileException.CreditFileExceptionType.EARLY_REPAYMENT_ARREARS_OUTSTANDING;
import static com.credit.bladi.exception.CreditFileException.CreditFileExceptionType.EARLY_REPAYMENT_NOT_AVAILABLE_FOR_PRODUCT;

import com.credit.bladi.exception.CreditFileException;
import com.credit.bladi.model.audit.AuditChanges;
import com.credit.bladi.model.audit.AuditTarget;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.CreditFileStatus;
import com.credit.bladi.model.enums.InstallmentStatus;
import com.credit.bladi.model.enums.NotificationType;
import com.credit.bladi.model.notification.LocalizedText;
import com.credit.bladi.model.servicing.CreditFileModel;
import com.credit.bladi.model.servicing.EarlyRepaymentModel;
import com.credit.bladi.model.servicing.EarlyRepaymentQuote;
import com.credit.bladi.model.servicing.EarlyRepaymentQuote.RescheduledInstallment;
import com.credit.bladi.model.servicing.InstallmentModel;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.repository.servicing.ICreditFileRepository;
import com.credit.bladi.repository.servicing.IEarlyRepaymentRepository;
import com.credit.bladi.repository.servicing.IInstallmentRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.notification.AfterCommitNotifications;
import com.credit.bladi.service.notification.DemandContactNotifier;
import com.credit.bladi.service.security.ICurrentActorProvider;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EarlyRepaymentService implements IEarlyRepaymentService {
    private final ICreditFileRepository creditFileRepository;

    private final IInstallmentRepository installmentRepository;

    private final IEarlyRepaymentRepository earlyRepaymentRepository;

    private final IWorkflowEngineService workflowEngineService;

    private final EarlyRepaymentCalculator calculator;

    private final IAuditService auditService;

    private final ICurrentActorProvider currentActorProvider;

    private final DemandContactNotifier demandContactNotifier;

    private final AfterCommitNotifications afterCommit;

    private final Clock clock;

    public EarlyRepaymentService(ICreditFileRepository creditFileRepository,
            IInstallmentRepository installmentRepository, IEarlyRepaymentRepository earlyRepaymentRepository,
            IWorkflowEngineService workflowEngineService, EarlyRepaymentCalculator calculator,
            IAuditService auditService, ICurrentActorProvider currentActorProvider,
            DemandContactNotifier demandContactNotifier, AfterCommitNotifications afterCommit,
            Clock clock) {
        this.creditFileRepository = creditFileRepository;
        this.installmentRepository = installmentRepository;
        this.earlyRepaymentRepository = earlyRepaymentRepository;
        this.workflowEngineService = workflowEngineService;
        this.calculator = calculator;
        this.auditService = auditService;
        this.currentActorProvider = currentActorProvider;
        this.demandContactNotifier = demandContactNotifier;
        this.afterCommit = afterCommit;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public EarlyRepaymentQuote quote(Long demandId, Money principal) {
        RepaymentContext context = loadContext(requireCreditFile(demandId));
        return calculator.quote(context.remaining(), context.demand().applicableRate(), principal,
            LocalDate.now(clock));
    }

    @Override
    @Transactional
    public EarlyRepaymentModel repay(Long demandId, Money principal) {
        RepaymentContext context = loadContext(creditFileRepository.findByDemandIdForUpdate(demandId)
            .orElseThrow(() -> CreditFileException.of(CREDIT_FILE_NOT_FOUND)));
        DemandModel demand = context.demand();
        CreditFileModel creditFile = context.creditFile();
        EarlyRepaymentQuote quote = calculator.quote(context.remaining(), demand.applicableRate(), principal,
            LocalDate.now(clock));
        String username = currentActorProvider.getCurrentUsername();
        Instant now = clock.instant();

        if (quote.fullRepayment()) {
            installmentRepository.markRepaidEarly(context.remaining().stream().map(InstallmentModel::id).toList(), now);
        } else {
            installmentRepository.reschedule(quote.newSchedule().stream()
                .collect(Collectors.toMap(RescheduledInstallment::installmentId, RescheduledInstallment::amount)));
        }
        EarlyRepaymentModel recorded = earlyRepaymentRepository.create(creditFile.id(), quote, username, now);

        auditService.log(username, AuditAction.EARLY_REPAYMENT, AuditTarget.demand(demand.publicId()),
            "creditFileId=" + creditFile.id() + ", full=" + quote.fullRepayment() + ", repaidPrincipal="
                + quote.repaidPrincipal().amount() + ", accruedInterest=" + quote.accruedInterest().amount()
                + ", indemnity=" + quote.indemnity().amount() + ", totalPaid=" + quote.totalToPay().amount() + ", "
                + AuditChanges.of()
                    .field("outstanding", quote.outstandingBefore(), quote.outstandingAfter())
                    .field("mensualite", context.remaining().getFirst().amount(), newMonthlyPayment(quote)));
        if (quote.fullRepayment() && creditFileRepository.closeIfActive(creditFile.id())) {
            auditService.log(username, AuditAction.CREDIT_FILE_CLOSED, AuditTarget.demand(demand.publicId()),
                "creditFileId=" + creditFile.id() + ", reference=" + creditFile.reference() + ", "
                    + AuditChanges.of().field("status", CreditFileStatus.ACTIVE, CreditFileStatus.CLOSED));
        }
        notifyClient(demand, quote);
        return recorded;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EarlyRepaymentModel> history(Long demandId) {
        CreditFileModel creditFile = requireCreditFile(demandId);
        return earlyRepaymentRepository.findByCreditFileId(creditFile.id());
    }

    private RepaymentContext loadContext(CreditFileModel creditFile) {
        Long demandId = creditFile.demandId();
        if (!creditFile.isActive()) {
            throw CreditFileException.of(CREDIT_FILE_CLOSED);
        }
        DemandModel demand = workflowEngineService.getDemand(demandId);
        if (demand.isMourabaha() || demand.applicableRate() == null) {
            throw CreditFileException.of(EARLY_REPAYMENT_NOT_AVAILABLE_FOR_PRODUCT);
        }
        List<InstallmentModel> installments = installmentRepository.findByCreditFileId(creditFile.id());
        if (installments.stream().anyMatch(InstallmentModel::isInArrears)) {
            throw CreditFileException.of(EARLY_REPAYMENT_ARREARS_OUTSTANDING);
        }
        List<InstallmentModel> remaining = installments.stream()
            .filter(installment -> installment.status() == InstallmentStatus.PENDING)
            .toList();
        if (remaining.isEmpty()) {
            throw CreditFileException.of(CREDIT_FILE_CLOSED);
        }
        return new RepaymentContext(creditFile, demand, remaining);
    }

    private CreditFileModel requireCreditFile(Long demandId) {
        return creditFileRepository.findByDemandId(demandId)
            .orElseThrow(() -> CreditFileException.of(CREDIT_FILE_NOT_FOUND));
    }

    private static Money newMonthlyPayment(EarlyRepaymentQuote quote) {
        return quote.newSchedule().isEmpty() ? null : quote.newSchedule().getFirst().amount();
    }

    private void notifyClient(DemandModel demand, EarlyRepaymentQuote quote) {
        LocalizedText title = LocalizedText.of(
            quote.fullRepayment() ? "earlyRepaymentFull.title" : "earlyRepaymentPartial.title");
        LocalizedText body = quote.fullRepayment()
            ? LocalizedText.of("earlyRepaymentFull.body", quote.totalToPay(), quote.indemnity(), demand.reference())
            : LocalizedText.of("earlyRepaymentPartial.body", quote.totalToPay(), quote.indemnity(), demand.reference(),
                newMonthlyPayment(quote));
        afterCommit.send(() -> demandContactNotifier.notify(demand, NotificationType.EARLY_REPAYMENT_RECORDED,
            title, body));
    }

    private record RepaymentContext(CreditFileModel creditFile, DemandModel demand, List<InstallmentModel> remaining) {
    }
}
