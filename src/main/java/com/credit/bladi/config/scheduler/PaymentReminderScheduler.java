package com.credit.bladi.config.scheduler;

import com.credit.bladi.config.time.ClockConfig;
import com.credit.bladi.service.servicing.ICreditFileService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PaymentReminderScheduler {
    private final ICreditFileService creditFileService;

    public PaymentReminderScheduler(ICreditFileService creditFileService) {
        this.creditFileService = creditFileService;
    }

    @Scheduled(cron = "${scheduler.payment-reminder.cron:0 0 8 * * *}", zone = ClockConfig.BUSINESS_ZONE_ID)
    public void remindOverdueInstallments() {
        creditFileService.sendOverdueReminders();
    }
}
