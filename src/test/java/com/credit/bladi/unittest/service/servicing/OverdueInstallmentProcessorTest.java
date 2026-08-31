package com.credit.bladi.unittest.service.servicing;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_760000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_7_25;
import static com.credit.bladi.unittest.mock.data.MockCodes.REFERENCE_DEM_3F2A9C1B;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_CONTACT_AT_ATLAS_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0612345678;
import static com.credit.bladi.unittest.mock.data.MockDates.DATE_2026_01_01T00_00_00Z;
import static com.credit.bladi.unittest.mock.data.MockDates.DATE_2026_01_15T00_00_00Z;
import static com.credit.bladi.unittest.mock.data.MockDates.DATE_2026_02_24T08_00_00Z;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.InstallmentStatus;
import com.credit.bladi.model.enums.NotificationType;
import com.credit.bladi.model.notification.LocalizedText;
import com.credit.bladi.model.servicing.InstallmentModel;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.repository.servicing.IInstallmentRepository;
import com.credit.bladi.service.notification.AfterCommitNotifications;
import com.credit.bladi.service.notification.DemandContactNotifier;
import com.credit.bladi.service.servicing.OverdueInstallmentProcessor;
import com.credit.bladi.unittest.mock.workflow.MockDemand;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OverdueInstallmentProcessorTest {
    private static final Instant NOW = Instant.parse(DATE_2026_02_24T08_00_00Z);

    private static final Instant REMINDER_CUTOFF = NOW.minus(3, ChronoUnit.DAYS);

    @Mock
    private IInstallmentRepository installmentRepository;

    @Mock
    private DemandContactNotifier demandContactNotifier;

    private OverdueInstallmentProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new OverdueInstallmentProcessor(installmentRepository, demandContactNotifier,
            new AfterCommitNotifications(Runnable::run));
    }

    private static DemandModel demand() {
        return MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_3F2A9C1B, 1L, COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, Money.of(AMOUNT_1500000), 2, Instant.parse(DATE_2026_01_01T00_00_00Z),
            Instant.parse(DATE_2026_01_15T00_00_00Z), new Rate(DECIMAL_7_25), EMAIL_CONTACT_AT_ATLAS_MA,
            PHONE_0612345678, null, null, null, null, 5L, null, null, null);
    }

    private static InstallmentModel installment(InstallmentStatus status, Instant lastReminderSentAt) {
        return new InstallmentModel(100L, UUID.randomUUID(), 10L, 1, LocalDate.of(2026, 2, 14),
            Money.of(AMOUNT_760000), status, null, lastReminderSentAt, null, null, null, null);
    }

    @Test
    void flagsPendingAsLateAndSendsReminder_whenNeverRemindedBefore() {
        DemandModel demand = demand();
        InstallmentModel pending = installment(InstallmentStatus.PENDING, null);
        when(installmentRepository.markLate(100L)).thenReturn(installment(InstallmentStatus.LATE, null));

        processor.process(pending, demand, NOW, REMINDER_CUTOFF);

        verify(installmentRepository).markLate(100L);
        verify(installmentRepository).recordReminderSent(100L, NOW);
        verify(demandContactNotifier).notify(eq(demand), eq(NotificationType.PAYMENT_REMINDER),
            any(LocalizedText.class), any(LocalizedText.class));
    }

    @Test
    void skipsReminder_whenOneWasSentLessThanThreeDaysAgo() {
        InstallmentModel recentlyReminded = installment(InstallmentStatus.LATE, NOW.minus(1, ChronoUnit.DAYS));

        processor.process(recentlyReminded, demand(), NOW, REMINDER_CUTOFF);

        verify(installmentRepository, never()).markLate(anyLong());
        verify(installmentRepository, never()).recordReminderSent(anyLong(), any());
        verify(demandContactNotifier, never()).notify(any(), any(), any(), any());
    }

    @Test
    void remindsAgain_whenLastReminderWasAtLeastThreeDaysAgo() {
        DemandModel demand = demand();
        InstallmentModel dueForAnotherReminder = installment(InstallmentStatus.LATE,
            REMINDER_CUTOFF.minusSeconds(60));

        processor.process(dueForAnotherReminder, demand, NOW, REMINDER_CUTOFF);

        verify(installmentRepository, never()).markLate(anyLong());
        verify(installmentRepository).recordReminderSent(100L, NOW);
        verify(demandContactNotifier).notify(eq(demand), eq(NotificationType.PAYMENT_REMINDER),
            any(LocalizedText.class), any(LocalizedText.class));
    }
}
