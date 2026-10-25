package com.credit.bladi.service.mortgage.conventional;

import static com.credit.bladi.exception.BAMException.BAMExceptionType.DURATION_LIMIT_EXCEEDED;
import static com.credit.bladi.exception.MortgageCreditException.MortgageCreditExceptionType.CSP_NOT_FOUND;
import static com.credit.bladi.exception.MortgageCreditException.MortgageCreditExceptionType.DEMAND_RATE_NOT_SET;

import com.credit.bladi.exception.BAMException;
import com.credit.bladi.exception.MortgageCreditException;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.creditrate.CreditRateModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.mortgage.FinancialSnapshot;
import com.credit.bladi.model.mortgage.MortgageAmortizationSnapshot;
import com.credit.bladi.model.mortgage.RateBoundsSnapshot;
import com.credit.bladi.model.mortgage.request.MortgageCreditRequest;
import com.credit.bladi.model.vo.AmortizationRow;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.MortgageDemandDetail;
import com.credit.bladi.repository.creditrate.ICreditRateRepository;
import com.credit.bladi.service.mortgage.strategy.ConventionalFinancingResult;
import com.credit.bladi.service.mortgage.strategy.FinancingCalculationStrategy;
import com.credit.bladi.service.mortgage.strategy.FinancingResult;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ConventionalFinancingStrategy implements FinancingCalculationStrategy {
    private final ICreditRateRepository creditRateRepository;

    private final MortgageCreditCalculator calculator;

    public ConventionalFinancingStrategy(ICreditRateRepository creditRateRepository,
                                         MortgageCreditCalculator calculator) {
        this.creditRateRepository = creditRateRepository;
        this.calculator = calculator;
    }

    @Override
    public ProductType getProductType() {
        return ProductType.CONVENTIONAL;
    }

    @Override
    public FinancingResult simulate(MortgageCreditRequest request, BamParametersModel bamParameters,
                                    Money totalMonthlyIncome) {
        CspCode cspCode = request.getCspCode();
        Money capital = request.getAmount();
        int duration = request.getDuration();
        boolean coBorrower = Boolean.TRUE.equals(request.getCoBorrower());

        CreditRateModel creditRate = creditRateRepository.findActiveByCspCode(cspCode)
            .orElseThrow(() -> MortgageCreditException.of(CSP_NOT_FOUND));

        assertProductRules(creditRate, capital, request.getPropertyValue(), request.getAge(), duration, bamParameters);
        Rate applicableRate = creditRate.calculateRate();

        RateBoundsSnapshot bounds = calculator.computeBounds(
            capital, request.getPropertyValue(), creditRate, duration, bamParameters, coBorrower);
        FinancialSnapshot applicableSnapshot = calculator.computeAtRate(
            capital, request.getPropertyValue(), applicableRate, duration, bamParameters, coBorrower);

        bamParameters.assertWithinDebtRatio(applicableSnapshot.allInMonthlyPayment(), totalMonthlyIncome);

        BigDecimal taeg = calculator.calculateTaeg(capital, applicableSnapshot, duration);
        List<AmortizationRow> schedule = calculator.computeSchedule(
            capital, applicableRate, applicableSnapshot.monthlyPayment(), duration);

        return new ConventionalFinancingResult(applicableRate, creditRate.getRateMin(), creditRate.getRateMax(),
            applicableSnapshot, bounds, schedule, taeg);
    }

    @Override
    public void assertEligible(MortgageDemandDetail detail, Money amount, Rate rate, int duration,
            BamParametersModel bamParameters) {
        CreditRateModel creditRate = creditRateRepository.findActiveByCspCode(detail.cspCode())
            .orElseThrow(() -> MortgageCreditException.of(CSP_NOT_FOUND));
        assertProductRules(creditRate, amount, detail.propertyValue(), detail.age(), duration, bamParameters);
        Money totalMonthlyIncome = detail.totalMonthlyIncome();
        if (totalMonthlyIncome != null && detail.propertyValue() != null) {
            FinancialSnapshot snapshot = calculator.computeAtRate(amount, detail.propertyValue(), rate, duration,
                bamParameters, Boolean.TRUE.equals(detail.coBorrower()));
            bamParameters.assertWithinDebtRatio(snapshot.allInMonthlyPayment(), totalMonthlyIncome);
        }
    }

    @Override
    public MortgageAmortizationSnapshot computeAmortizationSchedule(DemandModel demand, Money amount, int duration,
            Rate rateOverride, Money propertyValue, BamParametersModel bamParameters, boolean coBorrower) {
        Rate rate = rateOverride != null ? rateOverride : demand.applicableRate();
        if (rate == null) {
            throw MortgageCreditException.of(DEMAND_RATE_NOT_SET);
        }
        FinancialSnapshot snapshot = calculator.computeAtRate(amount, propertyValue, rate, duration, bamParameters,
            coBorrower);
        List<AmortizationRow> schedule = calculator.computeSchedule(amount, rate, snapshot.monthlyPayment(),
            duration);
        BigDecimal taeg = calculator.calculateTaeg(amount, snapshot, duration);
        return new MortgageAmortizationSnapshot(ProductType.CONVENTIONAL, rate, taeg, snapshot, schedule, null, null);
    }

    private static void assertProductRules(CreditRateModel creditRate, Money amount, Money propertyValue,
            Integer age, int duration, BamParametersModel bamParameters) {
        if (!creditRate.getDurationRange().contains(duration)) {
            throw BAMException.of(DURATION_LIMIT_EXCEEDED);
        }
        if (propertyValue != null) {
            bamParameters.assertWithinLtv(amount, propertyValue);
        }
        if (age != null) {
            bamParameters.assertEligibleAge(age, duration);
        }
    }
}
