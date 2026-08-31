package com.credit.bladi.service.workflow.engine;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.enums.ClassificationOutcome;
import com.credit.bladi.model.enums.DemandStatus;
import com.credit.bladi.model.enums.StepDirection;
import com.credit.bladi.model.user.User;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.DemandSnapshot;
import com.credit.bladi.model.workflow.DemandStepModel;
import com.credit.bladi.model.workflow.DemandStepSummary;
import com.credit.bladi.model.workflow.StepClosureDetails;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IWorkflowEngineService {
    DemandModel createDemand(DemandSnapshot snapshot);

    DemandModel openDemandFromSimulation(DemandSnapshot snapshot);

    Long resolveDemandId(UUID publicId);

    DemandModel getDemand(Long demandId);

    Map<Long, DemandModel> getDemands(List<Long> demandIds);

    CreditPage<DemandModel> getAllDemands(int page, int size, UUID enterprisePublicId, String companyName,
                                         DemandStatus status, Money minAmount, Money maxAmount);

    DemandStepModel getCurrentStep(Long demandId);

    Map<Long, DemandStepSummary> getCurrentStepSummaries(List<Long> demandIds);

    DemandModel advance(Long demandId, StepClosureDetails details, String assigneeUsername);

    DemandModel classify(Long demandId, ClassificationOutcome outcome, UUID enterprisePublicId, String note,
            String assigneeUsername, String clientRib);

    DemandStepModel assign(Long demandId, String targetUsername);

    List<User> getEligibleAssignees(Long demandId, StepDirection direction);
}
