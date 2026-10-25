package com.credit.bladi.model.servicing;

import com.credit.bladi.model.vo.Money;
import java.time.Instant;
import java.util.UUID;

public record EarlyRepaymentModel(
        Long id,
        UUID publicId,
        Long creditFileId,
        boolean fullRepayment,
        Money outstandingBefore,
        Money repaidPrincipal,
        Money accruedInterest,
        Money indemnity,
        Money totalPaid,
        Money outstandingAfter,
        String performedBy,
        Instant performedAt
) {
}
