package com.credit.bladi.service.workflow.product;

import com.credit.bladi.model.workflow.DemandSnapshot;

public interface WorkflowCodeResolver extends DemandTypeStrategy {
    String resolve(DemandSnapshot snapshot);
}
