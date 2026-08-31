package com.credit.bladi.unittest.service.security;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500000;
import static com.credit.bladi.unittest.mock.data.MockContacts.PERSON_SALMA_IDRISSI;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_ATLAS;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.service.borrower.IMortgageBorrowerService;
import com.credit.bladi.service.enterprise.IEnterpriseUserLinkService;
import com.credit.bladi.service.security.ClientDemandAccessGuard;
import com.credit.bladi.service.workflow.engine.IWorkflowEngineService;
import com.credit.bladi.unittest.mock.workflow.MockDemand;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClientDemandAccessGuardTest {
    @Mock
    private IWorkflowEngineService workflowEngineService;

    @Mock
    private IEnterpriseUserLinkService linkService;

    @Mock
    private IMortgageBorrowerService borrowerService;

    private ClientDemandAccessGuard guard;

    @BeforeEach
    void setUp() {
        guard = new ClientDemandAccessGuard(workflowEngineService, linkService, borrowerService);
    }

    @Test
    void mortgageDossierIsOpenToItsBorrower() {
        when(workflowEngineService.getDemand(7L)).thenReturn(MockDemand.mortgage(7L, UUID.randomUUID(), "DEM-7", 2L,
            PERSON_SALMA_IDRISSI, Money.of(AMOUNT_500000), 240, Instant.now(), null, null, null, null, null,
            ProductType.CONVENTIONAL, CspCode.SALA, null, null, 35, false, null));

        guard.assertApprovedForDemand(7L);

        verify(borrowerService).assertCurrentUserIsBorrower(7L);
        verify(linkService, never()).assertCurrentUserApprovedForEnterprise(ArgumentMatchers.any());
    }

    @Test
    void anInvestmentDossierIsOpenToTheLinkedEnterpriseClients() {
        when(workflowEngineService.getDemand(3L)).thenReturn(MockDemand.enterprise(3L, UUID.randomUUID(), "DEM-3",
            1L, LABEL_ATLAS, EnterpriseType.PME, Money.of(AMOUNT_1500000), 84, Instant.now(), null, null,
            null, null, null, null, null, null, 5L, null, null, null));

        guard.assertApprovedForDemand(3L);

        verify(linkService).assertCurrentUserApprovedForEnterprise(5L);
        verify(borrowerService, never()).assertCurrentUserIsBorrower(ArgumentMatchers.any());
    }
}
