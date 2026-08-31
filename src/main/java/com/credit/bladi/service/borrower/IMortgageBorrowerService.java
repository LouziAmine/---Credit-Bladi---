package com.credit.bladi.service.borrower;

import com.credit.bladi.model.workflow.DemandModel;
import java.util.List;

public interface IMortgageBorrowerService {
    void resolveBorrowerForClassification(DemandModel demand, String actorUsername);

    void assertCurrentUserIsBorrower(Long demandId);

    List<Long> getMyDemandIds();
}
