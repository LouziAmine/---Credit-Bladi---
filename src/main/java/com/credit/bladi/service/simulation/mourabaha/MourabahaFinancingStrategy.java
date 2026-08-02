package com.credit.bladi.service.simulation.mourabaha;

import static com.credit.bladi.exception.SimulationException.SimulationExceptionType.MOURABAHA_PARAMETERS_NOT_FOUND;

import com.credit.bladi.exception.SimulationException;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.mourabaha.MourabahaAmortizationRow;
import com.credit.bladi.model.mourabaha.MourabahaFinancialSnapshot;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.simulation.request.SimulationRequest;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.repository.mourabaha.IMourabahaParametersRepository;
import com.credit.bladi.service.simulation.engine.TaegCalculationEngine;
import com.credit.bladi.service.simulation.strategy.FinancingCalculationStrategy;
import com.credit.bladi.service.simulation.strategy.FinancingResult;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MourabahaFinancingStrategy implements FinancingCalculationStrategy {

    private final IMourabahaParametersRepository mourabahaParametersRepository;

    private final MourabahaCalculationEngine mourabahaEngine;

    private final TaegCalculationEngine taegEngine;

    public MourabahaFinancingStrategy(IMourabahaParametersRepository mourabahaParametersRepository,
                                      MourabahaCalculationEngine mourabahaEngine,
                                      TaegCalculationEngine taegEngine) {
        this.mourabahaParametersRepository = mourabahaParametersRepository;
        this.mourabahaEngine = mourabahaEngine;
        this.taegEngine = taegEngine;
    }

    @Override
    public ProductType getProductType() {
        return ProductType.MOURABAHA;
    }

    @Override
    public FinancingResult simulate(SimulationRequest request, BamParametersModel bamParameters,
                                    Money totalMonthlyIncome) {
        Money capital = request.getAmount();
        int duration = request.getDuration();

        MourabahaParametersModel mourabahaParameters = mourabahaParametersRepository.getActiveParameters()
            .orElseThrow(() -> SimulationException.of(MOURABAHA_PARAMETERS_NOT_FOUND));

        bamParameters.assertWithinLtv(capital, request.getPropertyValue());
        bamParameters.assertEligibleAge(request.getAge(), duration);

        MourabahaFinancialSnapshot snapshot = mourabahaEngine.compute(
            capital, request.getPropertyValue(), mourabahaParameters, duration, bamParameters);

        bamParameters.assertWithinDebtRatio(snapshot.monthlyPaymentTtc(), totalMonthlyIncome);

        BigDecimal taeg = taegEngine.calculateTaeg(
            capital, snapshot.processingFees(), snapshot.monthlyPaymentTtc(), duration);
        List<MourabahaAmortizationRow> schedule = mourabahaEngine.computeSchedule(
            capital, mourabahaParameters, duration);

        return new FinancingResult(mourabahaParameters.getProfitRate(), null, null, null, null,
            snapshot, null, schedule, taeg);
    }
}
