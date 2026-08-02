package com.credit.bladi.service.simulation;

import static com.credit.bladi.exception.SimulationException.SimulationExceptionType.BAM_PARAMETERS_NOT_FOUND;

import com.credit.bladi.exception.BAMException;
import com.credit.bladi.exception.SimulationException;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.simulation.request.SimulationRequest;
import com.credit.bladi.model.simulation.response.SimulationResponse;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.repository.bam.IBamParametersRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import com.credit.bladi.service.simulation.strategy.FinancingCalculationStrategy;
import com.credit.bladi.service.simulation.strategy.FinancingResult;
import com.credit.bladi.service.simulation.strategy.FinancingStrategyFactory;
import java.math.BigDecimal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SimulationService implements ISimulationService {

    private static final Logger LOGGER = LogManager.getLogger(SimulationService.class);

    private final IBamParametersRepository bamParametersRepository;

    private final FinancingStrategyFactory financingStrategyFactory;

    private final IAuditService auditService;

    private final ICurrentActorProvider currentActorProvider;

    public SimulationService(IBamParametersRepository bamParametersRepository,
                             FinancingStrategyFactory financingStrategyFactory,
                             IAuditService auditService,
                             ICurrentActorProvider currentActorProvider) {
        this.bamParametersRepository = bamParametersRepository;
        this.financingStrategyFactory = financingStrategyFactory;
        this.auditService = auditService;
        this.currentActorProvider = currentActorProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public SimulationResponse simulate(SimulationRequest request) {

        ProductType productType = request.getProductType();
        Money capital = request.getAmount();
        int duration = request.getDuration();

        LOGGER.info("Simulate - amount={}, duration={}, productType={}, cspCode={}, nationalityCode={}, "
                + "moroccanResident={}",
            capital, duration, productType, request.getCspCode(),
            request.getNationalityCode(), request.getMoroccanResident());

        BamParametersModel bamParameters = bamParametersRepository.getActiveParameters()
            .orElseThrow(() -> SimulationException.of(BAM_PARAMETERS_NOT_FOUND));

        FinancingCalculationStrategy strategy = financingStrategyFactory.getStrategy(productType);
        boolean coBorrower = Boolean.TRUE.equals(request.getCoBorrower());
        Money totalMonthlyIncome = Money.of(totalMonthlyIncome(request, coBorrower));

        FinancingResult result;
        try {
            result = strategy.simulate(request, bamParameters, totalMonthlyIncome);
        } catch (BAMException rejection) {
            auditService.log(currentActorProvider.getCurrentUsername(), AuditAction.SIMULATION_REJECTED,
                "reason=" + rejection.getType() + ", productType=" + productType
                    + ", amount=" + capital + ", duration=" + duration);
            throw rejection;
        }

        auditService.log(currentActorProvider.getCurrentUsername(), AuditAction.SIMULATION,
            "productType=" + productType + ", amount=" + capital + ", duration=" + duration
                + ", totalMonthlyIncome=" + totalMonthlyIncome
                + ", applicableRate=" + result.applicableRate()
                + ", taeg=" + result.taeg() + "%");

        return buildResponse(request, productType, capital, duration, totalMonthlyIncome, result);
    }

    private static BigDecimal totalMonthlyIncome(SimulationRequest request, boolean coBorrower) {
        BigDecimal income = request.getMonthlyIncome().amount();
        if (coBorrower) {
            income = income.add(request.getCoBorrowerMonthlyIncome().amount());
        }
        return income;
    }

    private static SimulationResponse buildResponse(SimulationRequest request, ProductType productType,
                                                     Money capital, int duration, Money totalMonthlyIncome,
                                                     FinancingResult result) {
        return SimulationResponse.getBuilder()
            .amount(capital)
            .duration(duration)
            .productType(productType)
            .cspCode(request.getCspCode())
            .nationalityCode(request.getNationalityCode())
            .moroccanResident(request.getMoroccanResident())
            .age(request.getAge())
            .totalMonthlyIncome(totalMonthlyIncome)
            .applicableRate(result.applicableRate())
            .rateMin(result.rateMin())
            .rateMax(result.rateMax())
            .applicable(result.applicableSnapshot())
            .bounds(result.bounds())
            .mourabahaSnapshot(result.mourabahaSnapshot())
            .amortizationSchedule(result.amortizationSchedule())
            .mourabahaAmortizationSchedule(result.mourabahaAmortizationSchedule())
            .taeg(result.taeg())
            .build();
    }
}
