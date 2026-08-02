package com.credit.bladi.service.simulation.conventional;

import static com.credit.bladi.exception.SimulationException.SimulationExceptionType.CSP_NOT_FOUND;

import com.credit.bladi.exception.SimulationException;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.simulation.AmortizationRow;
import com.credit.bladi.model.simulation.CreditRateModel;
import com.credit.bladi.model.simulation.FinancialSnapshot;
import com.credit.bladi.model.simulation.RateBoundsSnapshot;
import com.credit.bladi.model.simulation.request.SimulationRequest;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.repository.creditrate.ICreditRateRepository;
import com.credit.bladi.service.simulation.strategy.FinancingCalculationStrategy;
import com.credit.bladi.service.simulation.strategy.FinancingResult;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ConventionalFinancingStrategy implements FinancingCalculationStrategy {

    private final ICreditRateRepository creditRateRepository;

    private final RateStrategyFactory rateStrategyFactory;

    private final SimulationCalculator calculator;

    public ConventionalFinancingStrategy(ICreditRateRepository creditRateRepository,
                                         RateStrategyFactory rateStrategyFactory,
                                         SimulationCalculator calculator) {
        this.creditRateRepository = creditRateRepository;
        this.rateStrategyFactory = rateStrategyFactory;
        this.calculator = calculator;
    }

    @Override
    public ProductType getProductType() {
        return ProductType.CONVENTIONAL;
    }

    @Override
    public FinancingResult simulate(SimulationRequest request, BamParametersModel bamParameters,
                                    Money totalMonthlyIncome) {
        CspCode cspCode = request.getCspCode();
        Money capital = request.getAmount();
        int duration = request.getDuration();
        boolean coBorrower = Boolean.TRUE.equals(request.getCoBorrower());

        CreditRateModel creditRate = creditRateRepository.findActiveByCspCode(cspCode)
            .orElseThrow(() -> SimulationException.of(CSP_NOT_FOUND));
        RateCalculationStrategy cspStrategy = rateStrategyFactory.getStrategy(cspCode);

        Rate applicableRate = validateEligibilityAndSelectRate(request, creditRate, bamParameters, cspStrategy);

        RateBoundsSnapshot bounds = calculator.computeBounds(
            capital, request.getPropertyValue(), creditRate, duration, bamParameters, coBorrower);
        FinancialSnapshot applicableSnapshot = calculator.computeAtRate(
            capital, request.getPropertyValue(), applicableRate, duration, bamParameters, coBorrower);

        bamParameters.assertWithinDebtRatio(applicableSnapshot.allInMonthlyPayment(), totalMonthlyIncome);

        BigDecimal taeg = calculator.calculateTaeg(capital, applicableSnapshot, duration);
        List<AmortizationRow> schedule = calculator.computeSchedule(
            capital, applicableRate, applicableSnapshot.monthlyPayment(), duration);

        return new FinancingResult(applicableRate, creditRate.getRateMin(), creditRate.getRateMax(),
            applicableSnapshot, bounds, null, schedule, null, taeg);
    }

    private Rate validateEligibilityAndSelectRate(SimulationRequest request, CreditRateModel creditRate,
                                                  BamParametersModel bamParameters,
                                                  RateCalculationStrategy strategy) {
        creditRate.getDurationRange().assertContains(request.getDuration());
        bamParameters.assertWithinLtv(request.getAmount(), request.getPropertyValue());
        strategy.validateBamConstraints(request, bamParameters);
        return strategy.calculateRate(creditRate);
    }
}
