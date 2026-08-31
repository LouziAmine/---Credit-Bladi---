package com.credit.bladi.service.mortgage.mourabaha;

import static com.credit.bladi.exception.MortgageCreditException.MortgageCreditExceptionType.MOURABAHA_PARAMETERS_NOT_FOUND;

import com.credit.bladi.exception.MortgageCreditException;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.mortgage.MortgageAmortizationSnapshot;
import com.credit.bladi.model.mortgage.request.MortgageCreditRequest;
import com.credit.bladi.model.mourabaha.MourabahaAmortizationRow;
import com.credit.bladi.model.mourabaha.MourabahaFinancialSnapshot;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.MortgageDemandDetail;
import com.credit.bladi.repository.mourabaha.IMourabahaParametersRepository;
import com.credit.bladi.repository.pricing.IAgreedPricingRepository;
import com.credit.bladi.service.calculation.TaegCalculationEngine;
import com.credit.bladi.service.mortgage.strategy.FinancingCalculationStrategy;
import com.credit.bladi.service.mortgage.strategy.FinancingResult;
import com.credit.bladi.service.mortgage.strategy.MourabahaFinancingResult;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MourabahaFinancingStrategy implements FinancingCalculationStrategy {
    private final IMourabahaParametersRepository mourabahaParametersRepository;

    private final MourabahaCalculationEngine mourabahaEngine;

    private final TaegCalculationEngine taegEngine;

    private final IAgreedPricingRepository agreedPricingRepository;

    public MourabahaFinancingStrategy(IMourabahaParametersRepository mourabahaParametersRepository,
                                      MourabahaCalculationEngine mourabahaEngine,
                                      TaegCalculationEngine taegEngine,
                                      IAgreedPricingRepository agreedPricingRepository) {
        this.mourabahaParametersRepository = mourabahaParametersRepository;
        this.mourabahaEngine = mourabahaEngine;
        this.taegEngine = taegEngine;
        this.agreedPricingRepository = agreedPricingRepository;
    }

    @Override
    public ProductType getProductType() {
        return ProductType.MOURABAHA;
    }

    @Override
    public FinancingResult simulate(MortgageCreditRequest request, BamParametersModel bamParameters,
                                    Money totalMonthlyIncome) {
        Money capital = request.getAmount();
        int duration = request.getDuration();
        boolean coBorrower = Boolean.TRUE.equals(request.getCoBorrower());

        MourabahaParametersModel mourabahaParameters = mourabahaParametersRepository.getActiveParameters()
            .orElseThrow(() -> MortgageCreditException.of(MOURABAHA_PARAMETERS_NOT_FOUND));

        bamParameters.assertWithinLtv(capital, request.getPropertyValue());
        bamParameters.assertEligibleAge(request.getAge(), duration);

        MourabahaFinancialSnapshot snapshot = mourabahaEngine.compute(
            capital, request.getPropertyValue(), mourabahaParameters, duration, bamParameters, coBorrower);

        bamParameters.assertWithinDebtRatio(snapshot.allInMonthlyPaymentTtc(), totalMonthlyIncome);

        BigDecimal taeg = taegEngine.calculateTaeg(
            capital, snapshot.processingFees(), snapshot.allInMonthlyPaymentTtc(), duration);
        List<MourabahaAmortizationRow> schedule = mourabahaEngine.computeSchedule(
            capital, mourabahaParameters, duration);

        return new MourabahaFinancingResult(mourabahaParameters.getProfitRate(), snapshot, schedule, taeg);
    }

    @Override
    public void assertEligible(MortgageDemandDetail detail, Money amount, Rate rate, int duration,
            BamParametersModel bamParameters) {
        if (detail.propertyValue() != null) {
            bamParameters.assertWithinLtv(amount, detail.propertyValue());
        }
        if (detail.age() != null) {
            bamParameters.assertEligibleAge(detail.age(), duration);
        }
        Money totalMonthlyIncome = detail.totalMonthlyIncome();
        if (totalMonthlyIncome != null && detail.propertyValue() != null) {
            MourabahaFinancialSnapshot snapshot = mourabahaEngine.compute(amount, detail.propertyValue(),
                activeTermsAt(rate), duration, bamParameters, Boolean.TRUE.equals(detail.coBorrower()));
            bamParameters.assertWithinDebtRatio(snapshot.allInMonthlyPaymentTtc(), totalMonthlyIncome);
        }
    }

    @Override
    public MortgageAmortizationSnapshot computeAmortizationSchedule(DemandModel demand, Money amount, int duration,
            Rate rateOverride, Money propertyValue, BamParametersModel bamParameters, boolean coBorrower) {
        MourabahaParametersModel mourabahaParameters = agreedTerms(demand.id(),
            rateOverride != null ? rateOverride : demand.applicableRate());
        MourabahaFinancialSnapshot snapshot = mourabahaEngine.compute(amount, propertyValue, mourabahaParameters,
            duration, bamParameters, coBorrower);
        List<MourabahaAmortizationRow> schedule = mourabahaEngine.computeSchedule(amount, mourabahaParameters,
            duration);
        BigDecimal taeg = taegEngine.calculateTaeg(amount, snapshot.processingFees(),
            snapshot.allInMonthlyPaymentTtc(), duration);
        return new MortgageAmortizationSnapshot(ProductType.MOURABAHA, mourabahaParameters.getProfitRate(), taeg,
            null, null, snapshot, schedule);
    }

    private MourabahaParametersModel activeTermsAt(Rate profitRate) {
        MourabahaParametersModel active = mourabahaParametersRepository.getActiveParameters()
            .orElseThrow(() -> MortgageCreditException.of(MOURABAHA_PARAMETERS_NOT_FOUND));
        return profitRate != null ? active.withProfitRate(profitRate) : active;
    }

    private MourabahaParametersModel agreedTerms(Long demandId, Rate agreedProfitRate) {
        MourabahaParametersModel active = activeTermsAt(null);
        MourabahaParametersModel terms = agreedPricingRepository.findByDemandId(demandId)
            .map(agreed -> agreed.applyTo(active))
            .orElse(active);
        return agreedProfitRate != null ? terms.withProfitRate(agreedProfitRate) : terms;
    }
}
