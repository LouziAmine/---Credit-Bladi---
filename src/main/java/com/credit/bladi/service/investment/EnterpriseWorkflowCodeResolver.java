package com.credit.bladi.service.investment;

import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.workflow.DemandSnapshot;
import com.credit.bladi.model.workflow.EnterpriseDemandSnapshotDetail;
import com.credit.bladi.service.workflow.product.WorkflowCodeResolver;
import org.springframework.stereotype.Component;

@Component
public class EnterpriseWorkflowCodeResolver implements WorkflowCodeResolver {
    @Override
    public DemandType supportedType() {
        return DemandType.ENTERPRISE;
    }

    @Override
    public String resolve(DemandSnapshot snapshot) {
        if (!(snapshot.detail() instanceof EnterpriseDemandSnapshotDetail detail)) {
            throw new IllegalStateException("Enterprise workflow resolver received a " + snapshot.demandType()
                + " demand");
        }
        EnterpriseType enterpriseType = detail.enterpriseType();
        return switch (enterpriseType) {
            case TPE, AUTO_ENTREPRENEUR, PME -> "PME_INVESTMENT";
            case ETI, GE -> "GE_INVESTMENT";
        };
    }
}
