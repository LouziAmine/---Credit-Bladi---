package com.credit.bladi.service.simulation.conventional;

import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.simulation.AmortizationRow;
import com.credit.bladi.model.simulation.CreditRateModel;
import com.credit.bladi.model.simulation.FinancialSnapshot;
import com.credit.bladi.model.simulation.RateBoundsSnapshot;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.service.simulation.engine.RateCalculationEngine;
import com.credit.bladi.service.simulation.engine.TaegCalculationEngine;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SimulationCalculator {

    private final RateCalculationEngine engine;

    private final TaegCalculationEngine taegEngine;

    public SimulationCalculator(RateCalculationEngine engine, TaegCalculationEngine taegEngine) {
        this.engine = engine;
        this.taegEngine = taegEngine;
    }

    public RateBoundsSnapshot computeBounds(Money capital, Money propertyValue, CreditRateModel creditRate,
                                            int months, BamParametersModel params, boolean coBorrower) {
        return new RateBoundsSnapshot(
                computeAtRate(capital, propertyValue, creditRate.getRateMin(), months, params, coBorrower),
                computeAtRate(capital, propertyValue, creditRate.getRateMax(), months, params, coBorrower)
        );
    }

    public List<AmortizationRow> computeSchedule(Money capital, Rate applicableRate, Money monthlyPayment,
                                                 int months) {
        BigDecimal monthlyRate = engine.calculateMonthlyRate(applicableRate);
        return engine.generateAmortizationSchedule(capital, monthlyRate, monthlyPayment, months);
    }

    public BigDecimal calculateTaeg(Money capital, FinancialSnapshot snapshot, int months) {
        Money totalMonthlyPayment = snapshot.monthlyPayment().add(snapshot.adiMonthlyPremium());
        return taegEngine.calculateTaeg(capital, snapshot.processingFees(), totalMonthlyPayment, months);
    }

    public FinancialSnapshot computeAtRate(Money capital, Money propertyValue, Rate annualRate, int months,
                                       BamParametersModel params, boolean coBorrower) {
        Money monthlyPayment = engine.calculateMonthlyPayment(capital, annualRate, months);
        Money adiMonthlyPremium = engine.calculateAdiMonthlyPremium(capital, params, coBorrower);
        Money allInMonthlyPayment = monthlyPayment.add(adiMonthlyPremium);
        Money totalInterest = engine.calculateTotalInterestCost(monthlyPayment, months, capital);
        Money totalAdiCost = engine.calculateTotalAdiCost(adiMonthlyPremium, months);
        Money totalCost = engine.calculateTotalCost(monthlyPayment, months);
        Money registrationFees = engine.calculateRegistrationFees(propertyValue, params);
        Money landConservationFees = engine.calculateLandConservationFees(propertyValue, params);
        Money notaryFees = engine.calculateNotaryFees(propertyValue, params);
        Money processingFees = engine.calculateProcessingFees(capital, params);
        Money totalFees = registrationFees.add(landConservationFees).add(notaryFees).add(processingFees);

        return new FinancialSnapshot(
                monthlyPayment, adiMonthlyPremium, allInMonthlyPayment,
                totalInterest, totalAdiCost, totalCost,
                registrationFees, landConservationFees, notaryFees,
                processingFees, totalFees
        );
    }
}