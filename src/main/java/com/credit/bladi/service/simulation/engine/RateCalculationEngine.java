package com.credit.bladi.service.simulation.engine;

import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.simulation.AmortizationRow;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public final class RateCalculationEngine {

    private static final MathContext PRECISION = MathContext.DECIMAL128;

    public RateCalculationEngine() {
    }

    public BigDecimal calculateMonthlyRate(Rate annualRate) {
        return annualRate.value().divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
    }

    public Money calculateMonthlyPayment(Money capital, Rate annualRate, int months) {
        BigDecimal tm = calculateMonthlyRate(annualRate);
        BigDecimal onePlusTm = BigDecimal.ONE.add(tm, PRECISION);
        BigDecimal factor = onePlusTm.pow(months, PRECISION);
        BigDecimal numerator = tm.multiply(factor, PRECISION);
        BigDecimal denominator = factor.subtract(BigDecimal.ONE, PRECISION);
        BigDecimal result = capital.amount()
            .multiply(numerator.divide(denominator, PRECISION), PRECISION)
            .setScale(2, RoundingMode.HALF_UP);
        return Money.of(result);
    }

    public List<AmortizationRow> generateAmortizationSchedule(Money capital, BigDecimal monthlyRate,
                                                               Money monthlyPayment, int months) {
        List<AmortizationRow> schedule = new ArrayList<>(months);
        Money balance = capital;
        for (int installmentNumber = 1; installmentNumber <= months; installmentNumber++) {
            Money beginningBalance = balance;
            Money interest = Money.of(beginningBalance.amount()
                .multiply(monthlyRate, PRECISION)
                .setScale(2, RoundingMode.HALF_UP));
            Money principal;
            Money payment;
            if (installmentNumber < months) {
                principal = monthlyPayment.subtract(interest);
                payment = monthlyPayment;
            } else {
                principal = beginningBalance;
                payment = principal.add(interest);
            }
            Money endingBalance = beginningBalance.subtract(principal);
            schedule.add(new AmortizationRow(
                installmentNumber, beginningBalance, interest, principal, payment, endingBalance));
            balance = endingBalance;
        }
        return schedule;
    }

    public Money calculateTotalInterestCost(Money monthlyPayment, int months, Money capital) {
        BigDecimal result = monthlyPayment.amount()
            .multiply(BigDecimal.valueOf(months))
            .subtract(capital.amount())
            .setScale(2, RoundingMode.HALF_UP);
        return Money.of(result);
    }

    public Money calculateTotalCost(Money monthlyPayment, int months) {
        BigDecimal result = monthlyPayment.amount()
            .multiply(BigDecimal.valueOf(months))
            .setScale(2, RoundingMode.HALF_UP);
        return Money.of(result);
    }

    public Money calculateRegistrationFees(Money propertyValue, BamParametersModel params) {
        return propertyValue.multiply(params.getRegistrationFeeRatio().value());
    }

    public Money calculateLandConservationFees(Money propertyValue, BamParametersModel params) {
        return propertyValue.multiply(params.getLandConservationRatio().value());
    }

    public Money calculateNotaryFees(Money propertyValue, BamParametersModel params) {
        return propertyValue.multiply(params.getNotaryFeeRatio().value());
    }

    public Money calculateProcessingFees(Money capital, BamParametersModel params) {
        Money fees = capital.multiply(params.getProcessingFeesRatio().value());
        if (fees.isLessThan(params.getMinProcessingFees())) {
            return params.getMinProcessingFees();
        }
        if (fees.isGreaterThan(params.getMaxProcessingFees())) {
            return params.getMaxProcessingFees();
        }
        return fees;
    }

    public Money calculateAdiMonthlyPremium(Money capital, BamParametersModel params, boolean coBorrower) {
        BigDecimal multiplier = coBorrower ? BigDecimal.valueOf(2) : BigDecimal.ONE;
        BigDecimal result = capital.amount()
            .multiply(params.getAdiMonthlyRate().value())
            .multiply(multiplier)
            .setScale(2, RoundingMode.HALF_UP);
        return Money.of(result);
    }

    public Money calculateTotalAdiCost(Money adiMonthlyPremium, int months) {
        BigDecimal result = adiMonthlyPremium.amount()
            .multiply(BigDecimal.valueOf(months))
            .setScale(2, RoundingMode.HALF_UP);
        return Money.of(result);
    }
}