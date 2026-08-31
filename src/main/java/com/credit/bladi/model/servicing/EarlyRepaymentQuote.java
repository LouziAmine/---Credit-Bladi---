package com.credit.bladi.model.servicing;

import com.credit.bladi.model.vo.Money;
import java.time.LocalDate;
import java.util.List;

public record EarlyRepaymentQuote(
        boolean fullRepayment,
        LocalDate valueDate,
        Money outstandingBefore,
        Money repaidPrincipal,
        Money accruedInterest,
        Money indemnity,
        Money totalToPay,
        Money outstandingAfter,
        List<RescheduledInstallment> newSchedule
) {
    public record RescheduledInstallment(Long installmentId, int sequenceNumber, LocalDate dueDate, Money amount) {
    }
}
