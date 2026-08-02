package com.credit.bladi.service.simulation.engine;

import com.credit.bladi.model.vo.Money;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public final class TaegCalculationEngine {

    private static final MathContext PRECISION = MathContext.DECIMAL128;
    private static final int DEFAULT_MAX_ITERATIONS = 200;
    private static final BigDecimal DEFAULT_TOLERANCE = new BigDecimal("0.0000000001");

    private final int maxIterations;
    private final BigDecimal tolerance;

    public TaegCalculationEngine() {
        this(DEFAULT_MAX_ITERATIONS, DEFAULT_TOLERANCE);
    }

    TaegCalculationEngine(int maxIterations, BigDecimal tolerance) {
        this.maxIterations = maxIterations;
        this.tolerance = tolerance;
    }

    public BigDecimal calculateTaeg(Money capital, Money processingFees, Money totalMonthlyPayment, int months) {
        BigDecimal netAmount = capital.amount().subtract(processingFees.amount());
        BigDecimal payment = totalMonthlyPayment.amount();

        BigDecimal low = BigDecimal.ZERO;
        BigDecimal high = BigDecimal.ONE;
        BigDecimal monthlyRate = BigDecimal.ZERO;

        for (int i = 0; i < maxIterations && high.subtract(low).compareTo(tolerance) > 0; i++) {
            monthlyRate = low.add(high).divide(BigDecimal.valueOf(2), PRECISION);
            BigDecimal presentValue = presentValueOfPayments(payment, monthlyRate, months);
            if (presentValue.compareTo(netAmount) > 0) {
                low = monthlyRate;
            } else {
                high = monthlyRate;
            }
        }

        BigDecimal onePlusMonthly = BigDecimal.ONE.add(monthlyRate, PRECISION);
        BigDecimal annualFactor = onePlusMonthly.pow(12, PRECISION);
        BigDecimal taeg = annualFactor.subtract(BigDecimal.ONE, PRECISION).multiply(BigDecimal.valueOf(100));
        return taeg.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal presentValueOfPayments(BigDecimal payment, BigDecimal monthlyRate, int months) {
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return payment.multiply(BigDecimal.valueOf(months), PRECISION);
        }
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate, PRECISION);
        BigDecimal discountFactor = BigDecimal.ONE.subtract(
                BigDecimal.ONE.divide(onePlusRate.pow(months, PRECISION), PRECISION), PRECISION);
        BigDecimal annuityFactor = discountFactor.divide(monthlyRate, PRECISION);
        return payment.multiply(annuityFactor, PRECISION);
    }
}
