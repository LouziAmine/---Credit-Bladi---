package com.credit.bladi.service.servicing;

import static com.credit.bladi.exception.CreditFileException.CreditFileExceptionType.EARLY_REPAYMENT_AMOUNT_INVALID;

import com.credit.bladi.exception.CreditFileException;
import com.credit.bladi.model.servicing.EarlyRepaymentPolicy;
import com.credit.bladi.model.servicing.EarlyRepaymentQuote;
import com.credit.bladi.model.servicing.EarlyRepaymentQuote.RescheduledInstallment;
import com.credit.bladi.model.servicing.InstallmentModel;
import com.credit.bladi.model.vo.AmortizationRow;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.service.calculation.RateCalculationEngine;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class EarlyRepaymentCalculator {
    private final RateCalculationEngine rateCalculationEngine;

    private final EarlyRepaymentPolicy policy;

    public EarlyRepaymentCalculator(RateCalculationEngine rateCalculationEngine, EarlyRepaymentPolicy policy) {
        this.rateCalculationEngine = rateCalculationEngine;
        this.policy = policy;
    }

    public EarlyRepaymentQuote quote(List<InstallmentModel> remaining, Rate creditRate, Money requestedPrincipal,
            LocalDate valueDate) {
        BigDecimal monthlyRate = rateCalculationEngine.calculateMonthlyRate(creditRate);
        Money outstanding = presentValue(remaining, monthlyRate);
        if (requestedPrincipal != null
                && (requestedPrincipal.amount().signum() <= 0 || requestedPrincipal.isGreaterThan(outstanding))) {
            throw CreditFileException.of(EARLY_REPAYMENT_AMOUNT_INVALID);
        }
        boolean full = requestedPrincipal == null || requestedPrincipal.equalsValue(outstanding);
        Money repaid = full ? outstanding : requestedPrincipal;

        Money accruedInterest = accruedInterest(repaid, monthlyRate, remaining.getFirst().dueDate(), valueDate);
        Money indemnity = policy.indemnity(repaid, creditRate, outstanding);
        Money outstandingAfter = outstanding.subtract(repaid);
        List<RescheduledInstallment> newSchedule = full
            ? List.of()
            : reschedule(remaining, outstandingAfter, creditRate, monthlyRate);

        return new EarlyRepaymentQuote(full, valueDate, outstanding, repaid, accruedInterest, indemnity,
            repaid.add(accruedInterest).add(indemnity), outstandingAfter, newSchedule);
    }

    private static Money presentValue(List<InstallmentModel> remaining, BigDecimal monthlyRate) {
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal discount = BigDecimal.ONE;
        BigDecimal total = BigDecimal.ZERO;
        for (InstallmentModel installment : remaining) {
            discount = discount.multiply(onePlusRate, MathContext.DECIMAL128);
            total = total.add(installment.amount().amount().divide(discount, MathContext.DECIMAL128));
        }
        return Money.of(total.setScale(2, RoundingMode.HALF_UP));
    }

    private static Money accruedInterest(Money repaid, BigDecimal monthlyRate, LocalDate nextDueDate,
            LocalDate valueDate) {
        LocalDate periodStart = nextDueDate.minusMonths(1);
        long periodDays = ChronoUnit.DAYS.between(periodStart, nextDueDate);
        long elapsedDays = Math.clamp(ChronoUnit.DAYS.between(periodStart, valueDate), 0, periodDays);
        BigDecimal interest = repaid.amount()
            .multiply(monthlyRate, MathContext.DECIMAL128)
            .multiply(BigDecimal.valueOf(elapsedDays), MathContext.DECIMAL128)
            .divide(BigDecimal.valueOf(periodDays), MathContext.DECIMAL128);
        return Money.of(interest.setScale(2, RoundingMode.HALF_UP));
    }

    private List<RescheduledInstallment> reschedule(List<InstallmentModel> remaining, Money outstandingAfter,
            Rate creditRate, BigDecimal monthlyRate) {
        int months = remaining.size();
        Money newPayment = rateCalculationEngine.calculateMonthlyPayment(outstandingAfter, creditRate, months);
        List<AmortizationRow> rows = rateCalculationEngine.generateAmortizationSchedule(outstandingAfter, monthlyRate,
            newPayment, months);
        List<RescheduledInstallment> schedule = new ArrayList<>(months);
        for (int i = 0; i < months; i++) {
            InstallmentModel installment = remaining.get(i);
            schedule.add(new RescheduledInstallment(installment.id(), installment.sequenceNumber(),
                installment.dueDate(), rows.get(i).payment()));
        }
        return schedule;
    }
}
