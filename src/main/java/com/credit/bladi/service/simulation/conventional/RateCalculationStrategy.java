package com.credit.bladi.service.simulation.conventional;

import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.simulation.CreditRateModel;
import com.credit.bladi.model.simulation.request.SimulationRequest;
import com.credit.bladi.model.vo.Rate;

public interface RateCalculationStrategy {

    CspCode getCspCode();

    Rate calculateRate(CreditRateModel creditRate);

    default void validateBamConstraints(SimulationRequest request, BamParametersModel bamParameters) {
        bamParameters.assertEligibleAge(request.getAge(), request.getDuration());
    }
}