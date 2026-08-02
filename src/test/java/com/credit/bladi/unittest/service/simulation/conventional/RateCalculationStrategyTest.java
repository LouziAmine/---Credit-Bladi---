package com.credit.bladi.unittest.service.simulation.conventional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.credit.bladi.exception.BAMException;
import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.simulation.request.SimulationRequest;
import com.credit.bladi.service.simulation.conventional.csp.EmployeeStrategy;
import org.junit.jupiter.api.Test;

class RateCalculationStrategyTest {

    private final EmployeeStrategy strategy = new EmployeeStrategy();

    @Test
    void shouldNotThrowWhenAgeAtMaturityWithinLimit() {
        SimulationRequest request = SimulationRequest.getBuilder()
            .age(40)
            .duration(120)
            .cspCode(CspCode.SALA)
            .build();
        BamParametersModel bamParameters = BamParametersModel.getBuilder().maxAgeAtMaturity(70).build();

        assertDoesNotThrow(() -> strategy.validateBamConstraints(request, bamParameters));
    }

    @Test
    void shouldThrowWhenAgeAtMaturityExceedsLimit() {
        SimulationRequest request = SimulationRequest.getBuilder()
            .age(60)
            .duration(240)
            .cspCode(CspCode.SALA)
            .build();
        BamParametersModel bamParameters = BamParametersModel.getBuilder().maxAgeAtMaturity(70).build();

        assertThrows(BAMException.class, () -> strategy.validateBamConstraints(request, bamParameters));
    }
}
