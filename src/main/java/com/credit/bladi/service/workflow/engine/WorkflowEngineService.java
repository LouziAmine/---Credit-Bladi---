package com.credit.bladi.service.workflow.engine;

import static com.credit.bladi.exception.EnterpriseException.EnterpriseExceptionType.ENTERPRISE_NOT_FOUND;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.DEMAND_NOT_FOUND;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.NO_NEXT_STEP;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.NO_PREVIOUS_STEP;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.TERMS_FROZEN_AFTER_DECISION;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.WORKFLOW_NOT_CONFIGURED;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.exception.EnterpriseException;
import com.credit.bladi.exception.WorkflowException;
import com.credit.bladi.model.audit.AuditChanges;
import com.credit.bladi.model.audit.AuditTarget;
import com.credit.bladi.model.client.EnterpriseModel;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.AuditEntityType;
import com.credit.bladi.model.enums.ClassificationOutcome;
import com.credit.bladi.model.enums.DemandStatus;
import com.credit.bladi.model.enums.StepDirection;
import com.credit.bladi.model.user.User;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rib;
import com.credit.bladi.model.workflow.Demand;
import com.credit.bladi.model.workflow.DemandHistoryEntryModel;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.DemandSnapshot;
import com.credit.bladi.model.workflow.DemandStepModel;
import com.credit.bladi.model.workflow.DemandStepSummary;
import com.credit.bladi.model.workflow.StepClosureDetails;
import com.credit.bladi.model.workflow.StepDecisions;
import com.credit.bladi.model.workflow.WorkflowStepInfo;
import com.credit.bladi.model.workflow.event.CommitteeDecisionEvent;
import com.credit.bladi.model.workflow.event.DemandCompletedEvent;
import com.credit.bladi.repository.client.IEnterpriseRepository;
import com.credit.bladi.repository.workflow.IWorkflowRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.borrower.IMortgageBorrowerService;
import com.credit.bladi.service.enterprise.IEnterpriseLinkClassificationResolver;
import com.credit.bladi.service.security.ICurrentActorProvider;
import com.credit.bladi.service.workflow.access.StepAssignmentPolicy;
import com.credit.bladi.service.workflow.product.DemandEligibilityValidator;
import com.credit.bladi.service.workflow.product.DemandEligibilityValidatorFactory;
import com.credit.bladi.service.workflow.product.WorkflowCodeResolverFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkflowEngineService implements IWorkflowEngineService {
    private static final Logger LOGGER = LogManager.getLogger(WorkflowEngineService.class);

    private final IWorkflowRepository repository;

    private final IEnterpriseRepository enterpriseRepository;

    private final IAuditService auditService;

    private final ICurrentActorProvider currentActorProvider;

    private final DemandEligibilityValidatorFactory eligibilityValidatorFactory;

    private final IEnterpriseLinkClassificationResolver enterpriseUserLinkService;

    private final ApplicationEventPublisher eventPublisher;

    private final WorkflowCodeResolverFactory workflowCodeResolverFactory;

    private final StepAssignmentPolicy assignmentPolicy;

    private final WorkflowNotifications notifications;

    private final IMortgageBorrowerService borrowerService;

    public WorkflowEngineService(IWorkflowRepository repository, IEnterpriseRepository enterpriseRepository,
                                 IAuditService auditService, ICurrentActorProvider currentActorProvider,
                                 DemandEligibilityValidatorFactory eligibilityValidatorFactory,
                                 IEnterpriseLinkClassificationResolver enterpriseUserLinkService,
                                 ApplicationEventPublisher eventPublisher,
                                 WorkflowCodeResolverFactory workflowCodeResolverFactory,
                                 StepAssignmentPolicy assignmentPolicy, WorkflowNotifications notifications,
                                 IMortgageBorrowerService borrowerService) {
        this.repository = repository;
        this.enterpriseRepository = enterpriseRepository;
        this.auditService = auditService;
        this.currentActorProvider = currentActorProvider;
        this.eligibilityValidatorFactory = eligibilityValidatorFactory;
        this.enterpriseUserLinkService = enterpriseUserLinkService;
        this.eventPublisher = eventPublisher;
        this.workflowCodeResolverFactory = workflowCodeResolverFactory;
        this.assignmentPolicy = assignmentPolicy;
        this.notifications = notifications;
        this.borrowerService = borrowerService;
    }

    @Override
    @Transactional
    public DemandModel createDemand(DemandSnapshot snapshot) {
        return openNewDemand(snapshot, resolveWorkflowId(snapshot));
    }

    @Override
    @Transactional
    public DemandModel openDemandFromSimulation(DemandSnapshot snapshot) {
        Long workflowId = resolveWorkflowId(snapshot);
        return findUnclassifiedDuplicate(snapshot, workflowId)
            .orElseGet(() -> openNewDemand(snapshot, workflowId));
    }

    private Long resolveWorkflowId(DemandSnapshot snapshot) {
        String workflowCode = workflowCodeResolverFactory.get(snapshot.demandType()).resolve(snapshot);
        return repository.findActiveWorkflowIdByCode(workflowCode)
            .orElseThrow(() -> WorkflowException.of(WORKFLOW_NOT_CONFIGURED));
    }

    private DemandModel openNewDemand(DemandSnapshot snapshot, Long workflowId) {
        WorkflowStepInfo firstStep = repository.findFirstStep(workflowId)
            .orElseThrow(() -> WorkflowException.of(WORKFLOW_NOT_CONFIGURED));

        DemandModel demand = repository.createDemand(snapshot, workflowId, firstStep);
        LOGGER.info("createDemand - reference={}, workflowId={}, firstStep={}",
            demand.reference(), workflowId, firstStep.stepCode());
        notifications.awaitingAssignment(firstStep, demand);
        return demand;
    }

    private Optional<DemandModel> findUnclassifiedDuplicate(DemandSnapshot snapshot, Long workflowId) {
        if (snapshot.contactEmail() == null || snapshot.contactEmail().isBlank() || snapshot.contactName() == null) {
            return Optional.empty();
        }
        return repository.findUnclassifiedDemandId(snapshot.demandType(), snapshot.contactEmail(),
                snapshot.contactName())
            .flatMap(repository::findForUpdate)
            .filter(demand -> demand.state().status() == DemandStatus.IN_PROGRESS
                && workflowId.equals(demand.state().workflowId()))
            .filter(demand -> repository.findCurrentStepInfo(demand.id())
                .map(WorkflowStepInfo::requiresClassification)
                .orElse(false))
            .map(demand -> refreshWithSimulation(demand, snapshot));
    }

    private DemandModel refreshWithSimulation(Demand demand, DemandSnapshot snapshot) {
        DemandModel existing = demand.state();
        demand.refreshFromSimulation(snapshot);
        repository.save(demand);
        AuditChanges termChanges = AuditChanges.of()
            .field("amount", existing.amount(), snapshot.amount())
            .field("duration", existing.duration(), snapshot.duration())
            .field("rate", existing.applicableRate(), snapshot.applicableRate());
        if (!termChanges.isEmpty()) {
            auditService.log(currentActorProvider.getCurrentUsername(), AuditAction.DEMAND_REFRESHED_BY_SIMULATION,
                AuditTarget.demand(existing.publicId()),
                "demandId=" + existing.id() + ", reference=" + existing.reference() + ", " + termChanges);
        }
        LOGGER.info("openDemandFromSimulation - reference={} refreshed instead of opening a duplicate",
            existing.reference());
        return getDemand(existing.id());
    }

    @Override
    @Transactional(readOnly = true)
    public Long resolveDemandId(UUID publicId) {
        return repository.findDemandIdByPublicId(publicId)
            .orElseThrow(() -> WorkflowException.of(DEMAND_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public DemandModel getDemand(Long demandId) {
        return repository.findDemandById(demandId)
            .orElseThrow(() -> WorkflowException.of(DEMAND_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, DemandModel> getDemands(List<Long> demandIds) {
        return repository.findAllDemandsByIds(demandIds).stream()
            .collect(Collectors.toMap(DemandModel::id, Function.identity()));
    }

    @Override
    @Transactional(readOnly = true)
    public CreditPage<DemandModel> getAllDemands(int page, int size, UUID enterprisePublicId, String companyName,
                                                 DemandStatus status, Money minAmount, Money maxAmount) {
        if (enterprisePublicId == null) {
            return repository.findAllDemands(page, size, null, companyName, status, minAmount, maxAmount);
        }
        Optional<Long> enterpriseId = enterpriseRepository.findIdByPublicId(enterprisePublicId);
        if (enterpriseId.isEmpty()) {
            return CreditPage.empty(page, size);
        }
        return repository.findAllDemands(page, size, enterpriseId.get(), companyName, status, minAmount, maxAmount);
    }

    @Override
    @Transactional(readOnly = true)
    public DemandStepModel getCurrentStep(Long demandId) {
        return repository.findCurrentStep(demandId).orElseGet(() -> {
            getDemand(demandId);
            return lastHistoryEntryAsCurrentStep(demandId);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, DemandStepSummary> getCurrentStepSummaries(List<Long> demandIds) {
        return repository.findCurrentStepSummaries(demandIds);
    }

    @Override
    @Transactional
    public DemandModel advance(Long demandId, StepClosureDetails details, String assigneeUsername) {
        Demand demand = requireDemandForUpdate(demandId);
        demand.assertReadyToAdvance();
        WorkflowStepInfo current = demand.currentStep();
        String username = currentActorProvider.getCurrentUsername();
        assignmentPolicy.assertActorEligibleForStep(username, current, demand.currentAssignee());
        demand.assertDirectionAllowed(details);
        if (!details.isReturnForCompletion()) {
            assignmentPolicy.assertFourEyes(demandId, current, username);
        }
        if (details.overridesTerms() && isPastCommitteeDecision(demand.state().workflowId(), current)) {
            throw WorkflowException.of(TERMS_FROZEN_AFTER_DECISION);
        }

        StepDecisions decisions = demand.decide(details, username);
        if (decisions.rate().appliedRate() != null) {
            assignmentPolicy.assertWithinLegalRate(decisions.rate());
        }
        if (details.isRefusal()) {
            return refuse(demand, details, decisions, username);
        }
        assertTermsAcceptable(demand.state(), details, decisions, current);
        return moveOn(demand, details, decisions, assigneeUsername, username);
    }

    private boolean isPastCommitteeDecision(Long workflowId, WorkflowStepInfo current) {
        return repository.findAllSteps(workflowId).stream()
            .anyMatch(step -> step.requiresFourEyes() && step.globalOrder() < current.globalOrder());
    }

    private void assertTermsAcceptable(DemandModel demand, StepClosureDetails details, StepDecisions decisions,
                                       WorkflowStepInfo current) {
        DemandEligibilityValidator validator = eligibilityValidatorFactory.get(demand.demandType());
        if (details.overridesTerms()) {
            validator.assertEligible(demand, decisions.amount().appliedAmount(), decisions.rate().appliedRate(),
                decisions.duration().appliedDuration());
        }
        if (!details.isReturnForCompletion() && current.requiresInsuranceConfirmation()) {
            validator.assertInsuranceValidated(details, demand);
        }
    }

    private DemandModel moveOn(Demand demand, StepClosureDetails details, StepDecisions decisions,
                               String assigneeUsername, String username) {
        DemandModel state = demand.state();
        WorkflowStepInfo current = demand.currentStep();
        boolean returnedForCompletion = details.isReturnForCompletion();
        WorkflowStepInfo destination = returnedForCompletion
            ? repository.findPreviousStep(state.workflowId(), current.globalOrder())
                .orElseThrow(() -> WorkflowException.of(NO_PREVIOUS_STEP))
            : repository.findNextStep(state.workflowId(), current.globalOrder()).orElse(null);
        if (destination != null) {
            assignmentPolicy.assertEligibleNextAssignee(username, assigneeUsername, destination);
        }

        demand.advance(details, decisions, destination, assigneeUsername, username);
        repository.save(demand);
        if (!returnedForCompletion && current.requiresFourEyes()) {
            eventPublisher.publishEvent(new CommitteeDecisionEvent(state.id(), username));
        }
        announceArrival(state, destination, returnedForCompletion, assigneeUsername, username);

        AuditAction action = returnedForCompletion ? AuditAction.DEMAND_RETURNED : AuditAction.DEMAND_ADVANCED;
        auditService.log(username, action, AuditTarget.demand(state.publicId()),
            "demandId=" + state.id() + ", reference=" + state.reference() + ", fromStep=" + current.stepCode()
                + ", decisionOutcome=" + details.decisionOutcome() + ", " + decisions.termChanges());
        return getDemand(state.id());
    }

    private void announceArrival(DemandModel demand, WorkflowStepInfo destination, boolean returnedForCompletion,
                                 String assigneeUsername, String username) {
        if (destination == null) {
            notifications.completed(demand);
            eventPublisher.publishEvent(new DemandCompletedEvent(demand.id(), username));
            return;
        }
        notifications.assigned(assigneeUsername, username, demand, destination.stepName());
        if (!returnedForCompletion && destination.notifyContactOnEnterType() != null) {
            notifications.stepEntered(destination.notifyContactOnEnterType(), demand);
        }
    }

    @Override
    @Transactional
    public DemandModel classify(Long demandId, ClassificationOutcome outcome, UUID enterprisePublicId, String note,
                                 String assigneeUsername, String clientRib) {
        Demand demand = requireDemandForUpdate(demandId);
        demand.assertReadyToClassify();
        String username = currentActorProvider.getCurrentUsername();
        assignmentPolicy.assertActorEligibleForStep(username, demand.currentStep(), demand.currentAssignee());

        if (outcome != ClassificationOutcome.REAL) {
            demand.classifyAsNotReal(outcome, note, username);
            repository.save(demand);
            auditService.log(username, AuditAction.DEMAND_CLASSIFIED, AuditTarget.demand(demand.state().publicId()),
                "demandId=" + demandId + ", reference=" + demand.state().reference() + ", outcome=" + outcome);
            return getDemand(demandId);
        }
        return classifyAsReal(demand, enterprisePublicId, note, assigneeUsername, clientRib, username);
    }

    private DemandModel classifyAsReal(Demand demand, UUID enterprisePublicId, String note, String assigneeUsername,
                                       String clientRib, String username) {
        DemandModel state = demand.state();
        demand.assertEnterpriseGivenForRealClassification(enterprisePublicId != null);
        Long enterpriseId = demand.isEnterpriseDemand()
            ? enterpriseRepository.findIdByPublicId(enterprisePublicId)
                .orElseThrow(() -> EnterpriseException.of(ENTERPRISE_NOT_FOUND))
            : null;
        WorkflowStepInfo destination = repository.findNextStep(state.workflowId(), demand.currentStep().globalOrder())
            .orElseThrow(() -> WorkflowException.of(NO_NEXT_STEP));
        assignmentPolicy.assertEligibleNextAssignee(username, assigneeUsername, destination);

        demand.classifyAsReal(note, username, destination, assigneeUsername, enterpriseId);
        repository.save(demand);
        if (enterpriseId != null) {
            updateClientRib(state, enterpriseId, enterprisePublicId, clientRib, username);
            enterpriseUserLinkService.resolveClientForClassification(enterpriseId, state.contactEmail(),
                state.contactLanguage(), state.id(), username);
        } else {
            borrowerService.resolveBorrowerForClassification(state, username);
        }
        notifications.assigned(assigneeUsername, username, state, destination.stepName());
        notifications.enteredPipeline(state, enterpriseId);

        auditService.log(username, AuditAction.DEMAND_CLASSIFIED, AuditTarget.demand(state.publicId()),
            "demandId=" + state.id() + ", reference=" + state.reference() + ", outcome=" + ClassificationOutcome.REAL
                + ", enterpriseId=" + enterpriseId);
        return getDemand(state.id());
    }

    private void updateClientRib(DemandModel demand, Long enterpriseId, UUID enterprisePublicId, String clientRib,
                                 String username) {
        if (clientRib == null || clientRib.isBlank()) {
            return;
        }
        Rib newRib = Rib.of(clientRib);
        Rib previousRib = enterpriseRepository.findById(enterpriseId).map(EnterpriseModel::rib).orElse(null);
        AuditChanges ribChange = AuditChanges.of()
            .field("rib", previousRib != null ? previousRib.masked() : null, newRib.masked());
        enterpriseRepository.updateRib(enterpriseId, newRib);
        auditService.log(username, AuditAction.ENTERPRISE_RIB_UPDATED,
            new AuditTarget(AuditEntityType.ENTERPRISE, enterprisePublicId),
            "demandId=" + demand.id() + ", enterpriseId=" + enterpriseId + ", " + ribChange);
    }

    @Override
    @Transactional
    public DemandStepModel assign(Long demandId, String targetUsername) {
        Demand demand = requireDemandForUpdate(demandId);
        demand.assertOpen();
        WorkflowStepInfo current = demand.currentStep();

        String assignerUsername = currentActorProvider.getCurrentUsername();
        User target = assignmentPolicy.assertCanReassign(demandId, assignerUsername, targetUsername, current,
            demand.currentAssignee());

        demand.reassign(targetUsername, assignerUsername);
        repository.save(demand);
        notifications.assigned(target, assignerUsername, demand.state(), current.stepName());
        auditService.log(assignerUsername, AuditAction.DEMAND_ASSIGNED, AuditTarget.demand(demand.state().publicId()),
            "demandId=" + demandId + ", reference=" + demand.state().reference() + ", stepCode=" + current.stepCode()
                + ", assignee=" + targetUsername);

        return getCurrentStep(demandId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getEligibleAssignees(Long demandId, StepDirection direction) {
        DemandModel demand = getDemand(demandId);
        if (demand.status() != DemandStatus.IN_PROGRESS) {
            return List.of();
        }
        WorkflowStepInfo current = requireCurrentStepInfo(demandId);
        WorkflowStepInfo target = switch (direction) {
            case CURRENT -> current;
            case NEXT -> repository.findNextStep(demand.workflowId(), current.globalOrder()).orElse(null);
            case PREVIOUS -> repository.findPreviousStep(demand.workflowId(), current.globalOrder()).orElse(null);
        };
        return assignmentPolicy.eligibleAssignees(currentActorProvider.getCurrentUsername(), target);
    }

    private DemandModel refuse(Demand demand, StepClosureDetails details, StepDecisions decisions, String username) {
        DemandModel state = demand.state();
        demand.refuse(details, decisions);
        repository.save(demand);
        notifications.rejected(state);
        auditService.log(username, AuditAction.DEMAND_REJECTED, AuditTarget.demand(state.publicId()),
            "demandId=" + state.id() + ", reference=" + state.reference() + ", atStep="
                + demand.currentStep().stepCode() + ", decisionOutcome=" + details.decisionOutcome() + ", "
                + decisions.termChanges());
        return getDemand(state.id());
    }

    private Demand requireDemandForUpdate(Long demandId) {
        return repository.findForUpdate(demandId).orElseThrow(() -> WorkflowException.of(DEMAND_NOT_FOUND));
    }

    private WorkflowStepInfo requireCurrentStepInfo(Long demandId) {
        return repository.findCurrentStepInfo(demandId)
            .orElseThrow(() -> WorkflowException.of(DEMAND_NOT_FOUND));
    }

    private DemandStepModel lastHistoryEntryAsCurrentStep(Long demandId) {
        List<DemandHistoryEntryModel> history = repository.findHistory(demandId);
        if (history.isEmpty()) {
            throw new IllegalStateException("Demand " + demandId + " has neither an open step nor any step history");
        }
        DemandHistoryEntryModel last = history.get(history.size() - 1);
        return new DemandStepModel(last.processCode(), last.processName(), last.stepCode(), last.stepName(),
            last.typicalRole(), last.requiredGradeCode(), last.requiredGradeName(),
            last.requiredTeamCode(), last.requiredTeamName(), last.assignedUsername(), last.assignedBy(),
            last.enteredAt());
    }
}
