package com.credit.bladi.model.servicing;

import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public record LateInterestPolicy(BigDecimal marginPoints) {
    private static final BigDecimal DAYS_IN_YEAR = BigDecimal.valueOf(360);

    private static final BigDecimal PERCENT = BigDecimal.valueOf(100);

    public LateInterestPolicy {
        Objects.requireNonNull(marginPoints, "marginPoints cannot be null");
        if (marginPoints.signum() < 0) {
            throw new IllegalArgumentException("Late interest margin cannot be negative: " + marginPoints);
        }
    }

    public Money accrued(InstallmentModel installment, Rate creditRate, LocalDate until) {
        long daysLate = ChronoUnit.DAYS.between(installment.dueDate(), until);
        if (installment.isSettled() || daysLate <= 0) {
            return Money.of(BigDecimal.ZERO.setScale(2));
        }
        BigDecimal annualRate = (creditRate != null ? creditRate.value() : BigDecimal.ZERO).add(marginPoints);
        BigDecimal interest = installment.amount().amount()
            .multiply(annualRate, MathContext.DECIMAL128)
            .multiply(BigDecimal.valueOf(daysLate), MathContext.DECIMAL128)
            .divide(PERCENT.multiply(DAYS_IN_YEAR), MathContext.DECIMAL128)
            .setScale(2, RoundingMode.HALF_UP);
        return Money.of(interest);
    }
}
