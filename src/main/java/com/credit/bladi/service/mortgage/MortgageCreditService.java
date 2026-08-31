package com.credit.bladi.service.mortgage;

import static com.credit.bladi.exception.MortgageCreditException.MortgageCreditExceptionType.BAM_PARAMETERS_NOT_FOUND;

import com.credit.bladi.exception.BAMException;
import com.credit.bladi.exception.MortgageCreditException;
import com.credit.bladi.exception.RegulatoryLimitException;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.mortgage.MortgageAmortizationSnapshot;
import com.credit.bladi.model.mortgage.request.MortgageCreditRequest;
import com.credit.bladi.model.mortgage.response.MortgageCreditResponse;
import com.credit.bladi.model.parameters.RegulatoryLimits;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.DemandSnapshot;
import com.credit.bladi.model.workflow.MortgageDemandDetail;
import com.credit.bladi.model.workflow.MortgageDemandSnapshotDetail;
import com.credit.bladi.repository.bam.IBamParametersRepository;
import com.credit.bladi.repository.pricing.IAgreedPricingRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.mortgage.strategy.ConventionalFinancingResult;
import com.credit.bladi.service.mortgage.strategy.FinancingCalculationStrategy;
import com.credit.bladi.service.mortgage.strategy.FinancingResult;
import com.credit.bladi.service.mortgage.strategy.FinancingStrategyFactory;
import com.credit.bladi.service.mortgage.strategy.MourabahaFinancingResult;
import com.credit.bladi.service.request.IRequestContext;
import com.credit.bladi.service.security.ICurrentActorProvider;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import java.math.BigDecimal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MortgageCreditService implements IMortgageCreditService {
    private static final Logger LOGGER = LogManager.getLogger(MortgageCreditService.class);

    private final IBamParametersRepository bamParametersRepository;

    private final FinancingStrategyFactory financingStrategyFactory;

    private final IAuditService auditService;

    private final ICurrentActorProvider currentActorProvider;

    private final IWorkflowEngineService workflowEngineService;

    private final IRequestContext requestContext;

    private final IAgreedPricingRepository agreedPricingRepository;

    private final RegulatoryLimits regulatoryLimits;

    public MortgageCreditService(IBamParametersRepository bamParametersRepository,
                             FinancingStrategyFactory financingStrategyFactory,
                             IAuditService auditService,
                             ICurrentActorProvider currentActorProvider,
                             IWorkflowEngineService workflowEngineService,
                             IRequestContext requestContext,
                             IAgreedPricingRepository agreedPricingRepository,
                             RegulatoryLimits regulatoryLimits) {
        this.bamParametersRepository = bamParametersRepository;
        this.financingStrategyFactory = financingStrategyFactory;
        this.auditService = auditService;
        this.currentActorProvider = currentActorProvider;
        this.workflowEngineService = workflowEngineService;
        this.requestContext = requestContext;
        this.agreedPricingRepository = agreedPricingRepository;
        this.regulatoryLimits = regulatoryLimits;
    }

    @Override
    @Transactional
    public MortgageCreditResponse simulate(MortgageCreditRequest request) {
        ProductType productType = request.getProductType();
        Money capital = request.getAmount();
        int duration = request.getDuration();

        LOGGER.info("Simulate - amount={}, duration={}, productType={}, cspCode={}, nationalityCode={}, "
                + "moroccanResident={}",
            capital, duration, productType, request.getCspCode(),
            request.getNationalityCode(), request.getMoroccanResident());

        BamParametersModel bamParameters = bamParametersRepository.getActiveParameters()
            .orElseThrow(() -> MortgageCreditException.of(BAM_PARAMETERS_NOT_FOUND));

        FinancingCalculationStrategy strategy = financingStrategyFactory.getStrategy(productType);
        boolean coBorrower = Boolean.TRUE.equals(request.getCoBorrower());
        Money totalMonthlyIncome = Money.of(totalMonthlyIncome(request, coBorrower));

        FinancingResult result;
        try {
            result = strategy.simulate(request, bamParameters, totalMonthlyIncome);
            regulatoryLimits.assertSimulationWithinMaxRate(result.applicableRate(), result.taeg());
        } catch (BAMException rejection) {
            auditService.log(currentActorProvider.getCurrentUsername(), AuditAction.SIMULATION_REJECTED,
                "reason=" + rejection.getType() + ", productType=" + productType
                    + ", amount=" + capital + ", duration=" + duration);
            throw rejection;
        } catch (RegulatoryLimitException rejection) {
            auditService.log(currentActorProvider.getCurrentUsername(), AuditAction.SIMULATION_REJECTED,
                "reason=" + rejection.getType() + ", productType=" + productType
                    + ", amount=" + capital + ", duration=" + duration);
            throw rejection;
        }

        DemandModel demand = workflowEngineService.openDemandFromSimulation(
            new DemandSnapshot(DemandType.MORTGAGE, request.getApplicantName(), capital, duration,
                result.applicableRate(), request.getEmail(), request.getPhone(), requestContext.getLanguage(),
                new MortgageDemandSnapshotDetail(productType, request.getCspCode(), request.getNationalityCode(),
                    request.getMoroccanResident(), request.getPropertyValue(), request.getMonthlyIncome(),
                    request.getAge(), coBorrower, request.getCoBorrowerMonthlyIncome())));

        auditService.log(currentActorProvider.getCurrentUsername(), AuditAction.SIMULATION,
            "productType=" + productType + ", amount=" + capital + ", duration=" + duration
                + ", totalMonthlyIncome=" + totalMonthlyIncome
                + ", applicableRate=" + result.applicableRate()
                + ", taeg=" + result.taeg() + "%"
                + ", demandReference=" + demand.reference());

        return buildResponse(request, productType, capital, duration, totalMonthlyIncome, result, demand.reference());
    }

    @Override
    @Transactional(readOnly = true)
    public MortgageAmortizationSnapshot getAmortizationSchedule(Long demandId, Money amountOverride,
            Rate rateOverride, Integer durationOverride) {
        DemandModel demand = workflowEngineService.getDemand(demandId);
        MortgageDemandDetail detail = demand.mortgageDetail();
        Money amount = amountOverride != null ? amountOverride : demand.amount();
        int duration = durationOverride != null ? durationOverride : demand.duration();

        BamParametersModel active = bamParametersRepository.getActiveParameters()
            .orElseThrow(() -> MortgageCreditException.of(BAM_PARAMETERS_NOT_FOUND));
        BamParametersModel bamParameters = agreedPricingRepository.findByDemandId(demandId)
            .map(agreed -> agreed.applyTo(active))
            .orElse(active);

        return financingStrategyFactory.getStrategy(detail.productType())
            .computeAmortizationSchedule(demand, amount, duration, rateOverride, detail.propertyValue(),
                bamParameters, Boolean.TRUE.equals(detail.coBorrower()));
    }

    private static BigDecimal totalMonthlyIncome(MortgageCreditRequest request, boolean coBorrower) {
        BigDecimal income = request.getMonthlyIncome().amount();
        if (coBorrower) {
            income = income.add(request.getCoBorrowerMonthlyIncome().amount());
        }
        return income;
    }

    private static MortgageCreditResponse buildResponse(MortgageCreditRequest request, ProductType productType,
                                                     Money capital, int duration, Money totalMonthlyIncome,
                                                     FinancingResult result, String demandReference) {
        MortgageCreditResponse.Builder builder = MortgageCreditResponse.getBuilder()
            .demandReference(demandReference)
            .amount(capital)
            .duration(duration)
            .productType(productType)
            .cspCode(request.getCspCode())
            .nationalityCode(request.getNationalityCode())
            .moroccanResident(request.getMoroccanResident())
            .age(request.getAge())
            .totalMonthlyIncome(totalMonthlyIncome)
            .applicableRate(result.applicableRate())
            .taeg(result.taeg());
        switch (result) {
            case ConventionalFinancingResult conventional -> builder
                .rateMin(conventional.rateMin())
                .rateMax(conventional.rateMax())
                .applicable(conventional.applicableSnapshot())
                .bounds(conventional.bounds())
                .amortizationSchedule(conventional.amortizationSchedule());
            case MourabahaFinancingResult mourabaha -> builder
                .mourabahaSnapshot(mourabaha.mourabahaSnapshot())
                .mourabahaAmortizationSchedule(mourabaha.mourabahaAmortizationSchedule());
        }
        return builder.build();
    }
}
