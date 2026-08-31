package com.credit.bladi.service.servicing;

import com.credit.bladi.model.workflow.event.DemandCompletedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class CreditFileEventListener {
    private final ICreditFileService creditFileService;

    public CreditFileEventListener(ICreditFileService creditFileService) {
        this.creditFileService = creditFileService;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onDemandCompleted(DemandCompletedEvent event) {
        creditFileService.openCreditFile(event.demandId(), event.triggeredByUsername());
    }
}
