package com.credit.bladi.service.mortgage;

import static com.credit.bladi.exception.MortgageCreditException.MortgageCreditExceptionType.BAM_PARAMETERS_NOT_FOUND;

import com.credit.bladi.exception.MortgageCreditException;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.MortgageDemandDetail;
import com.credit.bladi.repository.bam.IBamParametersRepository;
import com.credit.bladi.service.mortgage.strategy.FinancingStrategyFactory;
import com.credit.bladi.service.workflow.product.DemandEligibilityValidator;
import org.springframework.stereotype.Component;

@Component
public class MortgageDemandEligibilityValidator implements DemandEligibilityValidator {
    private final IBamParametersRepository bamParametersRepository;

    private final FinancingStrategyFactory financingStrategyFactory;

    public MortgageDemandEligibilityValidator(IBamParametersRepository bamParametersRepository,
                                               FinancingStrategyFactory financingStrategyFactory) {
        this.bamParametersRepository = bamParametersRepository;
        this.financingStrategyFactory = financingStrategyFactory;
    }

    @Override
    public DemandType supportedType() {
        return DemandType.MORTGAGE;
    }

    @Override
    public void assertEligible(DemandModel demand, Money appliedAmount, Rate appliedRate, Integer appliedDuration) {
        MortgageDemandDetail detail = demand.mortgageDetail();
        BamParametersModel bamParameters = bamParametersRepository.getActiveParameters()
            .orElseThrow(() -> MortgageCreditException.of(BAM_PARAMETERS_NOT_FOUND));
        financingStrategyFactory.getStrategy(detail.productType())
            .assertEligible(detail, appliedAmount, appliedRate, appliedDuration, bamParameters);
    }
}
