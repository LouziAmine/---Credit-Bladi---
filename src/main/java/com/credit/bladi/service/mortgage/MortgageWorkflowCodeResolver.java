package com.credit.bladi.service.mortgage;

import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.workflow.DemandSnapshot;
import com.credit.bladi.service.workflow.product.WorkflowCodeResolver;
import org.springframework.stereotype.Component;

@Component
public class MortgageWorkflowCodeResolver implements WorkflowCodeResolver {
    @Override
    public DemandType supportedType() {
        return DemandType.MORTGAGE;
    }

    @Override
    public String resolve(DemandSnapshot snapshot) {
        return "MORTGAGE_RETAIL";
    }
}
