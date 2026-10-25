package com.credit.bladi.unittest.controller.client;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_300000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500000;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_PENDING;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_RESPONSABLE;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_C_AT_X_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.PERSON_SALMA_IDRISSI;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0600000000;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DEM_1;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DEM_OLDER;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DEM_RECENT;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_RC_45210;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_TEXTILE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDUPONT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.client.ClientPortfolioController;
import com.credit.bladi.controller.dto.enterprise.request.ClaimEnterpriseLinkRequestDTO;
import com.credit.bladi.controller.dto.enterprise.response.ClientDemandSummaryResponseDTO;
import com.credit.bladi.controller.dto.enterprise.response.ClientPortfolioEnterpriseResponseDTO;
import com.credit.bladi.controller.dto.enterprise.response.EnterpriseUserLinkResponseDTO;
import com.credit.bladi.exception.EnterpriseUserLinkException;
import com.credit.bladi.model.client.EnterpriseModel;
import com.credit.bladi.model.enterprise.EnterpriseUserLinkModel;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.EnterpriseLinkOrigin;
import com.credit.bladi.model.enums.EnterpriseLinkRole;
import com.credit.bladi.model.enums.EnterpriseLinkStatus;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.service.borrower.IMortgageBorrowerService;
import com.credit.bladi.service.client.IEnterpriseService;
import com.credit.bladi.service.enterprise.IEnterpriseUserLinkService;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ClientPortfolioControllerTest {
    private static final UUID ENTERPRISE_PUBLIC_ID = UUID.randomUUID();

    @Mock
    private IEnterpriseUserLinkService linkService;

    @Mock
    private IEnterpriseService enterpriseService;

    @Mock
    private IWorkflowEngineService workflowEngineService;

    @Mock
    private IMortgageBorrowerService borrowerService;

    private ClientPortfolioController controller;

    @BeforeEach
    void setUp() {
        controller = new ClientPortfolioController(linkService, enterpriseService, workflowEngineService,
            borrowerService);
    }

    private static EnterpriseUserLinkModel link(long enterpriseId, EnterpriseLinkStatus status) {
        return new EnterpriseUserLinkModel(10L, UUID.randomUUID(), enterpriseId, null, 5L, null,
            EnterpriseLinkRole.RESPONSABLE,
            status, EnterpriseLinkOrigin.CLIENT_CLAIM, LABEL_RC_45210, USER_JDUPONT, null, null, Instant.now(), null);
    }

    @Test
    void shouldReturn201WhenClaimingEnterprise() {
        ClaimEnterpriseLinkRequestDTO request = new ClaimEnterpriseLinkRequestDTO(LABEL_RC_45210, CODE_RESPONSABLE);
        when(linkService.claimEnterprise(LABEL_RC_45210, EnterpriseLinkRole.RESPONSABLE))
            .thenReturn(link(1L, EnterpriseLinkStatus.PENDING));

        ResponseEntity<EnterpriseUserLinkResponseDTO> result = controller.claimEnterprise(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(CODE_PENDING, result.getBody().status());
    }

    @Test
    void shouldListOnlyMyOwnLinks() {
        when(linkService.listMyLinks()).thenReturn(List.of(link(1L, EnterpriseLinkStatus.APPROVED),
            link(2L, EnterpriseLinkStatus.PENDING)));

        ResponseEntity<List<EnterpriseUserLinkResponseDTO>> result = controller.getMyEnterpriseLinks();

        assertEquals(2, result.getBody().size());
    }

    @Test
    void shouldReturnOnlyApprovedEnterprisesInPortfolio() {
        when(linkService.listMyApprovedLinks()).thenReturn(List.of(link(1L, EnterpriseLinkStatus.APPROVED)));
        when(enterpriseService.getEnterprisesByIds(List.of(1L)))
            .thenReturn(List.of(new EnterpriseModel(1L, UUID.randomUUID(), COMPANY_ATLAS_TEXTILE_SARL, LABEL_TEXTILE,
                LABEL_RC_45210, null, Instant.now())));

        ResponseEntity<List<ClientPortfolioEnterpriseResponseDTO>> result = controller.getMyPortfolio();

        assertEquals(1, result.getBody().size());
        assertEquals(COMPANY_ATLAS_TEXTILE_SARL, result.getBody().get(0).name());
    }

    @Test
    void shouldRejectDemandsListWhenCallerNotLinked() {
        when(enterpriseService.resolveEnterpriseId(ENTERPRISE_PUBLIC_ID)).thenReturn(1L);
        org.mockito.Mockito.doThrow(
            EnterpriseUserLinkException.of(
                EnterpriseUserLinkException.EnterpriseUserLinkExceptionType.ENTERPRISE_NOT_LINKED))
            .when(linkService).assertCurrentUserApprovedForEnterprise(1L);

        org.junit.jupiter.api.Assertions.assertThrows(EnterpriseUserLinkException.class,
            () -> controller.getPortfolioEnterpriseDemands(ENTERPRISE_PUBLIC_ID, 1, 20));
    }

    @Test
    void shouldReturnTrimmedDemandSummariesForLinkedEnterprise() {
        DemandModel demand = MockDemand.enterprise(1L, UUID.randomUUID(), LABEL_DEM_1, 1L, COMPANY_ATLAS_TEXTILE_SARL,
            EnterpriseType.PME, Money.of(AMOUNT_1500000), 84, Instant.now(), null, null, EMAIL_C_AT_X_MA,
            PHONE_0600000000, null, null, null, null, 1L, null, LABEL_TEXTILE, 5);
        CreditPage<DemandModel> page = new CreditPage<>();
        page.setItems(List.of(demand));
        page.setCurrentPage(1);
        page.setSize(20);
        page.setTotal(1);
        when(workflowEngineService.getAllDemands(eq(1), eq(20), eq(ENTERPRISE_PUBLIC_ID), isNull(), isNull(),
            isNull(), isNull())).thenReturn(page);
        when(enterpriseService.resolveEnterpriseId(ENTERPRISE_PUBLIC_ID)).thenReturn(1L);

        ResponseEntity<CreditPage<ClientDemandSummaryResponseDTO>> result =
            controller.getPortfolioEnterpriseDemands(ENTERPRISE_PUBLIC_ID, 1, 20);

        verify(linkService).assertCurrentUserApprovedForEnterprise(1L);
        assertEquals(1, result.getBody().getItems().size());
        assertEquals(LABEL_DEM_1, result.getBody().getItems().get(0).reference());
    }

    @Test
    void shouldListTheBorrowersOwnMortgageDossiersInLinkOrder() {
        DemandModel recent = MockDemand.mortgage(7L, UUID.randomUUID(), LABEL_DEM_RECENT, 2L, PERSON_SALMA_IDRISSI,
            Money.of(AMOUNT_500000), 240, Instant.now(), null, null, null, null, null,
            ProductType.CONVENTIONAL, CspCode.SALA, null, null, 35, false, null);
        DemandModel older = MockDemand.mortgage(3L, UUID.randomUUID(), LABEL_DEM_OLDER, 2L, PERSON_SALMA_IDRISSI,
            Money.of(AMOUNT_300000), 180, Instant.now(), null, null, null, null, null,
            ProductType.MOURABAHA, CspCode.SALA, null, null, 35, false, null);
        when(borrowerService.getMyDemandIds()).thenReturn(List.of(7L, 3L));
        when(workflowEngineService.getDemands(List.of(7L, 3L))).thenReturn(Map.of(3L, older, 7L, recent));

        ResponseEntity<List<ClientDemandSummaryResponseDTO>> result = controller.getMyMortgageDemands();

        assertEquals(List.of(LABEL_DEM_RECENT, LABEL_DEM_OLDER),
            result.getBody().stream().map(ClientDemandSummaryResponseDTO::reference).toList());
    }
}
