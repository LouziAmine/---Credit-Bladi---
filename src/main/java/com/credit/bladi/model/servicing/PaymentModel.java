package com.credit.bladi.model.servicing;

import com.credit.bladi.model.vo.Money;
import java.time.Instant;

public record PaymentModel(
        Long id,
        Long installmentId,
        Money amount,
        Instant paidAt
) {
}
