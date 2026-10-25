package com.credit.bladi.model.verification;

import com.credit.bladi.model.enums.RepaymentStatus;
import java.math.BigDecimal;

public record OutstandingLoan(
    String institution,
    String creditType,
    BigDecimal amount,
    RepaymentStatus repaymentStatus
) {
}
