package com.credit.bladi.service.security;

import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.service.borrower.IMortgageBorrowerService;
import com.credit.bladi.service.enterprise.IEnterpriseUserLinkService;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import org.springframework.stereotype.Component;

@Component
public class ClientDemandAccessGuard {
    private final IWorkflowEngineService workflowEngineService;

    private final IEnterpriseUserLinkService linkService;

    private final IMortgageBorrowerService borrowerService;

    public ClientDemandAccessGuard(IWorkflowEngineService workflowEngineService,
            IEnterpriseUserLinkService linkService, IMortgageBorrowerService borrowerService) {
        this.workflowEngineService = workflowEngineService;
        this.linkService = linkService;
        this.borrowerService = borrowerService;
    }

    public void assertApprovedForDemand(Long demandId) {
        DemandModel demand = workflowEngineService.getDemand(demandId);
        if (demand.demandType() == DemandType.MORTGAGE) {
            borrowerService.assertCurrentUserIsBorrower(demandId);
        } else {
            linkService.assertCurrentUserApprovedForEnterprise(demand.enterpriseIdOrNull());
        }
    }
}
