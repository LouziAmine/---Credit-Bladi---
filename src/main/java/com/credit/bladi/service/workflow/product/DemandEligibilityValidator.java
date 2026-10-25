package com.credit.bladi.service.workflow.product;

import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.StepClosureDetails;

public interface DemandEligibilityValidator extends DemandTypeStrategy {
    void assertEligible(DemandModel demand, Money appliedAmount, Rate appliedRate, Integer appliedDuration);

    default void assertInsuranceValidated(StepClosureDetails details, DemandModel demand) {
    }
}
