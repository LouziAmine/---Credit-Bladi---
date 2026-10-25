package com.credit.bladi.repository.workflow;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.enums.DemandStatus;
import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.workflow.Demand;
import com.credit.bladi.model.workflow.DemandAssignmentHistoryModel;
import com.credit.bladi.model.workflow.DemandHistoryEntryModel;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.DemandSnapshot;
import com.credit.bladi.model.workflow.DemandStepModel;
import com.credit.bladi.model.workflow.DemandStepSummary;
import com.credit.bladi.model.workflow.WorkflowStepInfo;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface IWorkflowRepository {
    Optional<Long> findActiveWorkflowIdByCode(String code);

    Optional<Long> findDemandIdByPublicId(UUID publicId);

    Optional<WorkflowStepInfo> findFirstStep(Long workflowId);

    Optional<WorkflowStepInfo> findNextStep(Long workflowId, Integer afterGlobalOrder);

    Optional<WorkflowStepInfo> findPreviousStep(Long workflowId, Integer beforeGlobalOrder);

    List<WorkflowStepInfo> findAllSteps(Long workflowId);

    DemandModel createDemand(DemandSnapshot snapshot, Long workflowId, WorkflowStepInfo firstStep);

    Optional<DemandModel> findDemandById(Long demandId);

    Optional<Demand> findForUpdate(Long demandId);

    void save(Demand demand);

    Optional<Long> findUnclassifiedDemandId(DemandType demandType, String contactEmail, String contactName);

    List<DemandModel> findAllDemandsByIds(List<Long> demandIds);

    CreditPage<DemandModel> findAllDemands(int page, int size, Long enterpriseId, String companyName,
                                           DemandStatus status, Money minAmount, Money maxAmount);

    Optional<WorkflowStepInfo> findCurrentStepInfo(Long demandId);

    Optional<DemandStepModel> findCurrentStep(Long demandId);

    Map<Long, DemandStepSummary> findCurrentStepSummaries(List<Long> demandIds);

    List<DemandHistoryEntryModel> findHistory(Long demandId);

    boolean hasClosedAnyStep(Long demandId, String username);

    List<DemandAssignmentHistoryModel> findAssignmentHistory(Long demandId);
}
