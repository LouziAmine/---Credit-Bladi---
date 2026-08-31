package com.credit.bladi.service.investment;

import static com.credit.bladi.exception.InvestmentCreditException.InvestmentCreditExceptionType.DEMAND_RATE_NOT_SET;
import static com.credit.bladi.exception.InvestmentCreditException.InvestmentCreditExceptionType.PARAMETERS_NOT_FOUND;

import com.credit.bladi.exception.InvestmentCreditException;
import com.credit.bladi.exception.RegulatoryLimitException;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.investment.InvestmentCreditParametersModel;
import com.credit.bladi.model.investment.InvestmentFinancialSnapshot;
import com.credit.bladi.model.investment.request.InvestmentCreditRequest;
import com.credit.bladi.model.investment.response.InvestmentCreditResponse;
import com.credit.bladi.model.parameters.RegulatoryLimits;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.DemandSnapshot;
import com.credit.bladi.model.workflow.EnterpriseDemandSnapshotDetail;
import com.credit.bladi.repository.investment.IInvestmentCreditParametersRepository;
import com.credit.bladi.repository.pricing.IAgreedPricingRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.request.IRequestContext;
import com.credit.bladi.service.security.ICurrentActorProvider;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvestmentCreditService implements IInvestmentCreditService {
    private static final Logger LOGGER = LogManager.getLogger(InvestmentCreditService.class);

    private final IInvestmentCreditParametersRepository parametersRepository;

    private final InvestmentCreditCalculator calculator;

    private final IWorkflowEngineService workflowEngineService;

    private final IAuditService auditService;

    private final ICurrentActorProvider currentActorProvider;

    private final IRequestContext requestContext;

    private final IAgreedPricingRepository agreedPricingRepository;

    private final RegulatoryLimits regulatoryLimits;

    public InvestmentCreditService(IInvestmentCreditParametersRepository parametersRepository,
                                   InvestmentCreditCalculator calculator,
                                   IWorkflowEngineService workflowEngineService,
                                   IAuditService auditService,
                                   ICurrentActorProvider currentActorProvider,
                                   IRequestContext requestContext,
                                   IAgreedPricingRepository agreedPricingRepository,
                                   RegulatoryLimits regulatoryLimits) {
        this.parametersRepository = parametersRepository;
        this.calculator = calculator;
        this.workflowEngineService = workflowEngineService;
        this.auditService = auditService;
        this.currentActorProvider = currentActorProvider;
        this.requestContext = requestContext;
        this.agreedPricingRepository = agreedPricingRepository;
        this.regulatoryLimits = regulatoryLimits;
    }

    @Override
    @Transactional
    public InvestmentCreditResponse simulate(InvestmentCreditRequest request) {
        EnterpriseType enterpriseType = request.getEnterpriseType();
        Money amount = request.getAmount();
        int duration = request.getDuration();

        LOGGER.info("Simulate - companyName={}, enterpriseType={}, amount={}, duration={}",
            request.getCompanyName(), enterpriseType, amount, duration);

        InvestmentCreditParametersModel parameters = parametersRepository.findActiveByEnterpriseType(enterpriseType)
            .orElseThrow(() -> InvestmentCreditException.of(PARAMETERS_NOT_FOUND));

        try {
            validateEligibility(request, parameters);
        } catch (InvestmentCreditException rejection) {
            auditRejectionAndRethrow(rejection, enterpriseType, amount, duration);
        }

        Rate applicableRate = parameters.calculateRate();
        InvestmentFinancialSnapshot snapshot = calculator.computeAtRate(amount, applicableRate, duration, parameters);
        try {
            regulatoryLimits.assertSimulationWithinMaxRate(applicableRate, snapshot.taeg());
        } catch (RegulatoryLimitException rejection) {
            auditService.log(currentActorProvider.getCurrentUsername(), AuditAction.ENTERPRISE_SIMULATION_REJECTED,
                "reason=" + rejection.getType() + ", enterpriseType=" + enterpriseType
                    + ", amount=" + amount + ", duration=" + duration);
            throw rejection;
        }

        Money totalAnnualDebtService = request.getExistingAnnualDebtService().add(snapshot.annualDebtService());
        try {
            parameters.assertWithinDebtServiceRatio(totalAnnualDebtService, request.getNetAnnualResult());
        } catch (InvestmentCreditException rejection) {
            auditRejectionAndRethrow(rejection, enterpriseType, amount, duration);
        }

        DemandModel demand = workflowEngineService.openDemandFromSimulation(
            new DemandSnapshot(DemandType.ENTERPRISE, request.getCompanyName(), amount, duration, applicableRate,
                request.getEmail(), request.getPhone(), requestContext.getLanguage(),
                new EnterpriseDemandSnapshotDetail(enterpriseType, request.getAnnualRevenue(),
                    request.getExistingAnnualDebtService(), request.getNetAnnualResult(),
                    request.getGuaranteesValue(), request.getSector(), request.getCompanyAgeYears())));

        auditService.log(currentActorProvider.getCurrentUsername(), AuditAction.ENTERPRISE_SIMULATION,
            "enterpriseType=" + enterpriseType + ", amount=" + amount + ", duration=" + duration
                + ", applicableRate=" + applicableRate + ", taeg=" + snapshot.taeg() + "%"
                + ", demandReference=" + demand.reference());

        return new InvestmentCreditResponse(request.getCompanyName(), enterpriseType, request.getSector(),
            amount, duration, snapshot, demand.reference());
    }

    @Override
    @Transactional(readOnly = true)
    public InvestmentFinancialSnapshot getAmortizationSchedule(Long demandId, Money amountOverride,
            Rate rateOverride, Integer durationOverride) {
        DemandModel demand = workflowEngineService.getDemand(demandId);
        Rate rate = rateOverride != null ? rateOverride : demand.applicableRate();
        if (rate == null) {
            throw InvestmentCreditException.of(DEMAND_RATE_NOT_SET);
        }
        Money amount = amountOverride != null ? amountOverride : demand.amount();
        int duration = durationOverride != null ? durationOverride : demand.duration();
        InvestmentCreditParametersModel active = parametersRepository
            .findActiveByEnterpriseType(demand.enterpriseDetail().enterpriseType())
            .orElseThrow(() -> InvestmentCreditException.of(PARAMETERS_NOT_FOUND));
        InvestmentCreditParametersModel parameters = agreedPricingRepository.findByDemandId(demandId)
            .map(agreed -> agreed.applyTo(active))
            .orElse(active);
        return calculator.computeAtRate(amount, rate, duration, parameters);
    }

    private void auditRejectionAndRethrow(InvestmentCreditException rejection, EnterpriseType enterpriseType,
            Money amount, int duration) {
        auditService.log(currentActorProvider.getCurrentUsername(), AuditAction.ENTERPRISE_SIMULATION_REJECTED,
            "reason=" + rejection.getType() + ", enterpriseType=" + enterpriseType
                + ", amount=" + amount + ", duration=" + duration);
        throw rejection;
    }

    private void validateEligibility(InvestmentCreditRequest request, InvestmentCreditParametersModel parameters) {
        parameters.assertMinCompanyAge(request.getCompanyAgeYears());
        parameters.assertWithinFinancingRatio(request.getAmount(), request.getAnnualRevenue());
        parameters.assertGuaranteeCoverage(request.getGuaranteesValue(), request.getAmount());
    }
}
