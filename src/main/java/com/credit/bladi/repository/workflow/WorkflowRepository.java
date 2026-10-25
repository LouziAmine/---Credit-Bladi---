package com.credit.bladi.repository.workflow;

import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.DEMAND_NOT_FOUND;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.STEP_NOT_FOUND;
import static com.credit.bladi.model.vo.ValueObjectMappers.toBigDecimal;
import static com.credit.bladi.model.vo.ValueObjectMappers.toRate;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.workflow.DemandAssignmentHistoryDatasource;
import com.credit.bladi.datasource.workflow.DemandCurrentStepProjection;
import com.credit.bladi.datasource.workflow.DemandDatasource;
import com.credit.bladi.datasource.workflow.DemandSpecifications;
import com.credit.bladi.datasource.workflow.WorkflowDatasource;
import com.credit.bladi.datasource.workflow.WorkflowProcessStepDatasource;
import com.credit.bladi.datasource.workflow.WorkflowProcessStepDemandDatasource;
import com.credit.bladi.datasource.workflow.WorkflowProcessStepInfoProjection;
import com.credit.bladi.datasource.workflow.entity.DemandAssignmentHistoryEntity;
import com.credit.bladi.datasource.workflow.entity.DemandEntity;
import com.credit.bladi.datasource.workflow.entity.WorkflowEntity;
import com.credit.bladi.datasource.workflow.entity.WorkflowProcessStepDemandEntity;
import com.credit.bladi.exception.WorkflowException;
import com.credit.bladi.model.enums.DemandStatus;
import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.enums.NotificationType;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.workflow.AmountDecision;
import com.credit.bladi.model.workflow.Demand;
import com.credit.bladi.model.workflow.DemandAssignmentHistoryModel;
import com.credit.bladi.model.workflow.DemandChange;
import com.credit.bladi.model.workflow.DemandDetail;
import com.credit.bladi.model.workflow.DemandHistoryEntryModel;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.DemandSnapshot;
import com.credit.bladi.model.workflow.DemandStepModel;
import com.credit.bladi.model.workflow.DemandStepSummary;
import com.credit.bladi.model.workflow.DurationDecision;
import com.credit.bladi.model.workflow.RateDecision;
import com.credit.bladi.model.workflow.StepClosureDetails;
import com.credit.bladi.model.workflow.WorkflowStepInfo;
import com.credit.bladi.repository.workflow.detail.DemandDetailStoreFactory;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class WorkflowRepository implements IWorkflowRepository {
    private final WorkflowDatasource workflowDatasource;

    private final WorkflowProcessStepDatasource stepDatasource;

    private final DemandDatasource demandDatasource;

    private final DemandDetailStoreFactory detailStoreFactory;

    private final WorkflowProcessStepDemandDatasource stepDemandDatasource;

    private final DemandAssignmentHistoryDatasource assignmentHistoryDatasource;

    private final Clock clock;

    public WorkflowRepository(WorkflowDatasource workflowDatasource, WorkflowProcessStepDatasource stepDatasource,
                              DemandDatasource demandDatasource,
                              DemandDetailStoreFactory detailStoreFactory,
                              WorkflowProcessStepDemandDatasource stepDemandDatasource,
                              DemandAssignmentHistoryDatasource assignmentHistoryDatasource,
                              Clock clock) {
        this.clock = clock;
        this.workflowDatasource = workflowDatasource;
        this.stepDatasource = stepDatasource;
        this.demandDatasource = demandDatasource;
        this.detailStoreFactory = detailStoreFactory;
        this.stepDemandDatasource = stepDemandDatasource;
        this.assignmentHistoryDatasource = assignmentHistoryDatasource;
    }

    @Override
    public Optional<Long> findActiveWorkflowIdByCode(String code) {
        return workflowDatasource.findActiveByCode(code).map(WorkflowEntity::getId);
    }

    @Override
    public Optional<Long> findDemandIdByPublicId(UUID publicId) {
        return demandDatasource.findByPublicId(publicId).map(DemandEntity::getId);
    }

    @Override
    public Optional<WorkflowStepInfo> findFirstStep(Long workflowId) {
        return stepDatasource.findFirstStep(workflowId).map(WorkflowRepository::toStepInfo);
    }

    @Override
    public Optional<WorkflowStepInfo> findNextStep(Long workflowId, Integer afterGlobalOrder) {
        return stepDatasource.findNextStep(workflowId, afterGlobalOrder).map(WorkflowRepository::toStepInfo);
    }

    @Override
    public Optional<WorkflowStepInfo> findPreviousStep(Long workflowId, Integer beforeGlobalOrder) {
        return stepDatasource.findPreviousStep(workflowId, beforeGlobalOrder).map(WorkflowRepository::toStepInfo);
    }

    @Override
    public List<WorkflowStepInfo> findAllSteps(Long workflowId) {
        return stepDatasource.findAllSteps(workflowId).stream().map(WorkflowRepository::toStepInfo).toList();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public DemandModel createDemand(DemandSnapshot snapshot, Long workflowId, WorkflowStepInfo firstStep) {
        DemandEntity entity = new DemandEntity();
        entity.setReference(generateReference());
        entity.setWorkflowId(workflowId);
        entity.setDemandType(snapshot.demandType().name());
        entity.setContactName(snapshot.contactName());
        entity.setAmount(snapshot.amount().amount());
        entity.setDuration(snapshot.duration());
        entity.setCreatedAt(clock.instant());
        entity.setApplicableRate(toBigDecimal(snapshot.applicableRate()));
        entity.setContactEmail(snapshot.contactEmail());
        entity.setContactPhone(snapshot.contactPhone());
        entity.setContactLanguage(snapshot.contactLanguage());
        demandDatasource.save(entity);

        detailStoreFactory.getStore(snapshot.demandType()).save(entity.getId(), snapshot.detail());

        WorkflowProcessStepDemandEntity stepDemand = new WorkflowProcessStepDemandEntity();
        stepDemand.setDemandId(entity.getId());
        stepDemand.setWorkflowProcessStepId(firstStep.workflowProcessStepId());
        stepDemand.setEnteredAt(clock.instant());
        stepDemandDatasource.save(stepDemand);

        return toDemandModel(entity);
    }

    @Override
    public Optional<DemandModel> findDemandById(Long demandId) {
        return demandDatasource.findById(demandId).map(this::toDemandModel);
    }

    @Override
    public List<DemandModel> findAllDemandsByIds(List<Long> demandIds) {
        return demandDatasource.findAllById(demandIds).stream().map(this::toDemandModel).toList();
    }

    @Override
    public CreditPage<DemandModel> findAllDemands(int page, int size, Long enterpriseId, String companyName,
                                                  DemandStatus status, Money minAmount, Money maxAmount) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Specification<DemandEntity>> filters = Stream.of(
            enterpriseId != null ? DemandSpecifications.enterpriseIdIs(enterpriseId) : null,
            companyName != null && !companyName.isBlank() ? DemandSpecifications.contactNameContains(companyName)
                : null,
            status != null ? DemandSpecifications.statusIs(status) : null,
            minAmount != null ? DemandSpecifications.amountGreaterThanOrEqualTo(minAmount.amount()) : null,
            maxAmount != null ? DemandSpecifications.amountLessThanOrEqualTo(maxAmount.amount()) : null
        ).filter(Objects::nonNull).toList();
        Page<DemandEntity> result = demandDatasource.findAll(Specification.allOf(filters), pageRequest);
        Map<Long, DemandDetail> detailsByDemandId = loadDetailsByDemandId(result.getContent());
        return CreditPage.of(result.getContent().stream()
            .map(entity -> toDemandModel(entity, detailsByDemandId.get(entity.getId())))
            .toList(), page, size, result.getTotalElements());
    }

    @Override
    public Optional<WorkflowStepInfo> findCurrentStepInfo(Long demandId) {
        return stepDemandDatasource.findOpenByDemandId(demandId)
            .flatMap(row -> stepDatasource.findInfoById(row.getWorkflowProcessStepId()))
            .map(WorkflowRepository::toStepInfo);
    }

    @Override
    public Optional<DemandStepModel> findCurrentStep(Long demandId) {
        return stepDemandDatasource.findOpenByDemandId(demandId)
            .flatMap(row -> stepDatasource.findInfoById(row.getWorkflowProcessStepId())
                .map(info -> new DemandStepModel(info.getProcessCode(), info.getProcessName(),
                    info.getStepCode(), info.getStepName(), info.getTypicalRole(),
                    info.getRequiredGradeCode(), info.getRequiredGradeName(),
                    info.getRequiredTeamCode(), info.getRequiredTeamName(),
                    row.getAssignedUsername(), row.getAssignedBy(), row.getEnteredAt())));
    }

    @Override
    public Map<Long, DemandStepSummary> findCurrentStepSummaries(List<Long> demandIds) {
        if (demandIds.isEmpty()) {
            return Map.of();
        }
        return stepDemandDatasource.findOpenStepSummariesByDemandIds(demandIds).stream()
            .collect(Collectors.toMap(DemandCurrentStepProjection::getDemandId,
                projection -> new DemandStepSummary(projection.getProcessName(), projection.getStepName())));
    }

    @Override
    public List<DemandHistoryEntryModel> findHistory(Long demandId) {
        List<WorkflowProcessStepDemandEntity> rows = stepDemandDatasource.findHistoryByDemandId(demandId);
        List<Long> stepIds = rows.stream().map(WorkflowProcessStepDemandEntity::getWorkflowProcessStepId)
            .distinct().toList();

        Map<Long, WorkflowProcessStepInfoProjection> infoByStepId = stepIds.isEmpty() ? Map.of()
            : stepDatasource.findInfoByIds(stepIds).stream()
                .collect(Collectors.toMap(WorkflowProcessStepInfoProjection::getId, Function.identity()));
        return rows.stream()
            .map(row -> {
                WorkflowProcessStepInfoProjection info = infoByStepId.get(row.getWorkflowProcessStepId());
                if (info == null) {
                    throw WorkflowException.of(STEP_NOT_FOUND);
                }
                return new DemandHistoryEntryModel(info.getProcessCode(), info.getProcessName(),
                    info.getStepCode(), info.getStepName(), info.getTypicalRole(),
                    info.getRequiredGradeCode(), info.getRequiredGradeName(),
                    info.getRequiredTeamCode(), info.getRequiredTeamName(),
                    row.getAssignedUsername(), row.getAssignedBy(), row.getEnteredAt(), row.getExitedAt(),
                    row.getDecisionNote(), row.getDecisionOutcome(), row.getReturnReasonCode(),
                    row.getRecommendations(), row.getReserves(), row.getCommitteeType(),
                    row.getInsuranceAdiSubscribed(), row.getInsuranceCoverageType(),
                    row.getInsuranceDamageSubscribed(),
                    row.getRateOption(), row.getPreviousRate(), row.getAppliedRate(), row.getRateChangedBy(),
                    row.getAmountOption(), row.getPreviousAmount(), row.getAppliedAmount(),
                    row.getDurationOption(), row.getPreviousDuration(), row.getAppliedDuration(),
                    row.getClassificationOutcome());
            })
            .toList();
    }

    @Override
    public boolean hasClosedAnyStep(Long demandId, String username) {
        return stepDemandDatasource.existsByDemandIdAndClosedBy(demandId, username);
    }

    @Override
    public Optional<Long> findUnclassifiedDemandId(DemandType demandType, String contactEmail,
            String contactName) {
        return demandDatasource.findUnclassifiedDemandId(demandType.name(), contactEmail, contactName);
    }

    @Override
    public List<DemandAssignmentHistoryModel> findAssignmentHistory(Long demandId) {
        return assignmentHistoryDatasource.findByDemandIdOrderByAssignedAtAsc(demandId).stream()
            .map(WorkflowRepository::toAssignmentHistoryModel)
            .toList();
    }

    private static DemandAssignmentHistoryModel toAssignmentHistoryModel(DemandAssignmentHistoryEntity e) {
        return new DemandAssignmentHistoryModel(e.getId(), e.getPublicId(), e.getStepCode(), e.getStepName(),
            e.getPreviousUsername(), e.getNewUsername(), e.getAssignedBy(), e.getAssignedAt());
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<Demand> findForUpdate(Long demandId) {
        return demandDatasource.findByIdForUpdate(demandId).map(entity -> {
            Optional<WorkflowProcessStepDemandEntity> open = stepDemandDatasource.findOpenByDemandId(demandId);
            WorkflowStepInfo currentStep = open
                .flatMap(row -> stepDatasource.findInfoById(row.getWorkflowProcessStepId()))
                .map(WorkflowRepository::toStepInfo)
                .orElse(null);
            return Demand.rehydrate(toDemandModel(entity), currentStep,
                open.map(WorkflowProcessStepDemandEntity::getAssignedUsername).orElse(null));
        });
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void save(Demand demand) {
        Long demandId = demand.id();
        for (DemandChange change : demand.pullChanges()) {
            switch (change) {
                case DemandChange.StepClosed closed -> closeStep(demandId, closed);
                case DemandChange.ClassificationClosed closed -> closeClassification(demandId, closed);
                case DemandChange.TermsApplied terms -> applyTerms(demandId, terms);
                case DemandChange.StepOpened opened -> openStep(demandId, opened);
                case DemandChange.EnterpriseAttached attached -> detailStoreFactory.getStore(DemandType.ENTERPRISE)
                    .attachEnterprise(demandId, attached.enterpriseId());
                case DemandChange.Reassigned reassigned -> reassign(demandId, reassigned);
                case DemandChange.SimulationRefreshed refreshed ->
                    refreshSimulationTerms(demandId, refreshed.snapshot());
                case DemandChange.Completed ignored -> requireDemand(demandId).setCompletedAt(clock.instant());
                case DemandChange.Rejected ignored -> requireDemand(demandId).setRejectedAt(clock.instant());
            }
        }
    }

    private void closeStep(Long demandId, DemandChange.StepClosed closed) {
        StepClosureDetails details = closed.details();
        RateDecision rateDecision = closed.decisions().rate();
        AmountDecision amountDecision = closed.decisions().amount();
        DurationDecision durationDecision = closed.decisions().duration();
        WorkflowProcessStepDemandEntity open = requireOpenStep(demandId);
        open.setExitedAt(clock.instant());
        open.setDecisionNote(details.note());
        open.setDecisionOutcome(details.decisionOutcome() != null ? details.decisionOutcome().name() : null);
        open.setReturnReasonCode(details.returnReasonCode());
        open.setRecommendations(details.recommendations());
        open.setReserves(details.reserves());
        open.setCommitteeType(details.committeeType() != null ? details.committeeType().name() : null);
        open.setInsuranceAdiSubscribed(details.insuranceAdiSubscribed());
        open.setInsuranceCoverageType(details.insuranceCoverageType() != null
            ? details.insuranceCoverageType().name() : null);
        open.setInsuranceDamageSubscribed(details.insuranceDamageSubscribed());
        open.setRateOption(rateDecision.option().name());
        open.setPreviousRate(toBigDecimal(rateDecision.previousRate()));
        open.setAppliedRate(rateDecision.appliedRate().value());
        open.setRateChangedBy(rateDecision.changedBy());
        open.setAmountOption(amountDecision.option().name());
        open.setPreviousAmount(toBigDecimal(amountDecision.previousAmount()));
        open.setAppliedAmount(amountDecision.appliedAmount().amount());
        open.setClosedBy(rateDecision.changedBy());
        open.setDurationOption(durationDecision.option().name());
        open.setPreviousDuration(durationDecision.previousDuration());
        open.setAppliedDuration(durationDecision.appliedDuration());
        stepDemandDatasource.saveAndFlush(open);
    }

    private void closeClassification(Long demandId, DemandChange.ClassificationClosed closed) {
        WorkflowProcessStepDemandEntity open = requireOpenStep(demandId);
        open.setExitedAt(clock.instant());
        open.setClosedBy(closed.closedBy());
        open.setDecisionNote(closed.note());
        open.setClassificationOutcome(closed.outcome().name());
        stepDemandDatasource.saveAndFlush(open);
    }

    private void applyTerms(Long demandId, DemandChange.TermsApplied terms) {
        DemandEntity entity = requireDemand(demandId);
        entity.setApplicableRate(terms.rate().value());
        entity.setAmount(terms.amount().amount());
        entity.setDuration(terms.duration());
    }

    private void openStep(Long demandId, DemandChange.StepOpened opened) {
        WorkflowProcessStepDemandEntity stepDemand = new WorkflowProcessStepDemandEntity();
        stepDemand.setDemandId(demandId);
        stepDemand.setWorkflowProcessStepId(opened.workflowProcessStepId());
        stepDemand.setAssignedUsername(opened.assignee());
        stepDemand.setAssignedBy(opened.assignedBy());
        stepDemand.setEnteredAt(clock.instant());
        stepDemandDatasource.save(stepDemand);
    }

    private void reassign(Long demandId, DemandChange.Reassigned reassigned) {
        WorkflowProcessStepDemandEntity open = requireOpenStep(demandId);
        open.setAssignedUsername(reassigned.newUsername());
        open.setAssignedBy(reassigned.assignedBy());
        stepDemandDatasource.save(open);

        DemandAssignmentHistoryEntity history = new DemandAssignmentHistoryEntity();
        history.setDemandId(demandId);
        history.setStepCode(reassigned.stepCode());
        history.setStepName(reassigned.stepName());
        history.setPreviousUsername(reassigned.previousUsername());
        history.setNewUsername(reassigned.newUsername());
        history.setAssignedBy(reassigned.assignedBy());
        history.setAssignedAt(clock.instant());
        assignmentHistoryDatasource.save(history);
    }

    private void refreshSimulationTerms(Long demandId, DemandSnapshot snapshot) {
        DemandEntity entity = requireDemand(demandId);
        entity.setAmount(snapshot.amount().amount());
        entity.setDuration(snapshot.duration());
        entity.setApplicableRate(toBigDecimal(snapshot.applicableRate()));
        entity.setContactPhone(snapshot.contactPhone());
        if (snapshot.contactLanguage() != null) {
            entity.setContactLanguage(snapshot.contactLanguage());
        }
        detailStoreFactory.getStore(snapshot.demandType()).save(demandId, snapshot.detail());
    }

    private WorkflowProcessStepDemandEntity requireOpenStep(Long demandId) {
        return stepDemandDatasource.findOpenByDemandId(demandId)
            .orElseThrow(() -> WorkflowException.of(STEP_NOT_FOUND));
    }

    private DemandEntity requireDemand(Long demandId) {
        return demandDatasource.findById(demandId)
            .orElseThrow(() -> WorkflowException.of(DEMAND_NOT_FOUND));
    }

    private static String generateReference() {
        return "DEM-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private DemandModel toDemandModel(DemandEntity e) {
        DemandType demandType = DemandType.valueOf(e.getDemandType());
        return toDemandModel(e, detailStoreFactory.getStore(demandType).load(e.getId()));
    }

    private DemandModel toDemandModel(DemandEntity e, DemandDetail detail) {
        DemandType demandType = DemandType.valueOf(e.getDemandType());
        return new DemandModel(e.getId(), e.getPublicId(), e.getReference(), e.getWorkflowId(), demandType,
            e.getContactName(), Money.of(e.getAmount()), e.getDuration(), e.getCreatedAt(), e.getCompletedAt(),
            toRate(e.getApplicableRate()),
            e.getContactEmail(), e.getContactPhone(), e.getContactLanguage(), e.getRejectedAt(), detail);
    }

    private Map<Long, DemandDetail> loadDetailsByDemandId(List<DemandEntity> entities) {
        return entities.stream()
            .collect(Collectors.groupingBy(DemandEntity::getDemandType,
                Collectors.mapping(DemandEntity::getId, Collectors.toList())))
            .entrySet().stream()
            .flatMap(entry -> detailStoreFactory.getStore(DemandType.valueOf(entry.getKey())).loadAll(entry.getValue())
                .entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static WorkflowStepInfo toStepInfo(WorkflowProcessStepInfoProjection projection) {
        NotificationType notifyContactOnEnterType = projection.getNotifyContactOnEnterType() != null
            ? NotificationType.valueOf(projection.getNotifyContactOnEnterType()) : null;
        return new WorkflowStepInfo(projection.getId(), projection.getWorkflowId(), projection.getProcessCode(),
            projection.getProcessName(), projection.getStepCode(), projection.getStepName(),
            projection.getTypicalRole(), projection.getGlobalOrder(), projection.getRequiredGradeId(),
            projection.getRequiredGradeCode(), projection.getRequiredGradeName(), projection.getRequiredTeamId(),
            projection.getRequiredTeamCode(), projection.getRequiredTeamName(),
            Boolean.TRUE.equals(projection.getRequiresClassification()),
            Boolean.TRUE.equals(projection.getRequiresInsuranceConfirmation()), notifyContactOnEnterType,
            Boolean.TRUE.equals(projection.getRequiresFourEyes()));
    }
}
