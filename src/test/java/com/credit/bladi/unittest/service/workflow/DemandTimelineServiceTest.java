package com.credit.bladi.unittest.service.workflow;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_6;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_ANALYSTE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CURRENT;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_DECISION;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_DECISION_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_DONE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_INITIATION;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_INITIATION_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_INSTRUCTION;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_INSTRUCTION_TS_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_PAE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_PIECE_MANQUANTE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_RISK_ANALYSIS;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_RISK_ANALYSIS_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_UPCOMING;
import static com.credit.bladi.unittest.mock.data.MockCodes.REFERENCE_DEM_ABCDEF12;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL;
import static com.credit.bladi.unittest.mock.data.MockDates.DATE_2026_08_01T00_00_00Z;
import static com.credit.bladi.unittest.mock.data.MockDates.DATE_2026_08_02T00_00_00Z;
import static com.credit.bladi.unittest.mock.data.MockDates.DATE_2026_08_03T00_00_00Z;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_ANALYSE_CONTRE_RISQUE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_ANALYSTE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_ANALYSTE_PAE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CHARGE_D_AFFAIRES;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CHARGE_D_INVESTISSEMENT_RESPONSABLES_PPIF;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DECIDEURS_DESIGNES_PAE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DECISION;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DECISION_COMITE_TOURNANT_OU_SEANCE_TENANTE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_EN_ATTENTE_DE_PIECE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_INITIATION_DU_DOSSIER;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_INSTRUCTION;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_PREPARATION_ET_VALIDATION_DE_LA_TS_INDICATIVE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ADMIN;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ANALYST1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.model.audit.AuditTarget;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.NotificationType;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
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
import com.credit.bladi.service.workflow.engine.DemandTimelineService;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import com.credit.bladi.unittest.mock.workflow.MockDemand;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DemandTimelineServiceTest {
    @Mock
    private IWorkflowRepository repository;

    @Mock
    private IDemandCommentRepository commentRepository;

    @Mock
    private IWorkflowEngineService workflowEngineService;

    @Mock
    private IAuditService auditService;

    @Mock
    private ICurrentActorProvider currentActorProvider;

    private DemandTimelineService timelineService;

    @BeforeEach
    void setUp() {
        timelineService = new DemandTimelineService(repository, commentRepository, workflowEngineService,
            auditService, currentActorProvider);
    }

    @Test
    void shouldMapStepsToDoneCurrentAndUpcomingInPipeline() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, Money.of(AMOUNT_1500000), 84, Instant.now(), null,
            new Rate(AMOUNT_6), null, null, null, null, null, null, null, null, null, null);
        WorkflowStepInfo initiationStep = new WorkflowStepInfo(1L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);
        WorkflowStepInfo instructionStep = new WorkflowStepInfo(2L, 1L, CODE_INSTRUCTION, LABEL_INSTRUCTION,
            CODE_INSTRUCTION_TS_STEP, LABEL_PREPARATION_ET_VALIDATION_DE_LA_TS_INDICATIVE,
            LABEL_CHARGE_D_INVESTISSEMENT_RESPONSABLES_PPIF, 2001,
            null, null, null, null, null, null);
        WorkflowStepInfo decisionStep = new WorkflowStepInfo(10L, 1L, CODE_DECISION, LABEL_DECISION,
            CODE_DECISION_STEP, LABEL_DECISION_COMITE_TOURNANT_OU_SEANCE_TENANTE, LABEL_DECIDEURS_DESIGNES_PAE, 4001,
            null, null, null, null, null, null, false, false, NotificationType.DEMAND_IN_DECISION, false);

        Instant enteredInitiation = Instant.parse(DATE_2026_08_01T00_00_00Z);
        Instant exitedInitiation = Instant.parse(DATE_2026_08_02T00_00_00Z);
        Instant enteredInstruction = Instant.parse(DATE_2026_08_02T00_00_00Z);
        DemandHistoryEntryModel doneEntry = new DemandHistoryEntryModel(CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, null,
            null, null,
            null, USER_ADMIN, null, enteredInitiation, exitedInitiation, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null);
        DemandHistoryEntryModel currentEntry = new DemandHistoryEntryModel(CODE_INSTRUCTION, LABEL_INSTRUCTION,
            CODE_INSTRUCTION_TS_STEP, LABEL_PREPARATION_ET_VALIDATION_DE_LA_TS_INDICATIVE,
            LABEL_CHARGE_D_INVESTISSEMENT_RESPONSABLES_PPIF, null, null, null, null, USER_ADMIN, null,
            enteredInstruction,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null);

        when(workflowEngineService.getDemand(1L)).thenReturn(activeDemand);
        when(repository.findAllSteps(1L)).thenReturn(List.of(initiationStep, instructionStep, decisionStep));
        when(repository.findHistory(1L)).thenReturn(List.of(doneEntry, currentEntry));

        List<DemandPipelineStepModel> pipeline = timelineService.getPipeline(1L);

        assertEquals(3, pipeline.size());
        assertEquals(CODE_DONE, pipeline.get(0).status());
        assertEquals(enteredInitiation, pipeline.get(0).enteredAt());
        assertEquals(exitedInitiation, pipeline.get(0).exitedAt());
        assertEquals(CODE_CURRENT, pipeline.get(1).status());
        assertEquals(enteredInstruction, pipeline.get(1).enteredAt());
        assertEquals(CODE_UPCOMING, pipeline.get(2).status());
        assertEquals(null, pipeline.get(2).enteredAt());
    }

    @Test
    void shouldKeepMostRecentVisitWhenStepWasReturnedToAndReenteredInPipeline() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, Money.of(AMOUNT_1500000), 84, Instant.now(), null,
            new Rate(AMOUNT_6), null, null, null, null, null, null, null, null, null, null);
        WorkflowStepInfo initiationStep = new WorkflowStepInfo(1L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);
        WorkflowStepInfo instructionStep = new WorkflowStepInfo(2L, 1L, CODE_INSTRUCTION, LABEL_INSTRUCTION,
            CODE_INSTRUCTION_TS_STEP, LABEL_PREPARATION_ET_VALIDATION_DE_LA_TS_INDICATIVE,
            LABEL_CHARGE_D_INVESTISSEMENT_RESPONSABLES_PPIF, 2001,
            null, null, null, null, null, null);

        Instant firstEntry = Instant.parse(DATE_2026_08_01T00_00_00Z);
        Instant firstExit = Instant.parse(DATE_2026_08_02T00_00_00Z);
        Instant secondEntry = Instant.parse(DATE_2026_08_03T00_00_00Z);
        DemandHistoryEntryModel firstVisit = new DemandHistoryEntryModel(CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, null,
            null, null,
            null, USER_ADMIN, null, firstEntry, firstExit, null, null, CODE_PIECE_MANQUANTE, null, null, null, null,
            null,
            null, null, null, null, null, null, null, null, null, null, null, null);
        DemandHistoryEntryModel secondVisit = new DemandHistoryEntryModel(CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, null,
            null, null,
            null, USER_ADMIN, null, secondEntry, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null);

        when(workflowEngineService.getDemand(1L)).thenReturn(activeDemand);
        when(repository.findAllSteps(1L)).thenReturn(List.of(initiationStep, instructionStep));
        when(repository.findHistory(1L)).thenReturn(List.of(firstVisit, secondVisit));

        List<DemandPipelineStepModel> pipeline = timelineService.getPipeline(1L);

        assertEquals(2, pipeline.size());
        assertEquals(CODE_CURRENT, pipeline.get(0).status());
        assertEquals(secondEntry, pipeline.get(0).enteredAt());
        assertEquals(CODE_UPCOMING, pipeline.get(1).status());
    }

    @Test
    void shouldAddCommentUsingCurrentStepCodeAndName() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, Money.of(AMOUNT_1500000), 84, Instant.now(), null,
            new Rate(AMOUNT_6), null, null, null, null, null, null, null, null, null, null);
        DemandStepModel currentStep = new DemandStepModel(CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE,
            CODE_ANALYSTE, LABEL_ANALYSTE, CODE_PAE, CODE_PAE, USER_ANALYST1, null, Instant.now());
        DemandCommentModel savedComment = new DemandCommentModel(1L, UUID.randomUUID(), CODE_RISK_ANALYSIS_STEP,
            LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, USER_ANALYST1, LABEL_EN_ATTENTE_DE_PIECE, Instant.now());

        when(workflowEngineService.getDemand(1L)).thenReturn(activeDemand);
        when(workflowEngineService.getCurrentStep(1L)).thenReturn(currentStep);
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ANALYST1);
        when(commentRepository.add(1L, CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT,
            USER_ANALYST1,
            LABEL_EN_ATTENTE_DE_PIECE)).thenReturn(savedComment);

        DemandCommentModel result = timelineService.addComment(1L, LABEL_EN_ATTENTE_DE_PIECE);

        assertEquals(savedComment, result);
        verify(auditService).log(eq(USER_ANALYST1), eq(AuditAction.DEMAND_COMMENTED), any(AuditTarget.class), any());
    }

    @Test
    void shouldReturnCommentsFromRepository() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, Money.of(AMOUNT_1500000), 84, Instant.now(), null,
            new Rate(AMOUNT_6), null, null, null, null, null, null, null, null, null, null);
        DemandCommentModel comment = new DemandCommentModel(1L, UUID.randomUUID(), CODE_RISK_ANALYSIS_STEP,
            LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, USER_ANALYST1, LABEL_EN_ATTENTE_DE_PIECE, Instant.now());

        when(workflowEngineService.getDemand(1L)).thenReturn(activeDemand);
        when(commentRepository.findByDemandId(1L)).thenReturn(List.of(comment));

        List<DemandCommentModel> result = timelineService.getComments(1L);

        assertEquals(1, result.size());
        assertEquals(comment, result.get(0));
    }
}
