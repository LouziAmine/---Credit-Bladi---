package com.credit.bladi.service.workflow.engine;

import com.credit.bladi.model.audit.AuditTarget;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.workflow.DemandAssignmentHistoryModel;
import com.credit.bladi.model.workflow.DemandCommentModel;
import com.credit.bladi.model.workflow.DemandHistoryEntryModel;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.DemandPipelineStepModel;
import com.credit.bladi.model.workflow.DemandStepModel;
import com.credit.bladi.model.workflow.WorkflowStepInfo;
import com.credit.bladi.repository.workflow.IDemandCommentRepository;
import com.credit.bladi.repository.workflow.IWorkflowRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DemandTimelineService implements IDemandTimelineService {
    private final IWorkflowRepository repository;

    private final IDemandCommentRepository commentRepository;

    private final IWorkflowEngineService workflowEngineService;

    private final IAuditService auditService;

    private final ICurrentActorProvider currentActorProvider;

    public DemandTimelineService(IWorkflowRepository repository, IDemandCommentRepository commentRepository,
            IWorkflowEngineService workflowEngineService, IAuditService auditService,
            ICurrentActorProvider currentActorProvider) {
        this.repository = repository;
        this.commentRepository = commentRepository;
        this.workflowEngineService = workflowEngineService;
        this.auditService = auditService;
        this.currentActorProvider = currentActorProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemandHistoryEntryModel> getHistory(Long demandId) {
        workflowEngineService.getDemand(demandId);
        return repository.findHistory(demandId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemandPipelineStepModel> getPipeline(Long demandId) {
        DemandModel demand = workflowEngineService.getDemand(demandId);
        List<WorkflowStepInfo> allSteps = repository.findAllSteps(demand.workflowId());

        Map<String, DemandHistoryEntryModel> historyByStepCode = repository.findHistory(demandId).stream()
            .collect(Collectors.toMap(DemandHistoryEntryModel::stepCode, Function.identity(),
                (firstVisit, latestVisit) -> latestVisit));

        return allSteps.stream()
            .map(step -> toPipelineStepModel(step, historyByStepCode.get(step.stepCode())))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemandAssignmentHistoryModel> getAssignmentHistory(Long demandId) {
        workflowEngineService.getDemand(demandId);
        return repository.findAssignmentHistory(demandId);
    }

    @Override
    @Transactional
    public DemandCommentModel addComment(Long demandId, String comment) {
        DemandModel demand = workflowEngineService.getDemand(demandId);
        DemandStepModel currentStep = workflowEngineService.getCurrentStep(demandId);
        String username = currentActorProvider.getCurrentUsername();
        DemandCommentModel saved = commentRepository.add(demandId, currentStep.stepCode(), currentStep.stepName(),
            username, comment);
        auditService.log(username, AuditAction.DEMAND_COMMENTED, AuditTarget.demand(demand.publicId()),
            "demandId=" + demandId + ", stepCode=" + currentStep.stepCode());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemandCommentModel> getComments(Long demandId) {
        workflowEngineService.getDemand(demandId);
        return commentRepository.findByDemandId(demandId);
    }

    private static DemandPipelineStepModel toPipelineStepModel(WorkflowStepInfo step, DemandHistoryEntryModel entry) {
        String status = entry == null ? "UPCOMING" : entry.exitedAt() != null ? "DONE" : "CURRENT";
        return new DemandPipelineStepModel(step.processCode(), step.processName(), step.stepCode(),
            step.stepName(), step.typicalRole(), step.requiredGradeCode(), step.requiredGradeName(),
            step.requiredTeamCode(), step.requiredTeamName(), step.globalOrder(), status,
            entry != null ? entry.enteredAt() : null, entry != null ? entry.exitedAt() : null);
    }
}
