package com.credit.bladi.unittest.controller.workflow;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_150;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_2000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_20000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_340000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_3500;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_36000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_3650;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_5000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_600000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_840000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_8500;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_9000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_25;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_60;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_6_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_6_30;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_COMPLETED;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_INITIATION;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_INITIATION_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_KEEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_REAL;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_RIB123;
import static com.credit.bladi.unittest.mock.data.MockCodes.REFERENCE_DEM_ABCDEF12;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_A_AT_BLADI_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_JEAN_AT_EXAMPLE_COM;
import static com.credit.bladi.unittest.mock.data.MockContacts.PERSON_ANALYST_ONE;
import static com.credit.bladi.unittest.mock.data.MockContacts.PERSON_JEAN_DUPONT;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0612345678;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_ATLAS;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CHARGE_D_AFFAIRES;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DOSSIER_COMPLET;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_EN_ATTENTE_DE_PIECE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_INITIATION;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ANALYST1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ANALYST2;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.user.response.UserResponseDTO;
import com.credit.bladi.controller.dto.workflow.request.AddDemandCommentRequestDTO;
import com.credit.bladi.controller.dto.workflow.request.AdvanceDemandRequestDTO;
import com.credit.bladi.controller.dto.workflow.request.AssignDemandRequestDTO;
import com.credit.bladi.controller.dto.workflow.request.ClassifyDemandRequestDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandAmortizationResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandAssignmentHistoryResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandCommentResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandHistoryEntryResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandPipelineStepResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandResponseDTO;
import com.credit.bladi.controller.dto.workflow.response.DemandStepResponseDTO;
import com.credit.bladi.controller.workflow.DemandController;
import com.credit.bladi.model.enums.ClassificationOutcome;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.enums.StepDirection;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.mortgage.FinancialSnapshot;
import com.credit.bladi.model.mortgage.MortgageAmortizationSnapshot;
import com.credit.bladi.model.user.User;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.workflow.DemandAmortizationResult;
import com.credit.bladi.model.workflow.DemandAssignmentHistoryModel;
import com.credit.bladi.model.workflow.DemandCommentModel;
import com.credit.bladi.model.workflow.DemandHistoryEntryModel;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.DemandPipelineStepModel;
import com.credit.bladi.model.workflow.DemandStepModel;
import com.credit.bladi.service.workflow.engine.IDemandTimelineService;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import com.credit.bladi.service.workflow.product.DemandAmortizationProvider;
import com.credit.bladi.service.workflow.product.DemandAmortizationProviderFactory;
import com.credit.bladi.unittest.mock.workflow.MockDemand;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class DemandControllerTest {
    @Mock
    private IWorkflowEngineService workflowEngineService;

    @Mock
    private IDemandTimelineService timelineService;

    @Mock
    private DemandAmortizationProviderFactory amortizationProviderFactory;

    @Mock
    private DemandAmortizationProvider amortizationProvider;

    private DemandController controller;

    private static final UUID DEMAND_PUBLIC_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        controller = new DemandController(workflowEngineService, timelineService, amortizationProviderFactory);
    }

    private static DemandModel mortgageDemand() {
        return MockDemand.mortgage(1L, DEMAND_PUBLIC_ID, REFERENCE_DEM_ABCDEF12, 1L, PERSON_JEAN_DUPONT,
            Money.of(AMOUNT_500000), 240, Instant.now(), null, Rate.of(DECIMAL_5_25),
            EMAIL_JEAN_AT_EXAMPLE_COM, PHONE_0612345678, null, ProductType.CONVENTIONAL, CspCode.SALA,
            Money.of(AMOUNT_600000), Money.of(AMOUNT_20000), 35, false, null);
    }

    @Test
    void shouldReturnPagedDemands() {
        DemandModel demand = mortgageDemand();
        CreditPage<DemandModel> page = new CreditPage<>();
        page.setItems(List.of(demand));
        page.setTotal(1);
        page.setCurrentPage(1);
        page.setSize(20);
        when(workflowEngineService.getAllDemands(1, 20, null, null, null, null, null)).thenReturn(page);
        when(workflowEngineService.getCurrentStepSummaries(List.of(1L))).thenReturn(Map.of());

        ResponseEntity<CreditPage<DemandResponseDTO>> response =
            controller.getAllDemands(1, 20, null, null, null, null, null);

        assertEquals(1, response.getBody().getItems().size());
    }

    @Test
    void shouldConvertMinMaxAmountToMoneyWhenFiltering() {
        UUID enterprisePublicId = UUID.randomUUID();
        CreditPage<DemandModel> page = new CreditPage<>();
        page.setItems(List.of());
        when(workflowEngineService.getAllDemands(1, 20, enterprisePublicId, LABEL_ATLAS, null,
            new Money(AMOUNT_1000), new Money(AMOUNT_9000))).thenReturn(page);
        when(workflowEngineService.getCurrentStepSummaries(List.of())).thenReturn(Map.of());

        controller.getAllDemands(1, 20, enterprisePublicId, LABEL_ATLAS, null, AMOUNT_1000,
            AMOUNT_9000);

        verify(workflowEngineService).getAllDemands(1, 20, enterprisePublicId, LABEL_ATLAS, null,
            new Money(AMOUNT_1000), new Money(AMOUNT_9000));
    }

    @Test
    void shouldResolveThenReturnDemandById() {
        when(workflowEngineService.resolveDemandId(DEMAND_PUBLIC_ID)).thenReturn(1L);
        when(workflowEngineService.getDemand(1L)).thenReturn(mortgageDemand());

        ResponseEntity<DemandResponseDTO> response = controller.getById(DEMAND_PUBLIC_ID);

        assertEquals(REFERENCE_DEM_ABCDEF12, response.getBody().reference());
    }

    @Test
    void shouldReturnCurrentStep() {
        when(workflowEngineService.resolveDemandId(DEMAND_PUBLIC_ID)).thenReturn(1L);
        DemandStepModel step = new DemandStepModel(CODE_INITIATION, LABEL_INITIATION, CODE_INITIATION_STEP,
            LABEL_INITIATION,
            LABEL_CHARGE_D_AFFAIRES, null, null, null, null, "cda1", USER_MANAGER1, Instant.now());
        when(workflowEngineService.getCurrentStep(1L)).thenReturn(step);

        ResponseEntity<DemandStepResponseDTO> response = controller.getCurrentStep(DEMAND_PUBLIC_ID);

        assertEquals(CODE_INITIATION_STEP, response.getBody().stepCode());
    }

    @Test
    void shouldReturnHistory() {
        when(workflowEngineService.resolveDemandId(DEMAND_PUBLIC_ID)).thenReturn(1L);
        DemandHistoryEntryModel entry = new DemandHistoryEntryModel(CODE_INITIATION, LABEL_INITIATION,
            CODE_INITIATION_STEP,
            LABEL_INITIATION, LABEL_CHARGE_D_AFFAIRES, null, null, null, null, "cda1", USER_MANAGER1, Instant.now(),
            Instant.now(), null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null);
        when(timelineService.getHistory(1L)).thenReturn(List.of(entry));

        ResponseEntity<List<DemandHistoryEntryResponseDTO>> response = controller.getHistory(DEMAND_PUBLIC_ID);

        assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldReturnPipeline() {
        when(workflowEngineService.resolveDemandId(DEMAND_PUBLIC_ID)).thenReturn(1L);
        DemandPipelineStepModel step = new DemandPipelineStepModel(CODE_INITIATION, LABEL_INITIATION,
            CODE_INITIATION_STEP,
            LABEL_INITIATION, LABEL_CHARGE_D_AFFAIRES, null, null, null, null, 1, CODE_COMPLETED, Instant.now(),
            Instant.now());
        when(timelineService.getPipeline(1L)).thenReturn(List.of(step));

        ResponseEntity<List<DemandPipelineStepResponseDTO>> response = controller.getPipeline(DEMAND_PUBLIC_ID);

        assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldAdvanceDemandWithKeepOptionsAndAssignee() {
        when(workflowEngineService.resolveDemandId(DEMAND_PUBLIC_ID)).thenReturn(1L);
        AdvanceDemandRequestDTO requestDTO = new AdvanceDemandRequestDTO(null, null, null, null, null, null, null,
            null, null, USER_ANALYST2, CODE_KEEP, null, CODE_KEEP, null, CODE_KEEP, null);
        when(workflowEngineService.advance(eq(1L), any(), eq(USER_ANALYST2))).thenReturn(mortgageDemand());

        ResponseEntity<DemandResponseDTO> response = controller.advance(DEMAND_PUBLIC_ID, requestDTO);

        assertNotNull(response.getBody());
        verify(workflowEngineService).advance(eq(1L), any(), eq(USER_ANALYST2));
    }

    @Test
    void shouldClassifyDemandRealOutcome() {
        UUID enterprisePublicId = UUID.randomUUID();
        when(workflowEngineService.resolveDemandId(DEMAND_PUBLIC_ID)).thenReturn(1L);
        ClassifyDemandRequestDTO requestDTO =
            new ClassifyDemandRequestDTO(CODE_REAL, enterprisePublicId, "cda1", "note", CODE_RIB123);
        when(workflowEngineService.classify(1L, ClassificationOutcome.REAL, enterprisePublicId, "note", "cda1",
            CODE_RIB123)).thenReturn(mortgageDemand());

        ResponseEntity<DemandResponseDTO> response = controller.classify(DEMAND_PUBLIC_ID, requestDTO);

        assertNotNull(response.getBody());
        verify(workflowEngineService).classify(1L, ClassificationOutcome.REAL, enterprisePublicId, "note", "cda1",
            CODE_RIB123);
    }

    @Test
    void shouldDispatchAmortizationScheduleToProductProvider() {
        when(workflowEngineService.resolveDemandId(DEMAND_PUBLIC_ID)).thenReturn(1L);
        DemandModel demand = mortgageDemand();
        when(workflowEngineService.getDemand(1L)).thenReturn(demand);
        when(amortizationProviderFactory.get(DemandType.MORTGAGE)).thenReturn(amortizationProvider);
        FinancialSnapshot conventional = new FinancialSnapshot(
            Money.of(AMOUNT_3500), Money.of(AMOUNT_150), Money.of(AMOUNT_3650),
            Money.of(AMOUNT_340000), Money.of(AMOUNT_36000),
            Money.of(AMOUNT_840000), Money.of(AMOUNT_1000),
            Money.of(AMOUNT_500), Money.of(AMOUNT_2000),
            Money.of(AMOUNT_5000), Money.of(AMOUNT_8500));
        MortgageAmortizationSnapshot snapshot = new MortgageAmortizationSnapshot(ProductType.CONVENTIONAL,
            Rate.of(DECIMAL_5_25), DECIMAL_5_60, conventional, List.of(), null, List.of());
        DemandAmortizationResult result = new DemandAmortizationResult(demand, demand.amount(), demand.duration(),
            snapshot);
        when(amortizationProvider.getAmortizationSchedule(demand, null, null, null)).thenReturn(result);

        ResponseEntity<DemandAmortizationResponseDTO> response =
            controller.getAmortizationSchedule(DEMAND_PUBLIC_ID, null, null, null);

        assertEquals(REFERENCE_DEM_ABCDEF12, response.getBody().reference());
        verify(amortizationProviderFactory).get(DemandType.MORTGAGE);
    }

    @Test
    void shouldOverrideAmountAndRateWhenProvidedToAmortizationSchedule() {
        when(workflowEngineService.resolveDemandId(DEMAND_PUBLIC_ID)).thenReturn(1L);
        DemandModel demand = mortgageDemand();
        when(workflowEngineService.getDemand(1L)).thenReturn(demand);
        when(amortizationProviderFactory.get(DemandType.MORTGAGE)).thenReturn(amortizationProvider);
        FinancialSnapshot conventional = new FinancialSnapshot(
            Money.of(AMOUNT_3500), Money.of(AMOUNT_150), Money.of(AMOUNT_3650),
            Money.of(AMOUNT_340000), Money.of(AMOUNT_36000),
            Money.of(AMOUNT_840000), Money.of(AMOUNT_1000),
            Money.of(AMOUNT_500), Money.of(AMOUNT_2000),
            Money.of(AMOUNT_5000), Money.of(AMOUNT_8500));
        MortgageAmortizationSnapshot snapshot = new MortgageAmortizationSnapshot(ProductType.CONVENTIONAL,
            Rate.of(DECIMAL_6_00), DECIMAL_6_30, conventional, List.of(), null, List.of());
        DemandAmortizationResult result = new DemandAmortizationResult(demand, new Money(AMOUNT_600000),
            180, snapshot);
        when(amortizationProvider.getAmortizationSchedule(demand, new Money(AMOUNT_600000),
            new Rate(DECIMAL_6_00), 180)).thenReturn(result);

        controller.getAmortizationSchedule(DEMAND_PUBLIC_ID, DECIMAL_6_00, AMOUNT_600000,
            180);

        verify(amortizationProvider).getAmortizationSchedule(demand, new Money(AMOUNT_600000),
            new Rate(DECIMAL_6_00), 180);
    }

    @Test
    void shouldAssignDemand() {
        when(workflowEngineService.resolveDemandId(DEMAND_PUBLIC_ID)).thenReturn(1L);
        DemandStepModel step = new DemandStepModel(CODE_INITIATION, LABEL_INITIATION, CODE_INITIATION_STEP,
            LABEL_INITIATION,
            LABEL_CHARGE_D_AFFAIRES, null, null, null, null, USER_ANALYST1, USER_MANAGER1, Instant.now());
        when(workflowEngineService.assign(1L, USER_ANALYST1)).thenReturn(step);

        ResponseEntity<DemandStepResponseDTO> response = controller.assign(DEMAND_PUBLIC_ID,
            new AssignDemandRequestDTO(USER_ANALYST1));

        assertEquals(USER_ANALYST1, response.getBody().assignedUsername());
    }

    @Test
    void shouldReturnAssignmentHistory() {
        when(workflowEngineService.resolveDemandId(DEMAND_PUBLIC_ID)).thenReturn(1L);
        DemandAssignmentHistoryModel entry = new DemandAssignmentHistoryModel(1L, UUID.randomUUID(),
            CODE_INITIATION_STEP, LABEL_INITIATION, null, USER_ANALYST1, USER_MANAGER1, Instant.now());
        when(timelineService.getAssignmentHistory(1L)).thenReturn(List.of(entry));

        ResponseEntity<List<DemandAssignmentHistoryResponseDTO>> response =
            controller.getAssignmentHistory(DEMAND_PUBLIC_ID);

        assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldReturnEligibleAssigneesForGivenDirection() {
        when(workflowEngineService.resolveDemandId(DEMAND_PUBLIC_ID)).thenReturn(1L);
        User user = User.getBuilder().id(2L).publicId(UUID.randomUUID()).username(USER_ANALYST1)
            .fullName(PERSON_ANALYST_ONE).email(EMAIL_A_AT_BLADI_MA).role(UserRole.MANAGER).build();
        when(workflowEngineService.getEligibleAssignees(1L, StepDirection.NEXT)).thenReturn(List.of(user));

        ResponseEntity<List<UserResponseDTO>> response = controller.getEligibleAssignees(DEMAND_PUBLIC_ID,
            StepDirection.NEXT);

        assertEquals(1, response.getBody().size());
        verify(workflowEngineService).getEligibleAssignees(1L, StepDirection.NEXT);
    }

    @Test
    void shouldReturnComments() {
        when(workflowEngineService.resolveDemandId(DEMAND_PUBLIC_ID)).thenReturn(1L);
        DemandCommentModel comment = new DemandCommentModel(1L, UUID.randomUUID(), CODE_INITIATION_STEP,
            LABEL_INITIATION,
            USER_ANALYST1, LABEL_EN_ATTENTE_DE_PIECE, Instant.now());
        when(timelineService.getComments(1L)).thenReturn(List.of(comment));

        ResponseEntity<List<DemandCommentResponseDTO>> response = controller.getComments(DEMAND_PUBLIC_ID);

        assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldAddComment() {
        when(workflowEngineService.resolveDemandId(DEMAND_PUBLIC_ID)).thenReturn(1L);
        DemandCommentModel comment = new DemandCommentModel(1L, UUID.randomUUID(), CODE_INITIATION_STEP,
            LABEL_INITIATION,
            USER_MANAGER1, LABEL_DOSSIER_COMPLET, Instant.now());
        when(timelineService.addComment(1L, LABEL_DOSSIER_COMPLET)).thenReturn(comment);

        ResponseEntity<DemandCommentResponseDTO> response = controller.addComment(DEMAND_PUBLIC_ID,
            new AddDemandCommentRequestDTO(LABEL_DOSSIER_COMPLET));

        assertEquals(LABEL_DOSSIER_COMPLET, response.getBody().comment());
    }
}
