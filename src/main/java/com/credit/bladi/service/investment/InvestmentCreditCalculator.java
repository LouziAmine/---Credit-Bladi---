package com.credit.bladi.service.investment;

import com.credit.bladi.model.investment.InvestmentCreditParametersModel;
import com.credit.bladi.model.investment.InvestmentFinancialSnapshot;
import com.credit.bladi.model.vo.AmortizationRow;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.service.calculation.RateCalculationEngine;
import com.credit.bladi.service.calculation.TaegCalculationEngine;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class InvestmentCreditCalculator {
    private static final int MONTHS_PER_YEAR = 12;

    private final RateCalculationEngine rateCalculationEngine;

    private final TaegCalculationEngine taegCalculationEngine;

    public InvestmentCreditCalculator(RateCalculationEngine rateCalculationEngine,
                                      TaegCalculationEngine taegCalculationEngine) {
        this.rateCalculationEngine = rateCalculationEngine;
        this.taegCalculationEngine = taegCalculationEngine;
    }

    public InvestmentFinancialSnapshot computeAtRate(Money amount, Rate rate, int duration,
                                                      InvestmentCreditParametersModel parameters) {
        Money processingFees = amount.multiply(parameters.getProcessingFeesRatio().value());
        Money monthlyPayment = rateCalculationEngine.calculateMonthlyPayment(amount, rate, duration);
        Money insurancePremium = amount.multiply(parameters.getInsurancePremiumRatio().value());
        Money allInMonthlyPayment = monthlyPayment.add(insurancePremium);
        Money annualDebtService = rateCalculationEngine.calculateTotalCost(monthlyPayment, MONTHS_PER_YEAR);
        Money totalCost = rateCalculationEngine.calculateTotalCost(monthlyPayment, duration);
        Money totalInterest = rateCalculationEngine.calculateTotalInterestCost(monthlyPayment, duration, amount);
        Money totalInsuranceCost = rateCalculationEngine.calculateTotalAdiCost(insurancePremium, duration);
        BigDecimal monthlyRate = rateCalculationEngine.calculateMonthlyRate(rate);
        List<AmortizationRow> schedule = rateCalculationEngine.generateAmortizationSchedule(
            amount, monthlyRate, monthlyPayment, duration);
        BigDecimal taeg = taegCalculationEngine.calculateTaeg(amount, processingFees, allInMonthlyPayment, duration);

        return new InvestmentFinancialSnapshot(rate, monthlyPayment, annualDebtService, totalInterest, totalCost,
            processingFees, taeg, schedule, insurancePremium, allInMonthlyPayment, totalInsuranceCost);
    }
}
