package com.credit.bladi.model.servicing;

import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

public record EarlyRepaymentPolicy(BigDecimal monthsOfInterest, BigDecimal maxShareOfOutstanding) {
    private static final BigDecimal MONTHS_IN_YEAR_PERCENT = BigDecimal.valueOf(1200);

    public EarlyRepaymentPolicy {
        Objects.requireNonNull(monthsOfInterest, "monthsOfInterest cannot be null");
        Objects.requireNonNull(maxShareOfOutstanding, "maxShareOfOutstanding cannot be null");
        if (monthsOfInterest.signum() < 0 || maxShareOfOutstanding.signum() < 0) {
            throw new IllegalArgumentException("Early repayment indemnity parameters cannot be negative");
        }
    }

    public Money indemnity(Money repaidPrincipal, Rate creditRate, Money outstandingBefore) {
        BigDecimal interestCap = repaidPrincipal.amount()
            .multiply(creditRate.value(), MathContext.DECIMAL128)
            .multiply(monthsOfInterest, MathContext.DECIMAL128)
            .divide(MONTHS_IN_YEAR_PERCENT, MathContext.DECIMAL128);
        BigDecimal outstandingCap = outstandingBefore.amount().multiply(maxShareOfOutstanding, MathContext.DECIMAL128);
        return Money.of(interestCap.min(outstandingCap).setScale(2, RoundingMode.HALF_UP));
    }
}
