package com.credit.bladi.unittest.service.servicing;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_10000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1000000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1500;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_15000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1520000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_20000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_30000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_5000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_750000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_755000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_760000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_761500;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_9120000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_2_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_7_12;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_7_25;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_0_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_1171_67;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_1952_78;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_760000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_761171_67;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_761952_78;
import static com.credit.bladi.unittest.mock.data.MockCodes.REFERENCE_DEM_3F2A9C1B;
import static com.credit.bladi.unittest.mock.data.MockCodes.REFERENCE_DEM_MOURABA1;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_CREDIT_BLADI_SA;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_CONTACT_AT_ATLAS_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_K_AT_X_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_MANAGER1_AT_BLADI_CREDIT_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.PERSON_KARIM_BENALI;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0600000000;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0612345678;
import static com.credit.bladi.unittest.mock.data.MockContacts.RIB_230780012345678901234503;
import static com.credit.bladi.unittest.mock.data.MockContacts.RIB_230_780_0123456789012345_03;
import static com.credit.bladi.unittest.mock.data.MockContacts.RIB_230_780_0123456789012345_95;
import static com.credit.bladi.unittest.mock.data.MockDates.DATE_2026_01_01T00_00_00Z;
import static com.credit.bladi.unittest.mock.data.MockDates.DATE_2026_01_15T00_00_00Z;
import static com.credit.bladi.unittest.mock.data.MockDates.DATE_2026_02_20T09_00_00Z;
import static com.credit.bladi.unittest.mock.data.MockDates.DATE_2026_02_24T10_00_00Z;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DOS_3F2A9C1B;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_CLIENT1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.exception.CreditFileException;
import com.credit.bladi.exception.CreditFileException.CreditFileExceptionType;
import com.credit.bladi.model.audit.AuditTarget;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.CreditFileStatus;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.InstallmentStatus;
import com.credit.bladi.model.enums.NotificationType;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.investment.InvestmentFinancialSnapshot;
import com.credit.bladi.model.notification.LocalizedText;
import com.credit.bladi.model.notification.NotificationMessage;
import com.credit.bladi.model.servicing.CreditFileModel;
import com.credit.bladi.model.servicing.InstallmentModel;
import com.credit.bladi.model.servicing.LateInterestPolicy;
import com.credit.bladi.model.servicing.PaymentBeneficiary;
import com.credit.bladi.model.servicing.ScheduledInstallment;
import com.credit.bladi.model.user.User;
import com.credit.bladi.model.vo.AmortizationRow;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
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
import com.credit.bladi.service.servicing.CreditFileService;
import com.credit.bladi.service.servicing.OverdueInstallmentProcessor;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import com.credit.bladi.unittest.mock.workflow.MockDemand;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreditFileServiceTest {
    private static final ZoneId CASABLANCA = ZoneId.of("Africa/Casablanca");

    private static final Instant NOW = Instant.parse(DATE_2026_02_24T10_00_00Z);

    private static final UUID CREDIT_FILE_PUBLIC_ID = UUID.randomUUID();

    private static final UUID INSTALLMENT_PUBLIC_ID = UUID.randomUUID();

    private static final String SOURCE_RIB = RIB_230_780_0123456789012345_03;

    private static final String SOURCE_RIB_NORMALIZED = RIB_230780012345678901234503;

    private static final LocalDate DUE_DATE = LocalDate.of(2026, 2, 14);

    private static final Instant DECLARED_AT = Instant.parse(DATE_2026_02_20T09_00_00Z);

    @Mock
    private ICreditFileRepository creditFileRepository;

    @Mock
    private IInstallmentRepository installmentRepository;

    @Mock
    private IPaymentRepository paymentRepository;

    @Mock
    private IWorkflowEngineService workflowEngineService;

    @Mock
    private IInvestmentCreditService investmentCreditService;

    @Mock
    private IMortgageCreditService mortgageCreditService;

    @Mock
    private DemandContactNotifier demandContactNotifier;

    @Mock
    private IAuditService auditService;

    @Mock
    private ICurrentActorProvider currentActorProvider;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private INotificationService notificationService;

    @Mock
    private OverdueInstallmentProcessor overdueInstallmentProcessor;

    private CreditFileService service;

    @BeforeEach
    void setUp() {
        service = new CreditFileService(creditFileRepository, installmentRepository, paymentRepository,
            workflowEngineService, investmentCreditService, mortgageCreditService, demandContactNotifier,
            auditService, currentActorProvider, userRepository, notificationService,
            new PaymentBeneficiary(COMPANY_CREDIT_BLADI_SA, Rib.of(RIB_230_780_0123456789012345_03)),
            Clock.fixed(NOW, CASABLANCA), new AfterCommitNotifications(Runnable::run),
            new LateInterestPolicy(DECIMAL_2_00), overdueInstallmentProcessor);
    }

    private static DemandModel completedDemand() {
        return MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_3F2A9C1B, 1L, COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, Money.of(AMOUNT_1500000), 2, Instant.parse(DATE_2026_01_01T00_00_00Z),
            Instant.parse(DATE_2026_01_15T00_00_00Z), new Rate(DECIMAL_7_25), EMAIL_CONTACT_AT_ATLAS_MA,
            PHONE_0612345678, null, null, null, null, 5L, null, null, null);
    }

    private static DemandModel completedMourabahaDemand() {
        return MockDemand.mortgage(1L, UUID.randomUUID(), REFERENCE_DEM_MOURABA1, 1L, PERSON_KARIM_BENALI,
            Money.of(AMOUNT_500000), 240, Instant.parse(DATE_2026_01_01T00_00_00Z),
            Instant.parse(DATE_2026_01_15T00_00_00Z), new Rate(DECIMAL_5_50), EMAIL_K_AT_X_MA, PHONE_0600000000, null,
            ProductType.MOURABAHA, CspCode.SALA, Money.of(AMOUNT_1000000),
            Money.of(AMOUNT_20000), 40, false, null);
    }

    private static CreditFileModel creditFile(CreditFileStatus status) {
        return new CreditFileModel(10L, CREDIT_FILE_PUBLIC_ID, 1L, LABEL_DOS_3F2A9C1B,
            Instant.parse(DATE_2026_01_15T00_00_00Z), Money.of(AMOUNT_1500000), status, USER_MANAGER1);
    }

    private static CreditFileModel creditFile() {
        return creditFile(CreditFileStatus.ACTIVE);
    }

    private static InvestmentFinancialSnapshot snapshot() {
        return new InvestmentFinancialSnapshot(new Rate(DECIMAL_7_25),
            Money.of(AMOUNT_760000), Money.of(AMOUNT_9120000),
            Money.of(AMOUNT_20000), Money.of(AMOUNT_1520000),
            Money.of(AMOUNT_15000), DECIMAL_7_12,
            List.of(
                new AmortizationRow(1, Money.of(AMOUNT_1500000), Money.of(AMOUNT_10000),
                    Money.of(AMOUNT_750000), Money.of(AMOUNT_760000),
                    Money.of(AMOUNT_750000)),
                new AmortizationRow(2, Money.of(AMOUNT_750000), Money.of(AMOUNT_5000),
                    Money.of(AMOUNT_750000), Money.of(AMOUNT_755000),
                    Money.of(BigDecimal.ZERO))),
            Money.of(AMOUNT_1500), Money.of(AMOUNT_761500),
            Money.of(AMOUNT_30000));
    }

    private static InstallmentModel installment(InstallmentStatus status, Long creditFileId) {
        boolean declared = status == InstallmentStatus.DECLARED;
        return new InstallmentModel(100L, INSTALLMENT_PUBLIC_ID, creditFileId, 1, DUE_DATE,
            Money.of(AMOUNT_760000), status, status == InstallmentStatus.PAID ? NOW : null, null,
            declared ? DECLARED_AT : null, declared ? USER_CLIENT1 : null, declared ? SOURCE_RIB_NORMALIZED : null,
            null);
    }

    private static InstallmentModel pendingInstallment() {
        return installment(InstallmentStatus.PENDING, 10L);
    }

    private static InstallmentModel declaredInstallment() {
        return installment(InstallmentStatus.DECLARED, 10L);
    }

    private static Money mad(String amount) {
        return Money.of(new BigDecimal(amount));
    }

    @Test
    void openCreditFile_createsDossierAndFullScheduleFromAmortization_andNotifiesTheClient() {
        DemandModel demand = completedDemand();
        when(creditFileRepository.findByDemandId(1L)).thenReturn(Optional.empty());
        when(workflowEngineService.getDemand(1L)).thenReturn(demand);
        when(investmentCreditService.getAmortizationSchedule(1L, null, null, null)).thenReturn(snapshot());
        when(creditFileRepository.create(eq(1L), eq(LABEL_DOS_3F2A9C1B), eq(Instant.parse(DATE_2026_01_15T00_00_00Z)),
            eq(Money.of(AMOUNT_1500000)), eq(USER_MANAGER1))).thenReturn(creditFile());

        CreditFileModel result = service.openCreditFile(1L, USER_MANAGER1);

        assertEquals(creditFile(), result);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ScheduledInstallment>> scheduleCaptor = ArgumentCaptor.forClass(List.class);
        verify(installmentRepository).createSchedule(eq(10L), scheduleCaptor.capture());
        List<ScheduledInstallment> schedule = scheduleCaptor.getValue();
        assertEquals(2, schedule.size());
        LocalDate expectedFirstDueDate = LocalDate.of(2026, 2, 14);
        assertEquals(expectedFirstDueDate, schedule.get(0).dueDate());
        assertEquals(expectedFirstDueDate.plusMonths(1), schedule.get(1).dueDate());
        assertEquals(Money.of(AMOUNT_760000), schedule.get(0).amount());

        verify(auditService).log(eq(USER_MANAGER1), eq(AuditAction.CREDIT_FILE_OPENED), any(AuditTarget.class), any());
        verify(demandContactNotifier).notify(eq(demand), eq(NotificationType.CREDIT_FILE_OPENED),
            any(LocalizedText.class), any(LocalizedText.class));
    }

    @Test
    void openCreditFile_isIdempotent_returnsExistingCreditFileWithoutRebuildingSchedule() {
        when(creditFileRepository.findByDemandId(1L)).thenReturn(Optional.of(creditFile()));

        CreditFileModel result = service.openCreditFile(1L, USER_MANAGER1);

        assertEquals(creditFile(), result);
        verify(installmentRepository, never()).createSchedule(anyLong(), anyList());
        verify(demandContactNotifier, never()).notify(any(), any(), any(), any());
    }

    @Test
    void getByDemandId_throws_whenNoCreditFileOpenedYet() {
        when(creditFileRepository.findByDemandId(1L)).thenReturn(Optional.empty());

        CreditFileException exception = assertThrows(CreditFileException.class, () -> service.getByDemandId(1L));

        assertEquals(CreditFileExceptionType.CREDIT_FILE_NOT_FOUND, exception.getType());
    }

    @Test
    void getInstallments_showsAccruedLateInterestOnUnpaidOverdueInstallment() {
        when(creditFileRepository.findById(10L)).thenReturn(Optional.of(creditFile()));
        when(workflowEngineService.getDemand(1L)).thenReturn(completedDemand());
        when(installmentRepository.findByCreditFileId(10L)).thenReturn(List.of(installment(InstallmentStatus.LATE,
            10L)));

        List<InstallmentModel> installments = service.getInstallments(10L);

        assertEquals(mad(TEXT_1952_78), installments.get(0).latePenalty());
        assertEquals(mad(TEXT_761952_78), installments.get(0).totalDue());
    }

    @Test
    void declarePayment_marksInstallmentDeclaredAndNotifiesTheManagingGestionnaire() {
        when(creditFileRepository.findByDemandIdForUpdate(1L)).thenReturn(Optional.of(creditFile()));
        when(installmentRepository.findById(100L)).thenReturn(Optional.of(pendingInstallment()));
        when(installmentRepository.declare(100L, USER_CLIENT1, SOURCE_RIB_NORMALIZED, NOW))
            .thenReturn(declaredInstallment());
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_CLIENT1);
        when(workflowEngineService.getDemand(1L)).thenReturn(completedDemand());
        when(userRepository.findByUsername(USER_MANAGER1)).thenReturn(Optional.of(
            User.getBuilder().username(USER_MANAGER1).email(EMAIL_MANAGER1_AT_BLADI_CREDIT_MA).build()));

        InstallmentModel result = service.declarePayment(1L, 100L, SOURCE_RIB);

        assertEquals(InstallmentStatus.DECLARED, result.status());
        verify(paymentRepository, never()).create(any(), any(), any());
        verify(installmentRepository, never()).markPaid(any(), any(), any());
        verify(auditService).log(eq(USER_CLIENT1), eq(AuditAction.PAYMENT_DECLARED), any(AuditTarget.class), any());
        ArgumentCaptor<NotificationMessage> messageCaptor = ArgumentCaptor.forClass(NotificationMessage.class);
        verify(notificationService).notify(messageCaptor.capture());
        assertEquals(USER_MANAGER1, messageCaptor.getValue().recipientUsername());
        assertEquals(EMAIL_MANAGER1_AT_BLADI_CREDIT_MA, messageCaptor.getValue().recipientEmail());
        assertEquals(NotificationType.PAYMENT_DECLARED, messageCaptor.getValue().type());
    }

    @Test
    void declarePayment_rejectsInvalidRibBeforeTouchingTheCreditFile() {
        assertThrows(IllegalArgumentException.class,
            () -> service.declarePayment(1L, 100L, RIB_230_780_0123456789012345_95));

        verify(creditFileRepository, never()).findByDemandIdForUpdate(any());
    }

    @Test
    void declarePayment_throws_whenCreditFileClosed() {
        when(creditFileRepository.findByDemandIdForUpdate(1L))
            .thenReturn(Optional.of(creditFile(CreditFileStatus.CLOSED)));

        CreditFileException exception = assertThrows(CreditFileException.class,
            () -> service.declarePayment(1L, 100L, SOURCE_RIB));

        assertEquals(CreditFileExceptionType.CREDIT_FILE_CLOSED, exception.getType());
    }

    @Test
    void declarePayment_throws_whenInstallmentDoesNotBelongToThisDemandsCreditFile() {
        when(creditFileRepository.findByDemandIdForUpdate(1L)).thenReturn(Optional.of(creditFile()));
        when(installmentRepository.findById(100L)).thenReturn(Optional.of(installment(InstallmentStatus.PENDING,
            999L)));

        CreditFileException exception = assertThrows(CreditFileException.class,
            () -> service.declarePayment(1L, 100L, SOURCE_RIB));

        assertEquals(CreditFileExceptionType.INSTALLMENT_NOT_FOR_CREDIT_FILE, exception.getType());
    }

    @Test
    void declarePayment_throws_whenInstallmentAlreadyPaid() {
        when(creditFileRepository.findByDemandIdForUpdate(1L)).thenReturn(Optional.of(creditFile()));
        when(installmentRepository.findById(100L)).thenReturn(Optional.of(installment(InstallmentStatus.PAID, 10L)));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_CLIENT1);

        CreditFileException exception = assertThrows(CreditFileException.class,
            () -> service.declarePayment(1L, 100L, SOURCE_RIB));

        assertEquals(CreditFileExceptionType.INSTALLMENT_ALREADY_PAID, exception.getType());
    }

    @Test
    void declarePayment_throws_whenAlreadyDeclared() {
        when(creditFileRepository.findByDemandIdForUpdate(1L)).thenReturn(Optional.of(creditFile()));
        when(installmentRepository.findById(100L)).thenReturn(Optional.of(declaredInstallment()));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_CLIENT1);

        CreditFileException exception = assertThrows(CreditFileException.class,
            () -> service.declarePayment(1L, 100L, SOURCE_RIB));

        assertEquals(CreditFileExceptionType.INSTALLMENT_ALREADY_DECLARED, exception.getType());
    }

    @Test
    void validatePayment_chargesLateInterestUpToTheDeclarationDateAndNotifiesTheClient() {
        DemandModel demand = completedDemand();
        when(creditFileRepository.findByDemandIdForUpdate(1L)).thenReturn(Optional.of(creditFile()));
        when(installmentRepository.findById(100L)).thenReturn(Optional.of(declaredInstallment()));
        when(workflowEngineService.getDemand(1L)).thenReturn(demand);
        Money lateInterest = mad(TEXT_1171_67);
        InstallmentModel paid = installment(InstallmentStatus.PAID, 10L).withLatePenalty(lateInterest);
        when(installmentRepository.markPaid(100L, NOW, lateInterest)).thenReturn(paid);
        when(installmentRepository.hasUnsettledInstallments(10L)).thenReturn(true);
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_MANAGER1);

        InstallmentModel result = service.validatePayment(1L, 100L);

        assertEquals(InstallmentStatus.PAID, result.status());
        verify(paymentRepository).create(100L, mad(TEXT_761171_67), NOW);
        verify(auditService).log(eq(USER_MANAGER1), eq(AuditAction.PAYMENT_VALIDATED), any(AuditTarget.class), any());
        verify(creditFileRepository, never()).closeIfActive(any());
        verify(demandContactNotifier).notify(eq(demand), eq(NotificationType.PAYMENT_RECEIVED),
            any(LocalizedText.class), any(LocalizedText.class));
    }

    @Test
    void validatePayment_neverChargesLateInterestOnMourabaha() {
        when(creditFileRepository.findByDemandIdForUpdate(1L)).thenReturn(Optional.of(creditFile()));
        when(installmentRepository.findById(100L)).thenReturn(Optional.of(declaredInstallment()));
        when(workflowEngineService.getDemand(1L)).thenReturn(completedMourabahaDemand());
        Money noInterest = mad(TEXT_0_00);
        when(installmentRepository.markPaid(100L, NOW, noInterest))
            .thenReturn(installment(InstallmentStatus.PAID, 10L));
        when(installmentRepository.hasUnsettledInstallments(10L)).thenReturn(true);

        service.validatePayment(1L, 100L);

        verify(paymentRepository).create(100L, mad(TEXT_760000), NOW);
    }

    @Test
    void validatePayment_closesTheCreditFileWhenTheLastInstallmentIsSettled() {
        when(creditFileRepository.findByDemandIdForUpdate(1L)).thenReturn(Optional.of(creditFile()));
        when(installmentRepository.findById(100L)).thenReturn(Optional.of(declaredInstallment()));
        when(workflowEngineService.getDemand(1L)).thenReturn(completedDemand());
        when(installmentRepository.markPaid(eq(100L), eq(NOW), any()))
            .thenReturn(installment(InstallmentStatus.PAID, 10L));
        when(installmentRepository.hasUnsettledInstallments(10L)).thenReturn(false);
        when(creditFileRepository.closeIfActive(10L)).thenReturn(true);
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_MANAGER1);

        service.validatePayment(1L, 100L);

        verify(creditFileRepository).closeIfActive(10L);
        verify(auditService).log(eq(USER_MANAGER1), eq(AuditAction.CREDIT_FILE_CLOSED), any(AuditTarget.class), any());
    }

    @Test
    void validatePayment_throws_whenNotDeclared() {
        when(creditFileRepository.findByDemandIdForUpdate(1L)).thenReturn(Optional.of(creditFile()));
        when(installmentRepository.findById(100L)).thenReturn(Optional.of(pendingInstallment()));
        when(workflowEngineService.getDemand(1L)).thenReturn(completedDemand());

        CreditFileException exception = assertThrows(CreditFileException.class,
            () -> service.validatePayment(1L, 100L));

        assertEquals(CreditFileExceptionType.INSTALLMENT_NOT_DECLARED, exception.getType());
        verify(paymentRepository, never()).create(any(), any(), any());
    }

    @Test
    void rejectPayment_revertsToLateWhenAlreadyDueAndNotifiesTheClientWithTheReason() {
        DemandModel demand = completedDemand();
        when(creditFileRepository.findByDemandIdForUpdate(1L)).thenReturn(Optional.of(creditFile()));
        when(installmentRepository.findById(100L)).thenReturn(Optional.of(declaredInstallment()));
        InstallmentModel reverted = installment(InstallmentStatus.LATE, 10L);
        when(installmentRepository.reject(100L, InstallmentStatus.LATE)).thenReturn(reverted);
        when(workflowEngineService.getDemand(1L)).thenReturn(demand);
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_MANAGER1);

        InstallmentModel result = service.rejectPayment(1L, 100L, "RIB ne correspond pas");

        assertEquals(InstallmentStatus.LATE, result.status());
        verify(auditService).log(eq(USER_MANAGER1), eq(AuditAction.PAYMENT_REJECTED), any(AuditTarget.class), any());
        verify(demandContactNotifier).notify(eq(demand), eq(NotificationType.PAYMENT_REJECTED),
            any(LocalizedText.class), any(LocalizedText.class));
    }

    @Test
    void rejectPayment_throws_whenNotDeclared() {
        when(creditFileRepository.findByDemandIdForUpdate(1L)).thenReturn(Optional.of(creditFile()));
        when(installmentRepository.findById(100L)).thenReturn(Optional.of(pendingInstallment()));

        CreditFileException exception = assertThrows(CreditFileException.class,
            () -> service.rejectPayment(1L, 100L, "raison"));

        assertEquals(CreditFileExceptionType.INSTALLMENT_NOT_DECLARED, exception.getType());
    }

    @Test
    void sendOverdueReminders_processesEachOverdueInstallmentWithItsDemand() {
        DemandModel demand = completedDemand();
        InstallmentModel overdue = pendingInstallment();
        when(installmentRepository.findOverdueUnpaid(LocalDate.of(2026, 2, 24), 0, 200))
            .thenReturn(List.of(overdue));
        when(creditFileRepository.findAllById(List.of(10L))).thenReturn(List.of(creditFile()));
        when(workflowEngineService.getDemands(List.of(1L))).thenReturn(Map.of(1L, demand));

        service.sendOverdueReminders();

        verify(overdueInstallmentProcessor).process(overdue, demand, NOW, NOW.minus(3, ChronoUnit.DAYS));
    }

    @Test
    void sendOverdueReminders_keepsGoingWhenOneInstallmentFails() {
        DemandModel demand = completedDemand();
        InstallmentModel first = pendingInstallment();
        InstallmentModel second = new InstallmentModel(101L, UUID.randomUUID(), 10L, 2, DUE_DATE.plusMonths(1),
            Money.of(AMOUNT_755000), InstallmentStatus.PENDING, null, null, null, null, null, null);
        when(installmentRepository.findOverdueUnpaid(any(), eq(0), eq(200))).thenReturn(List.of(first, second));
        when(creditFileRepository.findAllById(List.of(10L))).thenReturn(List.of(creditFile()));
        when(workflowEngineService.getDemands(List.of(1L))).thenReturn(Map.of(1L, demand));
        doThrow(new IllegalStateException("boom")).when(overdueInstallmentProcessor)
            .process(eq(first), any(), any(), any());

        service.sendOverdueReminders();

        verify(overdueInstallmentProcessor).process(eq(second), eq(demand), any(), any());
    }
}
