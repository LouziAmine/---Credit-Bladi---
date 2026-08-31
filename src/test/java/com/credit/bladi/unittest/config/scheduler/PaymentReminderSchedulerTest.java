package com.credit.bladi.unittest.config.scheduler;

import static org.mockito.Mockito.verify;

import com.credit.bladi.config.scheduler.PaymentReminderScheduler;
import com.credit.bladi.service.servicing.ICreditFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentReminderSchedulerTest {
    @Mock
    private ICreditFileService creditFileService;

    private PaymentReminderScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new PaymentReminderScheduler(creditFileService);
    }

    @Test
    void remindOverdueInstallments_delegatesToCreditFileService() {
        scheduler.remindOverdueInstallments();

        verify(creditFileService).sendOverdueReminders();
    }
}
