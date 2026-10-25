package com.credit.bladi.service.investment;

import static com.credit.bladi.exception.InvestmentCreditException.InvestmentCreditExceptionType.PARAMETERS_NOT_FOUND;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.INSURANCE_ADI_REQUIRED;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.INSURANCE_DAMAGE_REQUIRED;

import com.credit.bladi.exception.InvestmentCreditException;
import com.credit.bladi.exception.WorkflowException;
import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.investment.InvestmentCreditParametersModel;
import com.credit.bladi.model.investment.InvestmentFinancialSnapshot;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.EnterpriseDemandDetail;
import com.credit.bladi.model.workflow.StepClosureDetails;
import com.credit.bladi.repository.investment.IInvestmentCreditParametersRepository;
import com.credit.bladi.service.workflow.product.DemandEligibilityValidator;
import org.springframework.stereotype.Component;

@Component
public class InvestmentDemandEligibilityValidator implements DemandEligibilityValidator {
    private final IInvestmentCreditParametersRepository parametersRepository;

    private final InvestmentCreditCalculator calculator;

    public InvestmentDemandEligibilityValidator(IInvestmentCreditParametersRepository parametersRepository,
                                                 InvestmentCreditCalculator calculator) {
        this.parametersRepository = parametersRepository;
        this.calculator = calculator;
    }

    @Override
    public DemandType supportedType() {
        return DemandType.ENTERPRISE;
    }

    @Override
    public void assertEligible(DemandModel demand, Money appliedAmount, Rate appliedRate, Integer appliedDuration) {
        EnterpriseDemandDetail detail = demand.enterpriseDetail();
        InvestmentCreditParametersModel parameters = parametersRepository
            .findActiveByEnterpriseType(detail.enterpriseType())
            .orElseThrow(() -> InvestmentCreditException.of(PARAMETERS_NOT_FOUND));

        if (detail.annualRevenue() != null) {
            parameters.assertWithinFinancingRatio(appliedAmount, detail.annualRevenue());
        }
        if (detail.guaranteesValue() != null) {
            parameters.assertGuaranteeCoverage(detail.guaranteesValue(), appliedAmount);
        }
        if (detail.existingAnnualDebtService() != null && detail.netAnnualResult() != null) {
            InvestmentFinancialSnapshot snapshot = calculator.computeAtRate(appliedAmount, appliedRate,
                appliedDuration, parameters);
            Money totalAnnualDebtService = detail.existingAnnualDebtService().add(snapshot.annualDebtService());
            parameters.assertWithinDebtServiceRatio(totalAnnualDebtService, detail.netAnnualResult());
        }
    }

    @Override
    public void assertInsuranceValidated(StepClosureDetails details, DemandModel demand) {
        if (!Boolean.TRUE.equals(details.insuranceAdiSubscribed())) {
            throw WorkflowException.of(INSURANCE_ADI_REQUIRED);
        }
        EnterpriseDemandDetail detail = demand.enterpriseDetail();
        boolean hasRealGuarantees = detail.guaranteesValue() != null
            && detail.guaranteesValue().amount().signum() > 0;
        if (hasRealGuarantees && !Boolean.TRUE.equals(details.insuranceDamageSubscribed())) {
            throw WorkflowException.of(INSURANCE_DAMAGE_REQUIRED);
        }
    }
}
