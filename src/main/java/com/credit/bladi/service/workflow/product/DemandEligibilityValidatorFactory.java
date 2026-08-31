package com.credit.bladi.service.workflow.product;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DemandEligibilityValidatorFactory extends DemandTypeRegistry<DemandEligibilityValidator> {
    public DemandEligibilityValidatorFactory(List<DemandEligibilityValidator> validators) {
        super(validators, "eligibility validator");
    }
}
