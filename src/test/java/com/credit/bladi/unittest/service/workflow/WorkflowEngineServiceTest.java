package com.credit.bladi.unittest.service.workflow;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_100000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_20000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_2000000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_5000000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_6;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_600000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_99999999;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_01;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_7;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_14_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_25;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_ANALYSTE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CLASSIFICATION;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CLASSIFICATION_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CONTRACT;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CONTRACT_EDITION;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CONTRACT_EDITION_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CONTRACT_SIGNATURE_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CUSTOM_PROCESS;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_DECISION;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_DECISION_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_GESTIONNAIRE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_GE_INVESTMENT;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_GUARANTEES;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_GUARANTEES_MISE_EN_GESTION_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_GUARANTEES_NOTIF_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_INITIATION;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_INITIATION_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_INSTRUCTION;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_INSTRUCTION_TS_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_INSURANCE_VALIDATION_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MORTGAGE_DECISION;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MORTGAGE_DECISION_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MORTGAGE_INSTRUCTION;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MORTGAGE_INSTRUCTION_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_OGS_MEG;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_PAE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_PIECE_MANQUANTE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_PME_INVESTMENT;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_RESP_DEPT;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_RISK_ANALYSIS;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_RISK_ANALYSIS_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_RISK_COMMITTEE_PROG_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_RISK_RECEPTION_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_RISK_VALIDATION_DEPT_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_SOME_OTHER_INSURANCE_STEP_CODE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_SOME_OTHER_STEP_CODE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_SUPERVISEUR;
import static com.credit.bladi.unittest.mock.data.MockCodes.REFERENCE_DEM_ABCDEF12;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_CONTACT_AT_ATLAS_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_JEAN_AT_EXAMPLE_COM;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_SALMA_AT_GMAIL_COM;
import static com.credit.bladi.unittest.mock.data.MockContacts.PERSON_JEAN_DUPONT;
import static com.credit.bladi.unittest.mock.data.MockContacts.PERSON_RESPONSABLE_DEPARTEMENT;
import static com.credit.bladi.unittest.mock.data.MockContacts.PERSON_SALMA_IDRISSI;
import static com.credit.bladi.unittest.mock.data.MockContacts.PERSON_SUPPORT_ADMINISTRATIF;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0612345678;
import static com.credit.bladi.unittest.mock.data.MockContacts.RIB_230_780_0123456789012345_03;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_ANALYSE_CONTRE_RISQUE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_ANALYSTE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_ANALYSTE_PAE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_ASSURANCES_ET_GESTION_DES_GARANTIES;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CHARGE_D_AFFAIRES;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CHARGE_D_INVESTISSEMENT_RESPONSABLES_PPIF;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CLASSIFICATION_DU_DOSSIER;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CLIENT_CONFIRME;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CLIENT_INJOIGNABLE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CUSTOM_PROCESS;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DECIDEURS_DESIGNES_PAE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DECISION;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DECISION_2;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DECISION_COMITE_TOURNANT_OU_SEANCE_TENANTE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DEM_1;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DIRECTEUR_D_AGENCE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DOSSIER_COMPLET;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_EDITION_DES_CONTRATS;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_GARANTIES;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_GESTIONNAIRE_OGS_GARANTIES;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_GESTIONNAIRE_OGS_MISE_EN_GESTION;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_INITIATION_DU_DOSSIER;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_INSTRUCTION;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_MISE_EN_GESTION_DU_DOSSIER;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_NOTIFICATION_DES_GARANTIES;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_PREPARATION_ET_VALIDATION_DE_LA_TS_INDICATIVE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_PROGRAMMATION_DU_COMITE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_RECEPTION_DU_DOSSIER_ET_AFFECTATION;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_RESPONSABLE_DEPARTEMENT_DIRECTION_PAE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_RESPONSABLE_DEPARTEMENT_PAE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_SOUSCRIPTION_ET_VALIDATION_DES_ASSURANCES;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_TRAITEMENT_ANALYSE_CONTRE_RISQUE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_VALIDATION_DE_LA_CONTRE_ANALYSE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_VALIDATION_INITIALE_DU_DOSSIER_AVEC_LE_CLIENT;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ADMIN;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ANALYST1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ANALYST2;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_GESTIONNAIRE1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_PPIF_USER;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_RESP_DEPT;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_SUPERVISEUR_OGS;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_SUPERVISEUR_PAE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_SUPPORT1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.exception.WorkflowException;
import com.credit.bladi.exception.WorkflowException.WorkflowExceptionType;
import com.credit.bladi.model.audit.AuditTarget;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.ClassificationOutcome;
import com.credit.bladi.model.enums.CommitteeType;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.DecisionOutcome;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.InsuranceCoverageType;
import com.credit.bladi.model.enums.KeepOrManualOption;
import com.credit.bladi.model.enums.NotificationType;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.enums.StepDirection;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.notification.LocalizedText;
import com.credit.bladi.model.parameters.RegulatoryLimits;
import com.credit.bladi.model.user.User;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Rib;
import com.credit.bladi.model.workflow.Demand;
import com.credit.bladi.model.workflow.DemandChange;
import com.credit.bladi.model.workflow.DemandHistoryEntryModel;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.DemandSnapshot;
import com.credit.bladi.model.workflow.DemandStepModel;
import com.credit.bladi.model.workflow.DemandStepSummary;
import com.credit.bladi.model.workflow.StepClosureDetails;
import com.credit.bladi.model.workflow.WorkflowStepInfo;
import com.credit.bladi.model.workflow.event.DemandCompletedEvent;
import com.credit.bladi.repository.client.IEnterpriseRepository;
import com.credit.bladi.repository.user.IUserRepository;
import com.credit.bladi.repository.workflow.IWorkflowRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.borrower.IMortgageBorrowerService;
import com.credit.bladi.service.enterprise.IEnterpriseLinkClassificationResolver;
import com.credit.bladi.service.investment.EnterpriseWorkflowCodeResolver;
import com.credit.bladi.service.mortgage.MortgageWorkflowCodeResolver;
import com.credit.bladi.service.notification.AfterCommitNotifications;
import com.credit.bladi.service.notification.DemandContactNotifier;
import com.credit.bladi.service.notification.INotificationService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import com.credit.bladi.service.workflow.access.StepAssignmentPolicy;
import com.credit.bladi.service.workflow.engine.WorkflowEngineService;
import com.credit.bladi.service.workflow.engine.WorkflowNotifications;
import com.credit.bladi.service.workflow.product.DemandEligibilityValidator;
import com.credit.bladi.service.workflow.product.DemandEligibilityValidatorFactory;
import com.credit.bladi.service.workflow.product.WorkflowCodeResolverFactory;
import com.credit.bladi.unittest.mock.workflow.MockDemand;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class WorkflowEngineServiceTest {
    @Mock
    private IWorkflowRepository repository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IEnterpriseRepository enterpriseRepository;

    @Mock
    private IAuditService auditService;

    @Mock
    private ICurrentActorProvider currentActorProvider;

    @Mock
    private DemandEligibilityValidator eligibilityValidator;

    @Mock
    private DemandEligibilityValidatorFactory eligibilityValidatorFactory;

    @Mock
    private INotificationService notificationService;

    @Mock
    private IEnterpriseLinkClassificationResolver enterpriseUserLinkService;

    @Mock
    private DemandContactNotifier demandContactNotifier;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private IMortgageBorrowerService borrowerService;

    private WorkflowEngineService workflowEngineService;

    private DemandSnapshot snapshot;

    private final List<DemandChange> savedChanges = new ArrayList<>();

    @BeforeEach
    void setUp() {
        lenient().when(repository.findForUpdate(anyLong())).thenAnswer(invocation -> {
            Long demandId = invocation.getArgument(0);
            return repository.findDemandById(demandId).map(state -> Demand.rehydrate(state,
                repository.findCurrentStepInfo(demandId).orElse(null),
                repository.findCurrentStep(demandId).map(DemandStepModel::assignedUsername).orElse(null)));
        });
        lenient().doAnswer(invocation -> savedChanges.addAll(invocation.<Demand>getArgument(0).pendingChanges()))
            .when(repository).save(any(Demand.class));
        lenient().when(eligibilityValidatorFactory.get(any())).thenReturn(eligibilityValidator);
        WorkflowCodeResolverFactory workflowCodeResolverFactory = new WorkflowCodeResolverFactory(
            List.of(new EnterpriseWorkflowCodeResolver(), new MortgageWorkflowCodeResolver()));
        workflowEngineService = new WorkflowEngineService(repository, enterpriseRepository, auditService,
            currentActorProvider, eligibilityValidatorFactory, enterpriseUserLinkService, eventPublisher,
            workflowCodeResolverFactory,
            new StepAssignmentPolicy(userRepository, repository,
                new RegulatoryLimits(Rate.of(DECIMAL_14_00)), true),
            new WorkflowNotifications(notificationService, demandContactNotifier, userRepository,
                new AfterCommitNotifications(Runnable::run)), borrowerService);
        snapshot = MockDemand.enterpriseSnapshot(COMPANY_ATLAS_TEXTILE_SARL, EnterpriseType.PME,
            Money.of(AMOUNT_1500000), 84, null, null, null, null, null, null, null, null, null);
    }

    @Test
    void shouldCreateDemandAtFirstStepOfMatchingWorkflow() {
        WorkflowStepInfo firstStep = new WorkflowStepInfo(1L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);
        DemandModel created = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME,
            snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null, null, null, null,
                null, null, null, null, null);

        when(repository.findActiveWorkflowIdByCode(CODE_PME_INVESTMENT)).thenReturn(Optional.of(1L));
        when(repository.findFirstStep(1L)).thenReturn(Optional.of(firstStep));
        when(repository.createDemand(snapshot, 1L, firstStep)).thenReturn(created);

        DemandModel result = workflowEngineService.createDemand(snapshot);

        assertEquals(REFERENCE_DEM_ABCDEF12, result.reference());
    }

    @Test
    void shouldMapTpeAutoEntrepreneurAndPmeToThePmeInvestmentWorkflow() {
        WorkflowStepInfo firstStep = new WorkflowStepInfo(1L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);
        DemandModel created = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L, "X",
            EnterpriseType.TPE,
            Money.of(AMOUNT_100000), 60, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        when(repository.findActiveWorkflowIdByCode(CODE_PME_INVESTMENT)).thenReturn(Optional.of(1L));
        when(repository.findFirstStep(1L)).thenReturn(Optional.of(firstStep));
        when(repository.createDemand(any(), eq(1L), eq(firstStep))).thenReturn(created);

        EnterpriseType[] smallTierTypes = {EnterpriseType.TPE, EnterpriseType.AUTO_ENTREPRENEUR, EnterpriseType.PME};
        for (EnterpriseType type : smallTierTypes) {
            DemandSnapshot smallTierSnapshot = MockDemand.enterpriseSnapshot("X", type,
                Money.of(AMOUNT_100000), 60,
                null, null, null, null, null, null, null, null, null);
            workflowEngineService.createDemand(smallTierSnapshot);
        }

        verify(repository, times(3)).findActiveWorkflowIdByCode(CODE_PME_INVESTMENT);
    }

    @Test
    void shouldMapEtiAndGeToTheGeInvestmentWorkflow() {
        WorkflowStepInfo firstStep = new WorkflowStepInfo(1L, 2L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);
        DemandModel created = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 2L, "X",
            EnterpriseType.GE,
            Money.of(AMOUNT_5000000), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        when(repository.findActiveWorkflowIdByCode(CODE_GE_INVESTMENT)).thenReturn(Optional.of(2L));
        when(repository.findFirstStep(2L)).thenReturn(Optional.of(firstStep));
        when(repository.createDemand(any(), eq(2L), eq(firstStep))).thenReturn(created);

        EnterpriseType[] largeTierTypes = {EnterpriseType.ETI, EnterpriseType.GE};
        for (EnterpriseType type : largeTierTypes) {
            DemandSnapshot largeTierSnapshot = MockDemand.enterpriseSnapshot("X", type,
                Money.of(AMOUNT_5000000), 84,
                null, null, null, null, null, null, null, null, null);
            workflowEngineService.createDemand(largeTierSnapshot);
        }

        verify(repository, times(2)).findActiveWorkflowIdByCode(CODE_GE_INVESTMENT);
    }

    @Test
    void shouldThrowWhenWorkflowNotConfigured() {
        when(repository.findActiveWorkflowIdByCode(CODE_PME_INVESTMENT)).thenReturn(Optional.empty());

        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> workflowEngineService.createDemand(snapshot));

        assertEquals(WorkflowExceptionType.WORKFLOW_NOT_CONFIGURED, ex.getType());
    }

    @Test
    void shouldOpenNextStepWhenAdvancingBeforeLastStep() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo currentStep = new WorkflowStepInfo(1L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);
        WorkflowStepInfo nextStep = new WorkflowStepInfo(2L, 1L, CODE_INSTRUCTION, LABEL_INSTRUCTION,
            CODE_INSTRUCTION_TS_STEP, LABEL_PREPARATION_ET_VALIDATION_DE_LA_TS_INDICATIVE,
            LABEL_CHARGE_D_INVESTISSEMENT_RESPONSABLES_PPIF, 2001,
            null, null, null, null, null, null);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(currentStep));
        when(repository.findNextStep(1L, 1001)).thenReturn(Optional.of(nextStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        StepClosureDetails details = new StepClosureDetails(LABEL_DOSSIER_COMPLET, null, null, null, null, null, null,
            null,
            null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);
        workflowEngineService.advance(1L, details, null);

        assertTrue(stepClosedWith(details));
        assertTrue(stepOpened(2L));
        assertFalse(saved(DemandChange.Completed.class));
        verify(auditService).log(eq(USER_ADMIN), eq(AuditAction.DEMAND_ADVANCED), any(AuditTarget.class), any());
    }

    @Test
    void shouldApplyManualAmountAndDurationWhenAdvancing() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo currentStep = new WorkflowStepInfo(1L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);
        WorkflowStepInfo nextStep = new WorkflowStepInfo(2L, 1L, CODE_INSTRUCTION, LABEL_INSTRUCTION,
            CODE_INSTRUCTION_TS_STEP, LABEL_PREPARATION_ET_VALIDATION_DE_LA_TS_INDICATIVE,
            LABEL_CHARGE_D_INVESTISSEMENT_RESPONSABLES_PPIF, 2001,
            null, null, null, null, null, null);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(currentStep));
        when(repository.findNextStep(1L, 1001)).thenReturn(Optional.of(nextStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        StepClosureDetails details = new StepClosureDetails(LABEL_DOSSIER_COMPLET, null, null, null, null, null, null,
            null,
            null, KeepOrManualOption.KEEP, null, KeepOrManualOption.MANUAL, AMOUNT_2000000,
            KeepOrManualOption.MANUAL, 60);
        workflowEngineService.advance(1L, details, null);

        assertTrue(termsApplied(Money.of(AMOUNT_2000000), 60));
        verify(eligibilityValidator).assertEligible(activeDemand, Money.of(AMOUNT_2000000),
            new Rate(AMOUNT_6), 60);
    }

    @Test
    void shouldNotRevalidateEligibilityWhenAmountOptionIsKeep() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo currentStep = new WorkflowStepInfo(1L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);
        WorkflowStepInfo nextStep = new WorkflowStepInfo(2L, 1L, CODE_INSTRUCTION, LABEL_INSTRUCTION,
            CODE_INSTRUCTION_TS_STEP, LABEL_PREPARATION_ET_VALIDATION_DE_LA_TS_INDICATIVE,
            LABEL_CHARGE_D_INVESTISSEMENT_RESPONSABLES_PPIF, 2001,
            null, null, null, null, null, null);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(currentStep));
        when(repository.findNextStep(1L, 1001)).thenReturn(Optional.of(nextStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        StepClosureDetails details = new StepClosureDetails(LABEL_DOSSIER_COMPLET, null, null, null, null, null, null,
            null,
            null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);
        workflowEngineService.advance(1L, details, null);

        verify(eligibilityValidator, never()).assertEligible(any(), any(), any(), any());
    }

    @Test
    void shouldRejectManualAmountRejectedByEligibilityValidatorAndNotCloseTheStep() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo currentStep = new WorkflowStepInfo(1L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(currentStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);
        org.mockito.Mockito.doThrow(
                com.credit.bladi.exception.InvestmentCreditException.of(
                    com.credit.bladi.exception.InvestmentCreditException.InvestmentCreditExceptionType
                        .FINANCING_RATIO_EXCEEDED, DECIMAL_0_7))
            .when(eligibilityValidator).assertEligible(any(), any(), any(), any());

        StepClosureDetails details = new StepClosureDetails(null, null, null, null, null, null, null, null, null,
            KeepOrManualOption.KEEP, null, KeepOrManualOption.MANUAL, AMOUNT_99999999,
            KeepOrManualOption.KEEP, null);

        assertThrows(com.credit.bladi.exception.InvestmentCreditException.class,
            () -> workflowEngineService.advance(1L, details, null));

        assertFalse(saved(DemandChange.StepClosed.class));
    }

    @Test
    void shouldClassifyDemandRealAndOpenInitiationStepWithEnterpriseAttached() {
        DemandModel pendingDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, null, null, null, null, null, null,
            null, null, null, null, null);
        WorkflowStepInfo classificationStep = new WorkflowStepInfo(1L, 1L, CODE_CLASSIFICATION,
            LABEL_CLASSIFICATION_DU_DOSSIER, CODE_CLASSIFICATION_STEP,
            LABEL_VALIDATION_INITIALE_DU_DOSSIER_AVEC_LE_CLIENT,
            LABEL_CHARGE_D_AFFAIRES, 1, null, null, null, null, null, null, true, false, null, false);
        WorkflowStepInfo initiationStep = new WorkflowStepInfo(2L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);

        UUID enterprisePublicId = UUID.randomUUID();
        when(repository.findDemandById(1L)).thenReturn(Optional.of(pendingDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(classificationStep));
        when(repository.findNextStep(1L, 1)).thenReturn(Optional.of(initiationStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);
        when(enterpriseRepository.findIdByPublicId(enterprisePublicId)).thenReturn(Optional.of(5L));
        when(userRepository.findByUsername(USER_ANALYST1)).thenReturn(Optional.of(
            User.getBuilder().id(7L).username(USER_ANALYST1).role(UserRole.MANAGER).build()));

        workflowEngineService.classify(1L, ClassificationOutcome.REAL, enterprisePublicId, LABEL_CLIENT_CONFIRME,
            USER_ANALYST1, null);

        assertTrue(savedChanges.contains(new DemandChange.ClassificationClosed(ClassificationOutcome.REAL,
            LABEL_CLIENT_CONFIRME, USER_ADMIN)));
        assertTrue(savedChanges.contains(new DemandChange.EnterpriseAttached(5L)));
        assertTrue(stepOpened(2L));
        assertTrue(assignedTo(USER_ANALYST1, USER_ADMIN));
        assertFalse(saved(DemandChange.Rejected.class));
        verify(auditService).log(eq(USER_ADMIN), eq(AuditAction.DEMAND_CLASSIFIED), any(AuditTarget.class), any());
        verify(enterpriseRepository, never()).updateRib(any(), any());
    }

    @Test
    void shouldOfferPortalAccessToTheBorrowerWhenAMortgageIsClassifiedReal() {
        DemandModel mortgage = MockDemand.mortgage(1L, UUID.randomUUID(), "DEM-MORTGAGE1", 3L, PERSON_SALMA_IDRISSI,
            Money.of(AMOUNT_500000), 240, Instant.now(), null, null, EMAIL_SALMA_AT_GMAIL_COM, null, null,
            ProductType.CONVENTIONAL, CspCode.SALA, null, null, 35, false, null);
        WorkflowStepInfo classificationStep = new WorkflowStepInfo(1L, 3L, CODE_CLASSIFICATION,
            LABEL_CLASSIFICATION_DU_DOSSIER, CODE_CLASSIFICATION_STEP,
            LABEL_VALIDATION_INITIALE_DU_DOSSIER_AVEC_LE_CLIENT,
            LABEL_CHARGE_D_AFFAIRES, 1, null, null, null, null, null, null, true, false, null, false);
        WorkflowStepInfo instructionStep = new WorkflowStepInfo(2L, 3L, CODE_INSTRUCTION, LABEL_INSTRUCTION,
            CODE_MORTGAGE_INSTRUCTION_STEP, "Instruction du dossier", LABEL_ANALYSTE, 1001, null, null, null, null,
            null,
            null);
        when(repository.findDemandById(1L)).thenReturn(Optional.of(mortgage));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(classificationStep));
        when(repository.findNextStep(3L, 1)).thenReturn(Optional.of(instructionStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);
        when(userRepository.findByUsername(USER_ANALYST1)).thenReturn(Optional.of(
            User.getBuilder().id(7L).username(USER_ANALYST1).role(UserRole.MANAGER).build()));

        workflowEngineService.classify(1L, ClassificationOutcome.REAL, null, LABEL_CLIENT_CONFIRME, USER_ANALYST1,
            null);

        verify(borrowerService).resolveBorrowerForClassification(mortgage, USER_ADMIN);
        verify(enterpriseUserLinkService, never()).resolveClientForClassification(any(), any(), any(), any(), any());
    }

    @Test
    void shouldNotTouchBorrowersWhenAnInvestmentDossierIsClassifiedReal() {
        DemandModel pendingDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL, EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, null, null,
            null,
            null, null, null, null, null, null, null, null);
        WorkflowStepInfo classificationStep = new WorkflowStepInfo(1L, 1L, CODE_CLASSIFICATION,
            LABEL_CLASSIFICATION_DU_DOSSIER, CODE_CLASSIFICATION_STEP,
            LABEL_VALIDATION_INITIALE_DU_DOSSIER_AVEC_LE_CLIENT,
            LABEL_CHARGE_D_AFFAIRES, 1, null, null, null, null, null, null, true, false, null, false);
        WorkflowStepInfo initiationStep = new WorkflowStepInfo(2L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);
        UUID enterprisePublicId = UUID.randomUUID();
        when(repository.findDemandById(1L)).thenReturn(Optional.of(pendingDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(classificationStep));
        when(repository.findNextStep(1L, 1)).thenReturn(Optional.of(initiationStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);
        when(enterpriseRepository.findIdByPublicId(enterprisePublicId)).thenReturn(Optional.of(5L));
        when(userRepository.findByUsername(USER_ANALYST1)).thenReturn(Optional.of(
            User.getBuilder().id(7L).username(USER_ANALYST1).role(UserRole.MANAGER).build()));

        workflowEngineService.classify(1L, ClassificationOutcome.REAL, enterprisePublicId, "ok", USER_ANALYST1, null);

        verify(borrowerService, never()).resolveBorrowerForClassification(any(), any());
    }

    @Test
    void shouldRecordClientRibOnEnterpriseWhenProvidedAtClassification() {
        DemandModel pendingDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, null, null, null, null, null, null,
            null, null, null, null, null);
        WorkflowStepInfo classificationStep = new WorkflowStepInfo(1L, 1L, CODE_CLASSIFICATION,
            LABEL_CLASSIFICATION_DU_DOSSIER, CODE_CLASSIFICATION_STEP,
            LABEL_VALIDATION_INITIALE_DU_DOSSIER_AVEC_LE_CLIENT,
            LABEL_CHARGE_D_AFFAIRES, 1, null, null, null, null, null, null, true, false, null, false);
        WorkflowStepInfo initiationStep = new WorkflowStepInfo(2L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);

        UUID enterprisePublicId = UUID.randomUUID();
        when(repository.findDemandById(1L)).thenReturn(Optional.of(pendingDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(classificationStep));
        when(repository.findNextStep(1L, 1)).thenReturn(Optional.of(initiationStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);
        when(enterpriseRepository.findIdByPublicId(enterprisePublicId)).thenReturn(Optional.of(5L));
        when(userRepository.findByUsername(USER_ANALYST1)).thenReturn(Optional.of(
            User.getBuilder().id(7L).username(USER_ANALYST1).role(UserRole.MANAGER).build()));

        workflowEngineService.classify(1L, ClassificationOutcome.REAL, enterprisePublicId, LABEL_CLIENT_CONFIRME,
            USER_ANALYST1, RIB_230_780_0123456789012345_03);

        verify(enterpriseRepository).updateRib(5L, Rib.of(RIB_230_780_0123456789012345_03));
    }

    @Test
    void shouldDelegateDemandContactNotificationWhenClassifiedReal() {
        DemandModel pendingDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, null, EMAIL_CONTACT_AT_ATLAS_MA,
            PHONE_0612345678, null, null, null, null, null, null, null, null);
        WorkflowStepInfo classificationStep = new WorkflowStepInfo(1L, 1L, CODE_CLASSIFICATION,
            LABEL_CLASSIFICATION_DU_DOSSIER, CODE_CLASSIFICATION_STEP,
            LABEL_VALIDATION_INITIALE_DU_DOSSIER_AVEC_LE_CLIENT,
            LABEL_CHARGE_D_AFFAIRES, 1, null, null, null, null, null, null, true, false, null, false);
        WorkflowStepInfo initiationStep = new WorkflowStepInfo(2L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);

        UUID enterprisePublicId = UUID.randomUUID();
        when(repository.findDemandById(1L)).thenReturn(Optional.of(pendingDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(classificationStep));
        when(repository.findNextStep(1L, 1)).thenReturn(Optional.of(initiationStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);
        when(enterpriseRepository.findIdByPublicId(enterprisePublicId)).thenReturn(Optional.of(5L));

        workflowEngineService.classify(1L, ClassificationOutcome.REAL, enterprisePublicId, LABEL_CLIENT_CONFIRME, null,
            null);

        verify(demandContactNotifier).notify(any(DemandModel.class), eq(5L),
            eq(NotificationType.DEMAND_ENTERED_PIPELINE),
            any(LocalizedText.class), any(LocalizedText.class));
    }

    @Test
    void shouldRejectDemandWhenClassifiedFake() {
        DemandModel pendingDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, null, null, null, null, null, null,
            null, null, null, null, null);
        WorkflowStepInfo classificationStep = new WorkflowStepInfo(1L, 1L, CODE_CLASSIFICATION,
            LABEL_CLASSIFICATION_DU_DOSSIER, CODE_CLASSIFICATION_STEP,
            LABEL_VALIDATION_INITIALE_DU_DOSSIER_AVEC_LE_CLIENT,
            LABEL_CHARGE_D_AFFAIRES, 1, null, null, null, null, null, null, true, false, null, false);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(pendingDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(classificationStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        workflowEngineService.classify(1L, ClassificationOutcome.FAKE, null, LABEL_CLIENT_INJOIGNABLE, null, null);

        assertTrue(savedChanges.contains(new DemandChange.ClassificationClosed(ClassificationOutcome.FAKE,
            LABEL_CLIENT_INJOIGNABLE, USER_ADMIN)));
        assertTrue(saved(DemandChange.Rejected.class));
        assertFalse(saved(DemandChange.EnterpriseAttached.class));
        assertFalse(saved(DemandChange.StepOpened.class));
    }

    @Test
    void shouldRejectDemandWhenClassifiedSimulationOnly() {
        DemandModel pendingDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, null, null, null, null, null, null,
            null, null, null, null, null);
        WorkflowStepInfo classificationStep = new WorkflowStepInfo(1L, 1L, CODE_CLASSIFICATION,
            LABEL_CLASSIFICATION_DU_DOSSIER, CODE_CLASSIFICATION_STEP,
            LABEL_VALIDATION_INITIALE_DU_DOSSIER_AVEC_LE_CLIENT,
            LABEL_CHARGE_D_AFFAIRES, 1, null, null, null, null, null, null, true, false, null, false);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(pendingDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(classificationStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        workflowEngineService.classify(1L, ClassificationOutcome.SIMULATION_ONLY, null, null, null, null);

        assertTrue(saved(DemandChange.Rejected.class));
    }

    @Test
    void shouldThrowWhenClassifyingRealWithoutEnterpriseId() {
        DemandModel pendingDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, null, null, null, null, null, null,
            null, null, null, null, null);
        WorkflowStepInfo classificationStep = new WorkflowStepInfo(1L, 1L, CODE_CLASSIFICATION,
            LABEL_CLASSIFICATION_DU_DOSSIER, CODE_CLASSIFICATION_STEP,
            LABEL_VALIDATION_INITIALE_DU_DOSSIER_AVEC_LE_CLIENT,
            LABEL_CHARGE_D_AFFAIRES, 1, null, null, null, null, null, null, true, false, null, false);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(pendingDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(classificationStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        WorkflowException ex = assertThrows(WorkflowException.class, () -> workflowEngineService.classify(1L,
            ClassificationOutcome.REAL, null, null, USER_ANALYST1, null));

        assertEquals(WorkflowExceptionType.ENTERPRISE_REQUIRED_FOR_REAL_CLASSIFICATION, ex.getType());
        assertFalse(saved(DemandChange.ClassificationClosed.class));
    }

    @Test
    void shouldThrowWhenClassifyingRealWithNonexistentEnterprise() {
        DemandModel pendingDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, null, null, null, null, null, null,
            null, null, null, null, null);
        WorkflowStepInfo classificationStep = new WorkflowStepInfo(1L, 1L, CODE_CLASSIFICATION,
            LABEL_CLASSIFICATION_DU_DOSSIER, CODE_CLASSIFICATION_STEP,
            LABEL_VALIDATION_INITIALE_DU_DOSSIER_AVEC_LE_CLIENT,
            LABEL_CHARGE_D_AFFAIRES, 1, null, null, null, null, null, null, true, false, null, false);

        UUID enterprisePublicId = UUID.randomUUID();
        when(repository.findDemandById(1L)).thenReturn(Optional.of(pendingDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(classificationStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);
        when(enterpriseRepository.findIdByPublicId(enterprisePublicId)).thenReturn(Optional.empty());

        assertThrows(com.credit.bladi.exception.EnterpriseException.class, () -> workflowEngineService.classify(1L,
            ClassificationOutcome.REAL, enterprisePublicId, null, USER_ANALYST1, null));

        assertFalse(saved(DemandChange.ClassificationClosed.class));
    }

    @Test
    void shouldThrowWhenClassifyingDemandNotAtClassificationStep() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, null, null, null, null, null, null,
            null, null, null, null, null);
        WorkflowStepInfo initiationStep = new WorkflowStepInfo(2L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(initiationStep));

        WorkflowException ex = assertThrows(WorkflowException.class, () -> workflowEngineService.classify(1L,
            ClassificationOutcome.REAL, UUID.randomUUID(), null, USER_ANALYST1, null));

        assertEquals(WorkflowExceptionType.NOT_AT_CLASSIFICATION_STEP, ex.getType());
    }

    @Test
    void shouldThrowWhenAdvancingDemandStillAtClassificationStep() {
        DemandModel pendingDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, null, null, null, null, null, null,
            null, null, null, null, null);
        WorkflowStepInfo classificationStep = new WorkflowStepInfo(1L, 1L, CODE_CLASSIFICATION,
            LABEL_CLASSIFICATION_DU_DOSSIER, CODE_CLASSIFICATION_STEP,
            LABEL_VALIDATION_INITIALE_DU_DOSSIER_AVEC_LE_CLIENT,
            LABEL_CHARGE_D_AFFAIRES, 1, null, null, null, null, null, null, true, false, null, false);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(pendingDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(classificationStep));

        StepClosureDetails details = new StepClosureDetails(null, null, null, null, null, null, null, null, null,
            KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);

        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> workflowEngineService.advance(1L, details, USER_ANALYST1));

        assertEquals(WorkflowExceptionType.MUST_CLASSIFY_FIRST, ex.getType());
        assertFalse(saved(DemandChange.StepClosed.class));
    }

    @Test
    void shouldKeepDemandsCurrentAmountAndDurationWhenOptionsAreKeep() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo currentStep = new WorkflowStepInfo(1L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);
        WorkflowStepInfo nextStep = new WorkflowStepInfo(2L, 1L, CODE_INSTRUCTION, LABEL_INSTRUCTION,
            CODE_INSTRUCTION_TS_STEP, LABEL_PREPARATION_ET_VALIDATION_DE_LA_TS_INDICATIVE,
            LABEL_CHARGE_D_INVESTISSEMENT_RESPONSABLES_PPIF, 2001,
            null, null, null, null, null, null);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(currentStep));
        when(repository.findNextStep(1L, 1001)).thenReturn(Optional.of(nextStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        StepClosureDetails details = new StepClosureDetails(LABEL_DOSSIER_COMPLET, null, null, null, null, null, null,
            null,
            null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);
        workflowEngineService.advance(1L, details, null);

        assertTrue(termsApplied(snapshot.amount(), 84));
    }

    @Test
    void shouldThrowWhenAmountOptionManualButNoManualAmountGiven() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo currentStep = new WorkflowStepInfo(1L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(currentStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        StepClosureDetails details = new StepClosureDetails(null, null, null, null, null, null, null, null, null,
            KeepOrManualOption.KEEP, null, KeepOrManualOption.MANUAL, null, KeepOrManualOption.KEEP, null);

        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> workflowEngineService.advance(1L, details, null));

        assertEquals(WorkflowExceptionType.MANUAL_AMOUNT_REQUIRED, ex.getType());
        assertFalse(saved(DemandChange.StepClosed.class));
    }

    @Test
    void shouldThrowWhenDurationOptionManualButNoManualDurationGiven() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo currentStep = new WorkflowStepInfo(1L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(currentStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        StepClosureDetails details = new StepClosureDetails(null, null, null, null, null, null, null, null, null,
            KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.MANUAL, null);

        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> workflowEngineService.advance(1L, details, null));

        assertEquals(WorkflowExceptionType.MANUAL_DURATION_REQUIRED, ex.getType());
        assertFalse(saved(DemandChange.StepClosed.class));
    }

    @Test
    void shouldRefuseManualTermsOnceTheCommitteeDecisionIsBehind() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo decisionStep = new WorkflowStepInfo(10L, 1L, CODE_DECISION, LABEL_DECISION_2,
            CODE_DECISION_STEP,
            "Décision comité", "Décideurs", 3008, null, null, null, null, null, null, false, false, null, true);
        WorkflowStepInfo contractStep = new WorkflowStepInfo(13L, 1L, CODE_CONTRACT, "Contrats",
            CODE_CONTRACT_EDITION_STEP,
            LABEL_EDITION_DES_CONTRATS, PERSON_SUPPORT_ADMINISTRATIF, 3011, null, null, null, null, null, null);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(contractStep));
        when(repository.findAllSteps(1L)).thenReturn(List.of(decisionStep, contractStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        StepClosureDetails details = new StepClosureDetails(null, null, null, null, null, null, null, null, null,
            KeepOrManualOption.MANUAL, DECIMAL_0_01, KeepOrManualOption.KEEP, null,
            KeepOrManualOption.KEEP, null);

        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> workflowEngineService.advance(1L, details, null));

        assertEquals(WorkflowExceptionType.TERMS_FROZEN_AFTER_DECISION, ex.getType());
        assertFalse(saved(DemandChange.StepClosed.class));
    }

    @Test
    void shouldThrowWhenAdvancingPastInsuranceStepWithoutAdiSubscribed() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.GE, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6),
            null, null, null, null, null, new Money(BigDecimal.valueOf(1_000_000)), null, null, null, null);
        WorkflowStepInfo currentStep = new WorkflowStepInfo(14L, 6L, CODE_GUARANTEES,
            LABEL_ASSURANCES_ET_GESTION_DES_GARANTIES, CODE_INSURANCE_VALIDATION_STEP,
            LABEL_SOUSCRIPTION_ET_VALIDATION_DES_ASSURANCES, LABEL_GESTIONNAIRE_OGS_GARANTIES, 6001,
            null, null, null, null, null, null, false, true, null, false);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(currentStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);
        doThrow(WorkflowException.of(WorkflowExceptionType.INSURANCE_ADI_REQUIRED))
            .when(eligibilityValidator).assertInsuranceValidated(any(), any());

        StepClosureDetails details = new StepClosureDetails(null, null, null, null, null, null, null, null, null,
            KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);

        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> workflowEngineService.advance(1L, details, USER_GESTIONNAIRE1));

        assertEquals(WorkflowExceptionType.INSURANCE_ADI_REQUIRED, ex.getType());
        assertFalse(saved(DemandChange.StepClosed.class));
    }

    @Test
    void shouldThrowWhenAdvancingPastInsuranceStepWithGuaranteesButNoDamageInsurance() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.GE, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6),
            null, null, null, null, null, new Money(BigDecimal.valueOf(1_000_000)), null, null, null, null);
        WorkflowStepInfo currentStep = new WorkflowStepInfo(14L, 6L, CODE_GUARANTEES,
            LABEL_ASSURANCES_ET_GESTION_DES_GARANTIES, CODE_INSURANCE_VALIDATION_STEP,
            LABEL_SOUSCRIPTION_ET_VALIDATION_DES_ASSURANCES, LABEL_GESTIONNAIRE_OGS_GARANTIES, 6001,
            null, null, null, null, null, null, false, true, null, false);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(currentStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);
        doThrow(WorkflowException.of(WorkflowExceptionType.INSURANCE_DAMAGE_REQUIRED))
            .when(eligibilityValidator).assertInsuranceValidated(any(), any());

        StepClosureDetails details = new StepClosureDetails(null, null, null, null, null, null, true,
            InsuranceCoverageType.BANK_GROUP_POLICY, false,
            KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);

        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> workflowEngineService.advance(1L, details, USER_GESTIONNAIRE1));

        assertEquals(WorkflowExceptionType.INSURANCE_DAMAGE_REQUIRED, ex.getType());
        assertFalse(saved(DemandChange.StepClosed.class));
    }

    @Test
    void shouldAdvancePastInsuranceStepWhenAdiAndDamageInsuranceBothConfirmed() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.GE, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6),
            null, null, null, null, null, new Money(BigDecimal.valueOf(1_000_000)), null, null, null, null);
        WorkflowStepInfo currentStep = new WorkflowStepInfo(14L, 6L, CODE_GUARANTEES,
            LABEL_ASSURANCES_ET_GESTION_DES_GARANTIES, CODE_INSURANCE_VALIDATION_STEP,
            LABEL_SOUSCRIPTION_ET_VALIDATION_DES_ASSURANCES, LABEL_GESTIONNAIRE_OGS_GARANTIES, 6001,
            null, null, null, null, null, null, false, true, null, false);
        WorkflowStepInfo nextStep = new WorkflowStepInfo(15L, 6L, CODE_GUARANTEES,
            LABEL_ASSURANCES_ET_GESTION_DES_GARANTIES, CODE_GUARANTEES_NOTIF_STEP, LABEL_NOTIFICATION_DES_GARANTIES,
            LABEL_GESTIONNAIRE_OGS_GARANTIES, 6002, null, null, null, null, null, null);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(currentStep));
        when(repository.findNextStep(1L, 6001)).thenReturn(Optional.of(nextStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        StepClosureDetails details = new StepClosureDetails(null, null, null, null, null, null, true,
            InsuranceCoverageType.EXTERNAL_DELEGATION, true,
            KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);

        workflowEngineService.advance(1L, details, null);

        assertTrue(stepClosedWith(details));
    }

    @Test
    void shouldReturnFromInsuranceStepWithoutRequiringInsuranceFields() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.GE, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6),
            null, null, null, null, null, new Money(BigDecimal.valueOf(1_000_000)), null, null, null, null);
        WorkflowStepInfo currentStep = new WorkflowStepInfo(14L, 6L, CODE_GUARANTEES,
            LABEL_ASSURANCES_ET_GESTION_DES_GARANTIES, CODE_INSURANCE_VALIDATION_STEP,
            LABEL_SOUSCRIPTION_ET_VALIDATION_DES_ASSURANCES, LABEL_GESTIONNAIRE_OGS_GARANTIES, 6001,
            null, null, null, null, null, null, false, true, null, false);
        WorkflowStepInfo previousStep = new WorkflowStepInfo(13L, 6L, CODE_CONTRACT_EDITION,
            LABEL_EDITION_DES_CONTRATS, CODE_CONTRACT_SIGNATURE_STEP, "Signature des contrats",
            LABEL_CHARGE_D_AFFAIRES, 5002, null, null, null, null, null, null);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(currentStep));
        when(repository.findPreviousStep(1L, 6001)).thenReturn(Optional.of(previousStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        StepClosureDetails details = new StepClosureDetails(null, null, CODE_PIECE_MANQUANTE, null, null, null, null,
            null, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);

        workflowEngineService.advance(1L, details, null);

        assertTrue(stepClosedWith(details));
    }

    @Test
    void shouldReturnToPreviousStepWhenReturnReasonCodeProvided() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo currentStep = new WorkflowStepInfo(6L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE, 3002,
            null, null, null, null, null, null);
        WorkflowStepInfo previousStep = new WorkflowStepInfo(5L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_RECEPTION_STEP, LABEL_RECEPTION_DU_DOSSIER_ET_AFFECTATION,
            LABEL_RESPONSABLE_DEPARTEMENT_DIRECTION_PAE, 3001, null, null, null, null, null, null);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(currentStep));
        when(repository.findPreviousStep(1L, 3002)).thenReturn(Optional.of(previousStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        StepClosureDetails details = new StepClosureDetails("Pièce manquante", null, CODE_PIECE_MANQUANTE, null, null,
            null, null, null, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null,
            KeepOrManualOption.KEEP, null);
        workflowEngineService.advance(1L, details, null);

        assertTrue(stepClosedWith(details));
        assertTrue(stepOpened(5L));
        verify(repository, never()).findNextStep(anyLong(), any());
        assertFalse(saved(DemandChange.Completed.class));
        verify(auditService).log(eq(USER_ADMIN), eq(AuditAction.DEMAND_RETURNED), any(AuditTarget.class), any());
    }

    @Test
    void shouldRequireAssigneeWhenDestinationStepHasARequiredProfile() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo currentStep = new WorkflowStepInfo(1L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);
        WorkflowStepInfo nextStep = new WorkflowStepInfo(6L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE, 3002,
            3L, CODE_ANALYSTE, LABEL_ANALYSTE, 5L, CODE_PAE, CODE_PAE);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(currentStep));
        when(repository.findNextStep(1L, 1001)).thenReturn(Optional.of(nextStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        StepClosureDetails details = new StepClosureDetails(LABEL_DOSSIER_COMPLET, null, null, null, null, null, null,
            null,
            null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);
        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> workflowEngineService.advance(1L, details, null));

        assertEquals(WorkflowExceptionType.NEXT_ASSIGNEE_REQUIRED, ex.getType());
        assertFalse(saved(DemandChange.StepClosed.class));
    }

    @Test
    void shouldRejectIneligibleAssigneeForDestinationStep() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo currentStep = new WorkflowStepInfo(1L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);
        WorkflowStepInfo nextStep = new WorkflowStepInfo(6L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE, 3002,
            3L, CODE_ANALYSTE, LABEL_ANALYSTE, 5L, CODE_PAE, CODE_PAE);
        User actor = User.getBuilder().id(1L).username(USER_ADMIN).role(UserRole.MANAGER).build();
        User ineligibleTarget = User.getBuilder().id(3L).username(USER_SUPPORT1).role(UserRole.MANAGER)
            .gradeId(7L).teamId(5L).build();

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(currentStep));
        when(repository.findNextStep(1L, 1001)).thenReturn(Optional.of(nextStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);
        when(userRepository.findByUsername(USER_ADMIN)).thenReturn(Optional.of(actor));
        when(userRepository.findByUsername(USER_SUPPORT1)).thenReturn(Optional.of(ineligibleTarget));

        StepClosureDetails details = new StepClosureDetails(LABEL_DOSSIER_COMPLET, null, null, null, null, null, null,
            null,
            null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);
        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> workflowEngineService.advance(1L, details, USER_SUPPORT1));

        assertEquals(WorkflowExceptionType.ASSIGNEE_NOT_ELIGIBLE_FOR_STEP, ex.getType());
        assertFalse(saved(DemandChange.StepClosed.class));
    }

    @Test
    void shouldAssignDestinationStepToTheChosenEligibleAssignee() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo currentStep = new WorkflowStepInfo(1L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);
        WorkflowStepInfo nextStep = new WorkflowStepInfo(6L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE, 3002,
            3L, CODE_ANALYSTE, LABEL_ANALYSTE, 5L, CODE_PAE, CODE_PAE);
        User actor = User.getBuilder().id(1L).username(USER_ADMIN).role(UserRole.MANAGER).build();
        User eligibleTarget = User.getBuilder().id(2L).username(USER_ANALYST1).role(UserRole.MANAGER)
            .gradeId(3L).teamId(5L).build();

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(currentStep));
        when(repository.findNextStep(1L, 1001)).thenReturn(Optional.of(nextStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);
        when(userRepository.findByUsername(USER_ADMIN)).thenReturn(Optional.of(actor));
        when(userRepository.findByUsername(USER_ANALYST1)).thenReturn(Optional.of(eligibleTarget));

        StepClosureDetails details = new StepClosureDetails(LABEL_DOSSIER_COMPLET, null, null, null, null, null, null,
            null,
            null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);
        workflowEngineService.advance(1L, details, USER_ANALYST1);

        assertTrue(stepClosedWith(details));
        assertTrue(stepOpened(6L));
        assertTrue(assignedTo(USER_ANALYST1, USER_ADMIN));
    }

    @Test
    void shouldAllowSupervisorToPreassignAnyMemberOfDestinationTeam() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo currentStep = new WorkflowStepInfo(1L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);
        WorkflowStepInfo nextStep = new WorkflowStepInfo(6L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE, 3002,
            3L, CODE_ANALYSTE, LABEL_ANALYSTE, 5L, CODE_PAE, CODE_PAE);
        User supervisor = User.getBuilder().id(9L).username(USER_SUPERVISEUR_PAE).role(UserRole.MANAGER)
            .gradeId(9L).gradeCode(CODE_SUPERVISEUR).teamId(5L).build();
        User standInTarget = User.getBuilder().id(4L).username(USER_SUPPORT1).role(UserRole.MANAGER)
            .gradeId(7L).teamId(5L).build();

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(currentStep));
        when(repository.findNextStep(1L, 1001)).thenReturn(Optional.of(nextStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_SUPERVISEUR_PAE);
        when(userRepository.findByUsername(USER_SUPERVISEUR_PAE)).thenReturn(Optional.of(supervisor));
        when(userRepository.findByUsername(USER_SUPPORT1)).thenReturn(Optional.of(standInTarget));

        StepClosureDetails details = new StepClosureDetails(LABEL_DOSSIER_COMPLET, null, null, null, null, null, null,
            null,
            null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);
        workflowEngineService.advance(1L, details, USER_SUPPORT1);

        assertTrue(assignedTo(USER_SUPPORT1, USER_SUPERVISEUR_PAE));
    }

    @Test
    void shouldThrowWhenReturningForCompletionFromFirstStep() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo firstStep = new WorkflowStepInfo(1L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(firstStep));
        when(repository.findPreviousStep(1L, 1001)).thenReturn(Optional.empty());
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        StepClosureDetails details = new StepClosureDetails(null, null, CODE_PIECE_MANQUANTE, null, null, null, null,
            null,
            null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);
        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> workflowEngineService.advance(1L, details, null));

        assertEquals(WorkflowExceptionType.NO_PREVIOUS_STEP, ex.getType());
        assertFalse(saved(DemandChange.StepOpened.class));
        assertFalse(saved(DemandChange.Completed.class));
    }

    @Test
    void shouldPropagateDecisionOutcomeWhenClosingDecisionStep() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo decisionStep = new WorkflowStepInfo(10L, 1L, CODE_DECISION, LABEL_DECISION,
            CODE_DECISION_STEP, LABEL_DECISION_COMITE_TOURNANT_OU_SEANCE_TENANTE, LABEL_DECIDEURS_DESIGNES_PAE, 4001,
            null, null, null, null, null, null, false, false, NotificationType.DEMAND_IN_DECISION, false);
        WorkflowStepInfo nextStep = new WorkflowStepInfo(11L, 1L, CODE_GUARANTEES, LABEL_GARANTIES,
            CODE_GUARANTEES_NOTIF_STEP, LABEL_NOTIFICATION_DES_GARANTIES, LABEL_GESTIONNAIRE_OGS_GARANTIES, 5001,
            null, null, null, null, null, null);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(decisionStep));
        when(repository.findNextStep(1L, 4001)).thenReturn(Optional.of(nextStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        StepClosureDetails details = new StepClosureDetails("Dossier accordé en comité", DecisionOutcome.APPROVED,
            null,
            "Renforcer le suivi trimestriel",
            "Sous réserve de la garantie hypothécaire",
            null, null, null, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null,
            KeepOrManualOption.KEEP, null);
        workflowEngineService.advance(1L, details, null);

        assertTrue(stepClosedWith(details));
    }

    @Test
    void shouldPropagateCommitteeTypeWhenClosingCommitteeProgrammationStep() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo committeeProgStep = new WorkflowStepInfo(9L, 1L, CODE_RISK_ANALYSIS,
            LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_COMMITTEE_PROG_STEP, LABEL_PROGRAMMATION_DU_COMITE, PERSON_SUPPORT_ADMINISTRATIF, 3005,
            null, null, null, null, null, null);
        WorkflowStepInfo nextStep = new WorkflowStepInfo(10L, 1L, CODE_DECISION, LABEL_DECISION,
            CODE_DECISION_STEP, LABEL_DECISION_COMITE_TOURNANT_OU_SEANCE_TENANTE, LABEL_DECIDEURS_DESIGNES_PAE, 4001,
            null, null, null, null, null, null);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(committeeProgStep));
        when(repository.findNextStep(1L, 3005)).thenReturn(Optional.of(nextStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        StepClosureDetails details = new StepClosureDetails(null, null, null, null, null, CommitteeType.ROTATING, null,
            null, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);
        workflowEngineService.advance(1L, details, null);

        assertTrue(stepClosedWith(details));
    }

    @Test
    void shouldNotifyDemandContactWhenEnteringDecisionStep() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6),
            EMAIL_CONTACT_AT_ATLAS_MA, PHONE_0612345678, null, null, null, null, 5L, null, null, null);
        WorkflowStepInfo committeeProgStep = new WorkflowStepInfo(9L, 1L, CODE_RISK_ANALYSIS,
            LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_COMMITTEE_PROG_STEP, LABEL_PROGRAMMATION_DU_COMITE, PERSON_SUPPORT_ADMINISTRATIF, 3005,
            null, null, null, null, null, null);
        WorkflowStepInfo decisionStep = new WorkflowStepInfo(10L, 1L, CODE_DECISION, LABEL_DECISION,
            CODE_DECISION_STEP, LABEL_DECISION_COMITE_TOURNANT_OU_SEANCE_TENANTE, LABEL_DECIDEURS_DESIGNES_PAE, 4001,
            null, null, null, null, null, null, false, false, NotificationType.DEMAND_IN_DECISION, false);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(committeeProgStep));
        when(repository.findNextStep(1L, 3005)).thenReturn(Optional.of(decisionStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        StepClosureDetails details = new StepClosureDetails(null, null, null, null, null, CommitteeType.ROTATING, null,
            null, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);
        workflowEngineService.advance(1L, details, null);

        verify(demandContactNotifier).notify(any(DemandModel.class), eq(5L), eq(NotificationType.DEMAND_IN_DECISION),
            any(LocalizedText.class), any(LocalizedText.class));
    }

    @Test
    void shouldNotNotifyWhenReturningBackwardLandsOnDecisionStep() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6),
            EMAIL_CONTACT_AT_ATLAS_MA, PHONE_0612345678, null, null, null, null, 5L, null, null, null);
        WorkflowStepInfo guaranteesStep = new WorkflowStepInfo(11L, 1L, CODE_GUARANTEES, LABEL_GARANTIES,
            CODE_GUARANTEES_NOTIF_STEP, LABEL_NOTIFICATION_DES_GARANTIES, LABEL_GESTIONNAIRE_OGS_GARANTIES, 5001,
            null, null, null, null, null, null);
        WorkflowStepInfo decisionStep = new WorkflowStepInfo(10L, 1L, CODE_DECISION, LABEL_DECISION,
            CODE_DECISION_STEP, LABEL_DECISION_COMITE_TOURNANT_OU_SEANCE_TENANTE, LABEL_DECIDEURS_DESIGNES_PAE, 4001,
            null, null, null, null, null, null, false, false, NotificationType.DEMAND_IN_DECISION, false);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(guaranteesStep));
        when(repository.findPreviousStep(1L, 5001)).thenReturn(Optional.of(decisionStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        StepClosureDetails details = new StepClosureDetails(null, null, CODE_PIECE_MANQUANTE, null, null, null, null,
            null,
            null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);
        workflowEngineService.advance(1L, details, null);

        verify(demandContactNotifier, never()).notify(any(), any(), any(), any(), any());
    }

    @Test
    void shouldBlockAdvanceWhenCurrentStepRequiresClassificationRegardlessOfStepCode() {
        DemandModel pendingDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL, EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, null, null,
            null,
            null, null, null, null, null, null, null, null);
        WorkflowStepInfo customGateStep = new WorkflowStepInfo(1L, 1L, CODE_CUSTOM_PROCESS, LABEL_CUSTOM_PROCESS,
            CODE_SOME_OTHER_STEP_CODE, "A future step reusing the classification gate", LABEL_CHARGE_D_AFFAIRES, 1,
            null, null, null, null, null, null, true, false, null, false);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(pendingDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(customGateStep));

        StepClosureDetails details = new StepClosureDetails(null, null, null, null, null, null, null, null, null,
            KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);

        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> workflowEngineService.advance(1L, details, USER_ANALYST1));

        assertEquals(WorkflowExceptionType.MUST_CLASSIFY_FIRST, ex.getType());
    }

    @Test
    void shouldRequireInsuranceConfirmationRegardlessOfStepCode() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL, EnterpriseType.GE, snapshot.amount(), 84, Instant.now(), null,
            new Rate(AMOUNT_6), null, null, null, null, null,
            new Money(BigDecimal.valueOf(1_000_000)), null, null, null, null);
        WorkflowStepInfo customInsuranceStep = new WorkflowStepInfo(14L, 6L, CODE_CUSTOM_PROCESS, LABEL_CUSTOM_PROCESS,
            CODE_SOME_OTHER_INSURANCE_STEP_CODE, "A future step reusing the insurance gate",
            LABEL_GESTIONNAIRE_OGS_GARANTIES, 6001, null, null, null, null, null, null, false, true, null, false);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(customInsuranceStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);
        doThrow(WorkflowException.of(WorkflowExceptionType.INSURANCE_ADI_REQUIRED))
            .when(eligibilityValidator).assertInsuranceValidated(any(), any());

        StepClosureDetails details = new StepClosureDetails(null, null, null, null, null, null, null, null, null,
            KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);

        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> workflowEngineService.advance(1L, details, USER_GESTIONNAIRE1));

        assertEquals(WorkflowExceptionType.INSURANCE_ADI_REQUIRED, ex.getType());
        verify(eligibilityValidator).assertInsuranceValidated(any(), any());
    }

    @Test
    void shouldNotifyContactOnEnterForAnyStepConfiguredWithNotificationType() {
        DemandModel activeDemand = MockDemand.mortgage(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 3L,
            PERSON_JEAN_DUPONT,
            Money.of(AMOUNT_500000), 240, Instant.now(), null, new Rate(DECIMAL_5_25),
            EMAIL_JEAN_AT_EXAMPLE_COM, PHONE_0612345678, null, ProductType.CONVENTIONAL, CspCode.SALA,
            Money.of(AMOUNT_600000), Money.of(AMOUNT_20000), 35, false, null);
        WorkflowStepInfo instructionStep = new WorkflowStepInfo(17L, 3L, CODE_MORTGAGE_INSTRUCTION, LABEL_INSTRUCTION,
            CODE_MORTGAGE_INSTRUCTION_STEP, "Étude du dossier et calcul définitif", "Chargé de clientèle", 2001,
            null, null, null, null, null, null);
        WorkflowStepInfo mortgageDecisionStep = new WorkflowStepInfo(18L, 3L, CODE_MORTGAGE_DECISION, LABEL_DECISION_2,
            CODE_MORTGAGE_DECISION_STEP, "Décision agence/direction", LABEL_DIRECTEUR_D_AGENCE, 3001,
            null, null, null, null, null, null, false, false, NotificationType.DEMAND_IN_DECISION, false);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(instructionStep));
        when(repository.findNextStep(3L, 2001)).thenReturn(Optional.of(mortgageDecisionStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        StepClosureDetails details = new StepClosureDetails(null, null, null, null, null, null, null, null, null,
            KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);
        workflowEngineService.advance(1L, details, null);

        verify(demandContactNotifier).notify(any(DemandModel.class), isNull(), eq(NotificationType.DEMAND_IN_DECISION),
            any(LocalizedText.class), any(LocalizedText.class));
    }

    @Test
    void shouldSkipNotificationWhenConfiguredTypeHasNoMessageMapped() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL, EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null,
            new Rate(AMOUNT_6), EMAIL_CONTACT_AT_ATLAS_MA, PHONE_0612345678, null, null, null, null, 5L,
            null,
            null, null);
        WorkflowStepInfo currentStep = new WorkflowStepInfo(1L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);
        WorkflowStepInfo unmappedNotifyStep = new WorkflowStepInfo(2L, 1L, CODE_INSTRUCTION, LABEL_INSTRUCTION,
            CODE_INSTRUCTION_TS_STEP, LABEL_PREPARATION_ET_VALIDATION_DE_LA_TS_INDICATIVE,
            LABEL_CHARGE_D_INVESTISSEMENT_RESPONSABLES_PPIF, 2001, null, null, null, null, null, null, false, false,
            NotificationType.PAYMENT_RECEIVED, false);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(currentStep));
        when(repository.findNextStep(1L, 1001)).thenReturn(Optional.of(unmappedNotifyStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        StepClosureDetails details = new StepClosureDetails(null, null, null, null, null, null, null, null, null,
            KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);

        workflowEngineService.advance(1L, details, null);

        verify(demandContactNotifier, never()).notify(any(), any(), any(), any(), any());
        assertTrue(stepClosedWith(details));
    }

    @Test
    void shouldCompleteDemandWhenAdvancingPastLastStep() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo lastStep = new WorkflowStepInfo(12L, 1L, CODE_GUARANTEES, LABEL_GARANTIES,
            CODE_GUARANTEES_MISE_EN_GESTION_STEP, LABEL_MISE_EN_GESTION_DU_DOSSIER,
            LABEL_GESTIONNAIRE_OGS_MISE_EN_GESTION, 5002, null, null, null, null, null, null);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(lastStep));
        when(repository.findNextStep(1L, 5002)).thenReturn(Optional.empty());
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        workflowEngineService.advance(1L,
            new StepClosureDetails(null, null, null, null, null, null, null, null, null, KeepOrManualOption.KEEP, null,
                KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null), null);

        assertTrue(saved(DemandChange.Completed.class));
        assertFalse(saved(DemandChange.StepOpened.class));
    }

    @Test
    void shouldNotifyDemandContactWhenDemandCompleted() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6),
            EMAIL_CONTACT_AT_ATLAS_MA, PHONE_0612345678, null, null, null, null, 5L, null, null, null);
        WorkflowStepInfo lastStep = new WorkflowStepInfo(12L, 1L, CODE_GUARANTEES, LABEL_GARANTIES,
            CODE_GUARANTEES_MISE_EN_GESTION_STEP, LABEL_MISE_EN_GESTION_DU_DOSSIER,
            LABEL_GESTIONNAIRE_OGS_MISE_EN_GESTION, 5002, null, null, null, null, null, null);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(lastStep));
        when(repository.findNextStep(1L, 5002)).thenReturn(Optional.empty());
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        workflowEngineService.advance(1L,
            new StepClosureDetails(null, null, null, null, null, null, null, null, null, KeepOrManualOption.KEEP, null,
                KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null), null);

        verify(demandContactNotifier).notify(any(DemandModel.class), eq(5L), eq(NotificationType.DEMAND_COMPLETED),
            any(LocalizedText.class), any(LocalizedText.class));
    }

    @Test
    void shouldPublishDemandCompletedEventWhenAdvancingPastLastStep() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo lastStep = new WorkflowStepInfo(12L, 1L, CODE_GUARANTEES, LABEL_GARANTIES,
            CODE_GUARANTEES_MISE_EN_GESTION_STEP, LABEL_MISE_EN_GESTION_DU_DOSSIER,
            LABEL_GESTIONNAIRE_OGS_MISE_EN_GESTION, 5002, null, null, null, null, null, null);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(lastStep));
        when(repository.findNextStep(1L, 5002)).thenReturn(Optional.empty());
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        workflowEngineService.advance(1L,
            new StepClosureDetails(null, null, null, null, null, null, null, null, null, KeepOrManualOption.KEEP, null,
                KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null), null);

        verify(eventPublisher).publishEvent(new DemandCompletedEvent(1L, USER_ADMIN));
    }

    @Test
    void shouldNotPublishDemandCompletedEventWhenAdvancingToAnIntermediateStep() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo currentStep = new WorkflowStepInfo(1L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);
        WorkflowStepInfo nextStep = new WorkflowStepInfo(2L, 1L, CODE_INSTRUCTION, LABEL_INSTRUCTION,
            CODE_INSTRUCTION_TS_STEP, LABEL_PREPARATION_ET_VALIDATION_DE_LA_TS_INDICATIVE,
            LABEL_CHARGE_D_INVESTISSEMENT_RESPONSABLES_PPIF, 2001,
            null, null, null, null, null, null);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(currentStep));
        when(repository.findNextStep(1L, 1001)).thenReturn(Optional.of(nextStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);

        StepClosureDetails details = new StepClosureDetails(LABEL_DOSSIER_COMPLET, null, null, null, null, null, null,
            null,
            null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);
        workflowEngineService.advance(1L, details, null);

        verify(eventPublisher, never()).publishEvent(any(DemandCompletedEvent.class));
    }

    @Test
    void shouldRejectAdvanceOnAlreadyCompletedDemand() {
        DemandModel completedDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), Instant.now(), null, null, null, null, null, null,
                null, null, null, null, null);
        when(repository.findDemandById(1L)).thenReturn(Optional.of(completedDemand));

        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> workflowEngineService.advance(1L,
                new StepClosureDetails("note", null, null, null, null, null, null, null, null, KeepOrManualOption.KEEP,
                    null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null), null));

        assertEquals(WorkflowExceptionType.NO_NEXT_STEP, ex.getType());
        assertFalse(saved(DemandChange.StepClosed.class));
    }

    @Test
    void shouldThrowWhenDemandNotFound() {
        when(repository.findDemandById(99L)).thenReturn(Optional.empty());

        WorkflowException ex = assertThrows(WorkflowException.class, () -> workflowEngineService.getDemand(99L));

        assertEquals(WorkflowExceptionType.DEMAND_NOT_FOUND, ex.getType());
    }

    @Test
    void shouldRejectAdvanceWhenActorProfileDoesNotMatchRequiredStep() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo analysisStep = new WorkflowStepInfo(6L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE, 3002,
            3L, CODE_ANALYSTE, LABEL_ANALYSTE, 5L, CODE_PAE, CODE_PAE);
        User wrongGradeActor = User.getBuilder().id(1L).username(USER_RESP_DEPT).role(UserRole.MANAGER)
            .gradeId(4L).teamId(5L).build();

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(analysisStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_RESP_DEPT);
        when(userRepository.findByUsername(USER_RESP_DEPT)).thenReturn(Optional.of(wrongGradeActor));

        WorkflowException ex = assertThrows(WorkflowException.class, () -> workflowEngineService.advance(1L,
            new StepClosureDetails(null, null, null, null, null, null, null, null, null, KeepOrManualOption.KEEP, null,
                KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null), null));

        assertEquals(WorkflowExceptionType.ACTOR_NOT_ELIGIBLE_FOR_STEP, ex.getType());
        assertFalse(saved(DemandChange.StepClosed.class));
    }

    @Test
    void shouldAllowAdvanceWhenActorHasNoProfileConfigured() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo analysisStep = new WorkflowStepInfo(6L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE, 3002,
            3L, CODE_ANALYSTE, LABEL_ANALYSTE, 5L, CODE_PAE, CODE_PAE);
        WorkflowStepInfo nextStep = new WorkflowStepInfo(7L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_VALIDATION_DEPT_STEP, LABEL_VALIDATION_DE_LA_CONTRE_ANALYSE, LABEL_RESPONSABLE_DEPARTEMENT_PAE,
            3003,
            null, null, null, null, null, null);
        User unprofiledActor = User.getBuilder().id(1L).username(USER_ADMIN).role(UserRole.MANAGER).build();

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(analysisStep));
        when(repository.findNextStep(1L, 3002)).thenReturn(Optional.of(nextStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);
        when(userRepository.findByUsername(USER_ADMIN)).thenReturn(Optional.of(unprofiledActor));

        workflowEngineService.advance(1L,
            new StepClosureDetails(null, null, null, null, null, null, null, null, null, KeepOrManualOption.KEEP, null,
                KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null), null);

        assertTrue(saved(DemandChange.StepClosed.class));
        assertTrue(stepOpened(7L));
    }

    @Test
    void shouldAllowAdvanceWhenActorProfileMatchesRequiredStep() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo analysisStep = new WorkflowStepInfo(6L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE, 3002,
            3L, CODE_ANALYSTE, LABEL_ANALYSTE, 5L, CODE_PAE, CODE_PAE);
        WorkflowStepInfo nextStep = new WorkflowStepInfo(7L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_VALIDATION_DEPT_STEP, LABEL_VALIDATION_DE_LA_CONTRE_ANALYSE, LABEL_RESPONSABLE_DEPARTEMENT_PAE,
            3003,
            null, null, null, null, null, null);
        User matchingActor = User.getBuilder().id(1L).username(USER_ANALYST1).role(UserRole.MANAGER)
            .gradeId(3L).teamId(5L).build();

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(analysisStep));
        when(repository.findNextStep(1L, 3002)).thenReturn(Optional.of(nextStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ANALYST1);
        when(userRepository.findByUsername(USER_ANALYST1)).thenReturn(Optional.of(matchingActor));

        workflowEngineService.advance(1L,
            new StepClosureDetails(null, null, null, null, null, null, null, null, null, KeepOrManualOption.KEEP, null,
                KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null), null);

        assertTrue(saved(DemandChange.StepClosed.class));
        assertTrue(stepOpened(7L));
    }

    @Test
    void shouldRejectAdvanceWhenStepIsAssignedToSomeoneElse() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo analysisStep = new WorkflowStepInfo(6L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE, 3002,
            3L, CODE_ANALYSTE, LABEL_ANALYSTE, 5L, CODE_PAE, CODE_PAE);
        DemandStepModel assignedStep = new DemandStepModel(CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE,
            CODE_ANALYSTE, LABEL_ANALYSTE, CODE_PAE, CODE_PAE, USER_ANALYST2, USER_RESP_DEPT, Instant.now());

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(analysisStep));
        when(repository.findCurrentStep(1L)).thenReturn(Optional.of(assignedStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ANALYST1);

        WorkflowException ex = assertThrows(WorkflowException.class, () -> workflowEngineService.advance(1L,
            new StepClosureDetails(null, null, null, null, null, null, null, null, null, KeepOrManualOption.KEEP, null,
                KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null), null));

        assertEquals(WorkflowExceptionType.ACTOR_NOT_ASSIGNED_TO_STEP, ex.getType());
        assertFalse(saved(DemandChange.StepClosed.class));
        verify(userRepository, never()).findByUsername(USER_ANALYST1);
    }

    @Test
    void shouldAllowAdvanceWhenActorIsTheAssignedUser() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo analysisStep = new WorkflowStepInfo(6L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE, 3002,
            3L, CODE_ANALYSTE, LABEL_ANALYSTE, 5L, CODE_PAE, CODE_PAE);
        WorkflowStepInfo nextStep = new WorkflowStepInfo(7L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_VALIDATION_DEPT_STEP, LABEL_VALIDATION_DE_LA_CONTRE_ANALYSE, LABEL_RESPONSABLE_DEPARTEMENT_PAE,
            3003,
            null, null, null, null, null, null);
        DemandStepModel assignedStep = new DemandStepModel(CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE,
            CODE_ANALYSTE, LABEL_ANALYSTE, CODE_PAE, CODE_PAE, USER_ANALYST2, USER_RESP_DEPT, Instant.now());

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(analysisStep));
        when(repository.findCurrentStep(1L)).thenReturn(Optional.of(assignedStep));
        when(repository.findNextStep(1L, 3002)).thenReturn(Optional.of(nextStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ANALYST2);

        workflowEngineService.advance(1L,
            new StepClosureDetails(null, null, null, null, null, null, null, null, null, KeepOrManualOption.KEEP, null,
                KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null), null);

        assertTrue(saved(DemandChange.StepClosed.class));
        verify(userRepository, never()).findByUsername(any());
    }

    @Test
    void shouldAssignDemandToEligibleUser() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo analysisStep = new WorkflowStepInfo(6L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE, 3002,
            3L, CODE_ANALYSTE, LABEL_ANALYSTE, 5L, CODE_PAE, CODE_PAE);
        User assigner = User.getBuilder().id(1L).username(USER_RESP_DEPT).role(UserRole.MANAGER)
            .gradeId(4L).teamId(5L).build();
        User target = User.getBuilder().id(2L).username(USER_ANALYST2).role(UserRole.MANAGER)
            .gradeId(3L).teamId(5L).build();
        DemandStepModel updatedStep = new DemandStepModel(CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE,
            CODE_ANALYSTE, LABEL_ANALYSTE, CODE_PAE, CODE_PAE, USER_ANALYST2, USER_RESP_DEPT, Instant.now());

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(analysisStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_RESP_DEPT);
        when(userRepository.findByUsername(USER_RESP_DEPT)).thenReturn(Optional.of(assigner));
        when(userRepository.findByUsername(USER_ANALYST2)).thenReturn(Optional.of(target));
        when(repository.findCurrentStep(1L)).thenReturn(Optional.of(updatedStep));

        DemandStepModel result = workflowEngineService.assign(1L, USER_ANALYST2);

        assertEquals(USER_ANALYST2, result.assignedUsername());
        assertEquals(USER_RESP_DEPT, result.assignedBy());
        assertTrue(assignedTo(USER_ANALYST2, USER_RESP_DEPT));
        verify(auditService).log(eq(USER_RESP_DEPT), eq(AuditAction.DEMAND_ASSIGNED), any(AuditTarget.class), any());
    }

    @Test
    void shouldRejectAssignWhenAssignerNotInRequiredTeam() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo analysisStep = new WorkflowStepInfo(6L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE, 3002,
            3L, CODE_ANALYSTE, LABEL_ANALYSTE, 5L, CODE_PAE, CODE_PAE);
        User outsideAssigner = User.getBuilder().id(1L).username(USER_PPIF_USER).role(UserRole.MANAGER)
            .gradeId(4L).teamId(2L).build();

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(analysisStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_PPIF_USER);
        when(userRepository.findByUsername(USER_PPIF_USER)).thenReturn(Optional.of(outsideAssigner));

        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> workflowEngineService.assign(1L, USER_ANALYST2));

        assertEquals(WorkflowExceptionType.ASSIGNER_NOT_ELIGIBLE_FOR_STEP, ex.getType());
        assertFalse(saved(DemandChange.Reassigned.class) || saved(DemandChange.StepOpened.class));
    }

    @Test
    void shouldRejectAssignWhenAssignerHasNoTeamAtAll() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null,
            null, null, null, null, null, null, null, null, null);
        WorkflowStepInfo analysisStep = new WorkflowStepInfo(6L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE, 3002,
            3L, CODE_ANALYSTE, LABEL_ANALYSTE, 5L, CODE_PAE, CODE_PAE);
        User teamlessAssigner = User.getBuilder().id(1L).username(USER_ADMIN).role(UserRole.MANAGER).build();

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(analysisStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);
        when(userRepository.findByUsername(USER_ADMIN)).thenReturn(Optional.of(teamlessAssigner));

        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> workflowEngineService.assign(1L, USER_ANALYST2));

        assertEquals(WorkflowExceptionType.ASSIGNER_NOT_ELIGIBLE_FOR_STEP, ex.getType());
        assertFalse(saved(DemandChange.Reassigned.class) || saved(DemandChange.StepOpened.class));
    }

    @Test
    void shouldAllowSupervisorToAssignToAnyTeamMemberRegardlessOfGrade() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo analysisStep = new WorkflowStepInfo(6L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE, 3002,
            3L, CODE_ANALYSTE, LABEL_ANALYSTE, 5L, CODE_PAE, CODE_PAE);
        User supervisor = User.getBuilder().id(9L).username(USER_SUPERVISEUR_PAE).role(UserRole.MANAGER)
            .gradeId(9L).gradeCode(CODE_SUPERVISEUR).teamId(5L).build();
        User standInTarget = User.getBuilder().id(4L).username(USER_SUPPORT1).role(UserRole.MANAGER)
            .gradeId(7L).teamId(5L).build();
        DemandStepModel updatedStep = new DemandStepModel(CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE,
            CODE_ANALYSTE, LABEL_ANALYSTE, CODE_PAE, CODE_PAE, USER_SUPPORT1, USER_SUPERVISEUR_PAE, Instant.now());

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(analysisStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_SUPERVISEUR_PAE);
        when(userRepository.findByUsername(USER_SUPERVISEUR_PAE)).thenReturn(Optional.of(supervisor));
        when(userRepository.findByUsername(USER_SUPPORT1)).thenReturn(Optional.of(standInTarget));
        when(repository.findCurrentStep(1L)).thenReturn(Optional.of(updatedStep));

        DemandStepModel result = workflowEngineService.assign(1L, USER_SUPPORT1);

        assertEquals(USER_SUPPORT1, result.assignedUsername());
        assertTrue(assignedTo(USER_SUPPORT1, USER_SUPERVISEUR_PAE));
    }

    @Test
    void shouldRejectSupervisorAssignerWhenTheirTeamDoesNotMatchTheRequiredTeam() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo analysisStep = new WorkflowStepInfo(6L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE, 3002,
            3L, CODE_ANALYSTE, LABEL_ANALYSTE, 5L, CODE_PAE, CODE_PAE);
        User otherTeamSupervisor = User.getBuilder().id(9L).username(USER_SUPERVISEUR_OGS).role(UserRole.MANAGER)
            .gradeId(9L).gradeCode(CODE_SUPERVISEUR).teamId(6L).build();

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(analysisStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_SUPERVISEUR_OGS);
        when(userRepository.findByUsername(USER_SUPERVISEUR_OGS)).thenReturn(Optional.of(otherTeamSupervisor));

        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> workflowEngineService.assign(1L, USER_ANALYST2));

        assertEquals(WorkflowExceptionType.ASSIGNER_NOT_ELIGIBLE_FOR_STEP, ex.getType());
        assertFalse(saved(DemandChange.Reassigned.class) || saved(DemandChange.StepOpened.class));
    }

    @Test
    void shouldRejectAssignWhenTargetNotEligible() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo analysisStep = new WorkflowStepInfo(6L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE, 3002,
            3L, CODE_ANALYSTE, LABEL_ANALYSTE, 5L, CODE_PAE, CODE_PAE);
        User assigner = User.getBuilder().id(1L).username(USER_RESP_DEPT).role(UserRole.MANAGER)
            .gradeId(4L).teamId(5L).build();
        User ineligibleTarget = User.getBuilder().id(3L).username(USER_SUPPORT1).role(UserRole.MANAGER)
            .gradeId(7L).teamId(5L).build();

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(analysisStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_RESP_DEPT);
        when(userRepository.findByUsername(USER_RESP_DEPT)).thenReturn(Optional.of(assigner));
        when(userRepository.findByUsername(USER_SUPPORT1)).thenReturn(Optional.of(ineligibleTarget));

        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> workflowEngineService.assign(1L, USER_SUPPORT1));

        assertEquals(WorkflowExceptionType.ASSIGNEE_NOT_ELIGIBLE_FOR_STEP, ex.getType());
        assertFalse(saved(DemandChange.Reassigned.class) || saved(DemandChange.StepOpened.class));
    }

    @Test
    void shouldReturnEmptyEligibleAssigneesWhenDemandIsCompleted() {
        DemandModel completedDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), Instant.now(), null, null, null, null, null, null,
                null, null, null, null, null);
        when(repository.findDemandById(1L)).thenReturn(Optional.of(completedDemand));

        List<User> result = workflowEngineService.getEligibleAssignees(1L, StepDirection.CURRENT);

        assertEquals(0, result.size());
        verify(repository, never()).findCurrentStepInfo(anyLong());
    }

    @Test
    void shouldReturnEmptyEligibleAssigneesWhenStepHasNoRequiredProfile() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo unconfiguredStep = new WorkflowStepInfo(1L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(unconfiguredStep));

        List<User> result = workflowEngineService.getEligibleAssignees(1L, StepDirection.CURRENT);

        assertEquals(0, result.size());
        verify(userRepository, never()).findManagersByGradeIdAndTeamId(any(), any());
    }

    @Test
    void shouldDelegateEligibleAssigneesToRepositoryWithRequiredGradeAndTeam() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo analysisStep = new WorkflowStepInfo(6L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE, 3002,
            3L, CODE_ANALYSTE, LABEL_ANALYSTE, 5L, CODE_PAE, CODE_PAE);
        User eligibleUser = User.getBuilder().id(2L).username(USER_ANALYST2).role(UserRole.MANAGER)
            .gradeId(3L).teamId(5L).build();

        User actor = User.getBuilder().id(1L).username(USER_RESP_DEPT).role(UserRole.MANAGER)
            .gradeId(4L).teamId(5L).build();

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(analysisStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_RESP_DEPT);
        when(userRepository.findByUsername(USER_RESP_DEPT)).thenReturn(Optional.of(actor));
        when(userRepository.findManagersByGradeIdAndTeamId(3L, 5L)).thenReturn(List.of(eligibleUser));

        List<User> result = workflowEngineService.getEligibleAssignees(1L, StepDirection.CURRENT);

        assertEquals(1, result.size());
        assertEquals(USER_ANALYST2, result.get(0).getUsername());
    }

    @Test
    void shouldReturnWholeTeamRosterWhenActorIsSupervisorOfRequiredTeam() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo analysisStep = new WorkflowStepInfo(6L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE, 3002,
            3L, CODE_ANALYSTE, LABEL_ANALYSTE, 5L, CODE_PAE, CODE_PAE);
        User supervisor = User.getBuilder().id(9L).username(USER_SUPERVISEUR_PAE).role(UserRole.MANAGER)
            .gradeId(9L).gradeCode(CODE_SUPERVISEUR).teamId(5L).build();
        User anyTeamMember = User.getBuilder().id(4L).username(USER_SUPPORT1).role(UserRole.MANAGER)
            .gradeId(7L).teamId(5L).build();

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(analysisStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_SUPERVISEUR_PAE);
        when(userRepository.findByUsername(USER_SUPERVISEUR_PAE)).thenReturn(Optional.of(supervisor));
        when(userRepository.findManagersByTeamId(5L)).thenReturn(List.of(anyTeamMember));

        List<User> result = workflowEngineService.getEligibleAssignees(1L, StepDirection.CURRENT);

        assertEquals(1, result.size());
        assertEquals(USER_SUPPORT1, result.get(0).getUsername());
        verify(userRepository, never()).findManagersByGradeIdAndTeamId(anyLong(), anyLong());
    }

    @Test
    void shouldReturnEligibleAssigneesForTheNextStepWhenDirectionIsNext() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo currentStep = new WorkflowStepInfo(1L, 1L, CODE_INITIATION, LABEL_INITIATION_DU_DOSSIER,
            CODE_INITIATION_STEP, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES, LABEL_CHARGE_D_AFFAIRES, 1001,
            null, null, null, null, null, null);
        WorkflowStepInfo nextStep = new WorkflowStepInfo(6L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE, 3002,
            3L, CODE_ANALYSTE, LABEL_ANALYSTE, 5L, CODE_PAE, CODE_PAE);
        User actor = User.getBuilder().id(1L).username(USER_ADMIN).role(UserRole.MANAGER).build();
        User eligibleUser = User.getBuilder().id(2L).username(USER_ANALYST1).role(UserRole.MANAGER)
            .gradeId(3L).teamId(5L).build();

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(currentStep));
        when(repository.findNextStep(1L, 1001)).thenReturn(Optional.of(nextStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);
        when(userRepository.findByUsername(USER_ADMIN)).thenReturn(Optional.of(actor));
        when(userRepository.findManagersByGradeIdAndTeamId(3L, 5L)).thenReturn(List.of(eligibleUser));

        List<User> result = workflowEngineService.getEligibleAssignees(1L, StepDirection.NEXT);

        assertEquals(1, result.size());
        assertEquals(USER_ANALYST1, result.get(0).getUsername());
    }

    @Test
    void shouldReturnEmptyWhenDirectionIsNextAndAlreadyAtTheLastStep() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo lastStep = new WorkflowStepInfo(12L, 1L, CODE_GUARANTEES, LABEL_GARANTIES,
            CODE_GUARANTEES_MISE_EN_GESTION_STEP, LABEL_MISE_EN_GESTION_DU_DOSSIER,
            LABEL_GESTIONNAIRE_OGS_MISE_EN_GESTION, 5002, 10L, CODE_GESTIONNAIRE, "Gestionnaire", 7L, CODE_OGS_MEG,
            "OGS Mise en gestion");

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(lastStep));
        when(repository.findNextStep(1L, 5002)).thenReturn(Optional.empty());

        List<User> result = workflowEngineService.getEligibleAssignees(1L, StepDirection.NEXT);

        assertEquals(0, result.size());
    }

    @Test
    void shouldReturnEligibleAssigneesForThePreviousStepWhenDirectionIsPrevious() {
        DemandModel activeDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), null, new Rate(AMOUNT_6), null, null,
                null, null, null, null, null, null, null, null);
        WorkflowStepInfo currentStep = new WorkflowStepInfo(6L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_ANALYSIS_STEP, LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE, 3002,
            3L, CODE_ANALYSTE, LABEL_ANALYSTE, 5L, CODE_PAE, CODE_PAE);
        WorkflowStepInfo previousStep = new WorkflowStepInfo(5L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE,
            CODE_RISK_RECEPTION_STEP, LABEL_RECEPTION_DU_DOSSIER_ET_AFFECTATION,
            LABEL_RESPONSABLE_DEPARTEMENT_DIRECTION_PAE, 3001, 4L, CODE_RESP_DEPT, PERSON_RESPONSABLE_DEPARTEMENT,
            5L, CODE_PAE, CODE_PAE);
        User actor = User.getBuilder().id(1L).username(USER_ADMIN).role(UserRole.MANAGER).build();
        User eligibleUser = User.getBuilder().id(2L).username(USER_RESP_DEPT).role(UserRole.MANAGER)
            .gradeId(4L).teamId(5L).build();

        when(repository.findDemandById(1L)).thenReturn(Optional.of(activeDemand));
        when(repository.findCurrentStepInfo(1L)).thenReturn(Optional.of(currentStep));
        when(repository.findPreviousStep(1L, 3002)).thenReturn(Optional.of(previousStep));
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_ADMIN);
        when(userRepository.findByUsername(USER_ADMIN)).thenReturn(Optional.of(actor));
        when(userRepository.findManagersByGradeIdAndTeamId(4L, 5L)).thenReturn(List.of(eligibleUser));

        List<User> result = workflowEngineService.getEligibleAssignees(1L, StepDirection.PREVIOUS);

        assertEquals(1, result.size());
        assertEquals(USER_RESP_DEPT, result.get(0).getUsername());
    }

    @Test
    void isCompletedShouldReflectCompletedAtPresence() {
        DemandModel active = MockDemand.enterprise(1L, UUID.randomUUID(), LABEL_DEM_1, 1L, "X", EnterpriseType.GE,
            Money.of(BigDecimal.TEN), 12, Instant.now(), null, new Rate(AMOUNT_6), null, null, null, null,
                null, null, null, null, null, null);
        DemandModel completed = MockDemand.enterprise(1L, UUID.randomUUID(), LABEL_DEM_1, 1L, "X", EnterpriseType.GE,
            Money.of(BigDecimal.TEN), 12, Instant.now(), Instant.now(), null, null, null, null, null, null, null, null,
                null, null, null);

        assertFalse(active.isCompleted());
        assertTrue(completed.isCompleted());
    }

    @Test
    void shouldUseLastVisitedStepAsCurrentStepOfACompletedDemand() {
        DemandModel completedDemand = MockDemand.enterprise(1L, UUID.randomUUID(), REFERENCE_DEM_ABCDEF12, 1L,
            COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, snapshot.amount(), 84, Instant.now(), Instant.now(), null, null, null, null, null, null,
                null, null, null, null, null);
        DemandHistoryEntryModel lastEntry = new DemandHistoryEntryModel(CODE_GUARANTEES, LABEL_GARANTIES,
            CODE_GUARANTEES_MISE_EN_GESTION_STEP, LABEL_MISE_EN_GESTION_DU_DOSSIER,
            LABEL_GESTIONNAIRE_OGS_MISE_EN_GESTION, null,
            null, null, null, USER_ADMIN, null, Instant.now(), Instant.now(), null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null);

        when(repository.findDemandById(1L)).thenReturn(Optional.of(completedDemand));
        when(repository.findCurrentStep(1L)).thenReturn(Optional.empty());
        when(repository.findHistory(1L)).thenReturn(List.of(lastEntry));

        DemandStepModel result = workflowEngineService.getCurrentStep(1L);

        assertEquals(CODE_GUARANTEES_MISE_EN_GESTION_STEP, result.stepCode());
        assertEquals(LABEL_MISE_EN_GESTION_DU_DOSSIER, result.stepName());
    }

    @Test
    void shouldReturnCurrentStepSummariesFromRepositoryInOneBatchedCall() {
        List<Long> demandIds = List.of(1L, 2L);
        Map<Long, DemandStepSummary> summaries = Map.of(
            1L, new DemandStepSummary(LABEL_ANALYSE_CONTRE_RISQUE, LABEL_TRAITEMENT_ANALYSE_CONTRE_RISQUE),
            2L, new DemandStepSummary(LABEL_INITIATION_DU_DOSSIER, LABEL_CREATION_DU_DOSSIER_ET_OUVERTURE_DES_LIGNES));

        when(repository.findCurrentStepSummaries(demandIds)).thenReturn(summaries);

        Map<Long, DemandStepSummary> result = workflowEngineService.getCurrentStepSummaries(demandIds);

        assertEquals(summaries, result);
        verify(repository, times(1)).findCurrentStepSummaries(demandIds);
    }

    private boolean saved(Class<? extends DemandChange> type) {
        return savedChanges.stream().anyMatch(type::isInstance);
    }

    private boolean stepClosedWith(StepClosureDetails details) {
        return savedChanges.stream().anyMatch(change -> change instanceof DemandChange.StepClosed closed
            && closed.details().equals(details));
    }

    private boolean stepOpened(Long workflowProcessStepId) {
        return savedChanges.stream().anyMatch(change -> change instanceof DemandChange.StepOpened opened
            && opened.workflowProcessStepId().equals(workflowProcessStepId));
    }

    private boolean assignedTo(String assignee, String assignedBy) {
        return savedChanges.stream().anyMatch(change -> switch (change) {
            case DemandChange.StepOpened opened ->
                assignee.equals(opened.assignee()) && assignedBy.equals(opened.assignedBy());
            case DemandChange.Reassigned reassigned ->
                assignee.equals(reassigned.newUsername()) && assignedBy.equals(reassigned.assignedBy());
            default -> false;
        });
    }

    private boolean termsApplied(Money amount, Integer duration) {
        return savedChanges.stream().anyMatch(change -> change instanceof DemandChange.TermsApplied terms
            && terms.amount().equals(amount) && terms.duration().equals(duration));
    }
}
